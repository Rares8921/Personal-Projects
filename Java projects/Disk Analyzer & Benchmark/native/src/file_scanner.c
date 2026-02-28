#include "disk_native.h"
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

typedef struct {
    ULONGLONG size;
    wchar_t   path[DN_MAX_PATH];
} SizedEntry;

/* ===== Context for single-pass full scan ===== */
typedef struct {
    FileEntryNative* entries;
    int entryCount;
    int entryCap;

    SizedEntry* allFiles;
    int allFileCount;
    int allFileCap;

    /* ExtStat is defined further down; forward-declared here */
    void* extStats;
    int extStatCount;
    int extStatCap;

    int totalFileCount;
} FullScanCtx;

static int sized_cmp(const void* a, const void* b) {
    ULONGLONG sa = ((const SizedEntry*)a)->size;
    ULONGLONG sb = ((const SizedEntry*)b)->size;
    if (sb > sa) return 1;
    if (sb < sa) return -1;
    return 0;
}

LONGLONG dn_get_directory_size(const wchar_t* path) {
    if (!path) return -1;

    wchar_t searchPath[DN_MAX_PATH];
    _snwprintf(searchPath, DN_MAX_PATH, L"%s\\*", path);

    WIN32_FIND_DATAW fd;
    HANDLE h = FindFirstFileW(searchPath, &fd);
    if (h == INVALID_HANDLE_VALUE) return -1;

    LONGLONG total = 0;
    do {
        if (wcscmp(fd.cFileName, L".") == 0 || wcscmp(fd.cFileName, L"..") == 0) continue;

        wchar_t full[DN_MAX_PATH];
        _snwprintf(full, DN_MAX_PATH, L"%s\\%s", path, fd.cFileName);

        if (fd.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY) {
            if (!(fd.dwFileAttributes & FILE_ATTRIBUTE_REPARSE_POINT)) {
                LONGLONG sub = dn_get_directory_size(full);
                if (sub > 0) total += sub;
            }
        } else {
            ULARGE_INTEGER sz;
            sz.LowPart  = fd.nFileSizeLow;
            sz.HighPart = fd.nFileSizeHigh;
            total += sz.QuadPart;
        }
    } while (FindNextFileW(h, &fd));

    FindClose(h);
    return total;
}

int dn_get_file_count(const wchar_t* path) {
    if (!path) return -1;

    wchar_t searchPath[DN_MAX_PATH];
    _snwprintf(searchPath, DN_MAX_PATH, L"%s\\*", path);

    WIN32_FIND_DATAW fd;
    HANDLE h = FindFirstFileW(searchPath, &fd);
    if (h == INVALID_HANDLE_VALUE) return -1;

    int count = 0;
    do {
        if (wcscmp(fd.cFileName, L".") == 0 || wcscmp(fd.cFileName, L"..") == 0) continue;

        if (fd.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY) {
            if (!(fd.dwFileAttributes & FILE_ATTRIBUTE_REPARSE_POINT)) {
                wchar_t full[DN_MAX_PATH];
                _snwprintf(full, DN_MAX_PATH, L"%s\\%s", path, fd.cFileName);
                int sub = dn_get_file_count(full);
                if (sub > 0) count += sub;
            }
        } else {
            count++;
        }
    } while (FindNextFileW(h, &fd));

    FindClose(h);
    return count;
}

int dn_scan_directory(const wchar_t* path, FileEntryNative* entries,
                      int maxEntries, int* outCount) {
    if (!path || !entries || !outCount) return DN_ERR_INVALID_PARAM;

    wchar_t searchPath[DN_MAX_PATH];
    _snwprintf(searchPath, DN_MAX_PATH, L"%s\\*", path);

    WIN32_FIND_DATAW fd;
    HANDLE h = FindFirstFileW(searchPath, &fd);
    if (h == INVALID_HANDLE_VALUE) return DN_ERR_NOT_FOUND;

    int idx = *outCount;
    do {
        if (idx >= maxEntries) break;
        if (wcscmp(fd.cFileName, L".") == 0 || wcscmp(fd.cFileName, L"..") == 0) continue;

        FileEntryNative* e = &entries[idx];
        _snwprintf(e->path, DN_MAX_PATH, L"%s\\%s", path, fd.cFileName);
        e->isDirectory  = (fd.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY) ? 1 : 0;
        e->attributes   = fd.dwFileAttributes;

        ULARGE_INTEGER ft;
        ft.LowPart  = fd.ftLastWriteTime.dwLowDateTime;
        ft.HighPart = fd.ftLastWriteTime.dwHighDateTime;
        e->modifiedTime = ft.QuadPart;

        if (e->isDirectory) {
            if (!(fd.dwFileAttributes & FILE_ATTRIBUTE_REPARSE_POINT)) {
                LONGLONG ds = dn_get_directory_size(e->path);
                e->size = (ds > 0) ? (ULONGLONG)ds : 0;
            } else {
                e->size = 0;
            }
        } else {
            ULARGE_INTEGER sz;
            sz.LowPart  = fd.nFileSizeLow;
            sz.HighPart = fd.nFileSizeHigh;
            e->size = sz.QuadPart;
        }
        idx++;
    } while (FindNextFileW(h, &fd));

    FindClose(h);
    *outCount = idx;
    return DN_OK;
}

static void collect_files_recursive(const wchar_t* path,
                                    SizedEntry** list, int* count, int* cap) {
    wchar_t sp[DN_MAX_PATH];
    _snwprintf(sp, DN_MAX_PATH, L"%s\\*", path);

    WIN32_FIND_DATAW fd;
    HANDLE h = FindFirstFileW(sp, &fd);
    if (h == INVALID_HANDLE_VALUE) return;

    do {
        if (wcscmp(fd.cFileName, L".") == 0 || wcscmp(fd.cFileName, L"..") == 0) continue;

        wchar_t full[DN_MAX_PATH];
        _snwprintf(full, DN_MAX_PATH, L"%s\\%s", path, fd.cFileName);

        if (fd.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY) {
            if (!(fd.dwFileAttributes & FILE_ATTRIBUTE_REPARSE_POINT))
                collect_files_recursive(full, list, count, cap);
        } else {
            if (*count >= *cap) {
                int newCap = (*cap) * 2;
                SizedEntry* newList = (SizedEntry*)realloc(*list, newCap * sizeof(SizedEntry));
                if (!newList) continue;
                *list = newList;
                *cap  = newCap;
            }
            ULARGE_INTEGER sz;
            sz.LowPart  = fd.nFileSizeLow;
            sz.HighPart = fd.nFileSizeHigh;
            (*list)[*count].size = sz.QuadPart;
            wcsncpy((*list)[*count].path, full, DN_MAX_PATH - 1);
            (*count)++;
        }
    } while (FindNextFileW(h, &fd));

    FindClose(h);
}

int dn_find_largest_files(const wchar_t* path, FileEntryNative* results,
                          int maxResults, int* outCount) {
    if (!path || !results || !outCount) return DN_ERR_INVALID_PARAM;

    int cap = 4096;
    int count = 0;
    SizedEntry* list = (SizedEntry*)malloc(cap * sizeof(SizedEntry));
    if (!list) return DN_ERR_ALLOC_FAILURE;

    collect_files_recursive(path, &list, &count, &cap);

    if (count > 1) qsort(list, count, sizeof(SizedEntry), sized_cmp);

    int n = count < maxResults ? count : maxResults;
    for (int i = 0; i < n; i++) {
        wcsncpy(results[i].path, list[i].path, DN_MAX_PATH - 1);
        results[i].size        = list[i].size;
        results[i].isDirectory = 0;
        results[i].attributes  = 0;
        results[i].modifiedTime = 0;
    }
    *outCount = n;

    free(list);
    return DN_OK;
}

typedef struct {
    char ext[32];
    LONGLONG totalSize;
    int      fileCount;
} ExtStat;

static int extstat_cmp(const void* a, const void* b) {
    LONGLONG sa = ((const ExtStat*)a)->totalSize;
    LONGLONG sb = ((const ExtStat*)b)->totalSize;
    if (sb > sa) return 1;
    if (sb < sa) return -1;
    return 0;
}

static void collect_ext_recursive(const wchar_t* path,
                                  ExtStat** stats, int* count, int* cap) {
    wchar_t sp[DN_MAX_PATH];
    _snwprintf(sp, DN_MAX_PATH, L"%s\\*", path);

    WIN32_FIND_DATAW fd;
    HANDLE h = FindFirstFileW(sp, &fd);
    if (h == INVALID_HANDLE_VALUE) return;

    do {
        if (wcscmp(fd.cFileName, L".") == 0 || wcscmp(fd.cFileName, L"..") == 0) continue;

        wchar_t full[DN_MAX_PATH];
        _snwprintf(full, DN_MAX_PATH, L"%s\\%s", path, fd.cFileName);

        if (fd.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY) {
            if (!(fd.dwFileAttributes & FILE_ATTRIBUTE_REPARSE_POINT))
                collect_ext_recursive(full, stats, count, cap);
        } else {
            ULARGE_INTEGER sz;
            sz.LowPart  = fd.nFileSizeLow;
            sz.HighPart = fd.nFileSizeHigh;

            const wchar_t* dot = wcsrchr(fd.cFileName, L'.');
            char ext[32] = "(none)";
            if (dot && wcslen(dot) < 30) {
                int j;
                for (j = 0; dot[j] && j < 31; j++)
                    ext[j] = (char)dot[j];
                ext[j] = '\0';
                for (j = 0; ext[j]; j++)
                    if (ext[j] >= 'A' && ext[j] <= 'Z') ext[j] += 32;
            }

            int found = -1;
            for (int k = 0; k < *count; k++) {
                if (strcmp((*stats)[k].ext, ext) == 0) { found = k; break; }
            }

            if (found >= 0) {
                (*stats)[found].totalSize += sz.QuadPart;
                (*stats)[found].fileCount++;
            } else {
                if (*count >= *cap) {
                    int nc = (*cap) * 2;
                    ExtStat* ns = (ExtStat*)realloc(*stats, nc * sizeof(ExtStat));
                    if (!ns) continue;
                    *stats = ns;
                    *cap = nc;
                }
                strncpy((*stats)[*count].ext, ext, 31);
                (*stats)[*count].totalSize = sz.QuadPart;
                (*stats)[*count].fileCount = 1;
                (*count)++;
            }
        }
    } while (FindNextFileW(h, &fd));

    FindClose(h);
}

int dn_get_extension_stats(const wchar_t* path, char* jsonOut, int maxLen) {
    if (!path || !jsonOut) return DN_ERR_INVALID_PARAM;

    int cap = 256;
    int count = 0;
    ExtStat* stats = (ExtStat*)malloc(cap * sizeof(ExtStat));
    if (!stats) return DN_ERR_ALLOC_FAILURE;

    collect_ext_recursive(path, &stats, &count, &cap);

    if (count > 1) qsort(stats, count, sizeof(ExtStat), extstat_cmp);

    int pos = 0;
    pos += _snprintf(jsonOut + pos, maxLen - pos, "[");
    int limit = count < 50 ? count : 50;
    for (int i = 0; i < limit && pos < maxLen - 64; i++) {
        if (i > 0) pos += _snprintf(jsonOut + pos, maxLen - pos, ",");
        pos += _snprintf(jsonOut + pos, maxLen - pos,
                         "{\"ext\":\"%s\",\"size\":%lld,\"count\":%d}",
                         stats[i].ext, (long long)stats[i].totalSize, stats[i].fileCount);
    }
    pos += _snprintf(jsonOut + pos, maxLen - pos, "]");

    free(stats);
    return DN_OK;
}

/* ===================================================================
 *  dn_full_scan  –  Single-pass recursive scan
 *
 *  Collects in ONE traversal:
 *    - Immediate children of `path` (with accurate directory sizes)
 *    - Top-N largest files across the whole subtree
 *    - Extension statistics
 *    - Total size and file count
 *
 *  Output is a composite JSON blob consumed by the Java layer.
 * =================================================================== */

static void fs_add_ext(FullScanCtx* ctx, const wchar_t* fileName, ULONGLONG fileSize) {
    ExtStat* stats = (ExtStat*)ctx->extStats;

    const wchar_t* dot = wcsrchr(fileName, L'.');
    char ext[32] = "(none)";
    if (dot && wcslen(dot) < 30) {
        int j;
        for (j = 0; dot[j] && j < 31; j++)
            ext[j] = (char)dot[j];
        ext[j] = '\0';
        for (j = 0; ext[j]; j++)
            if (ext[j] >= 'A' && ext[j] <= 'Z') ext[j] += 32;
    }

    int found = -1;
    for (int k = 0; k < ctx->extStatCount; k++) {
        if (strcmp(stats[k].ext, ext) == 0) { found = k; break; }
    }

    if (found >= 0) {
        stats[found].totalSize += fileSize;
        stats[found].fileCount++;
    } else {
        if (ctx->extStatCount >= ctx->extStatCap) {
            int nc = ctx->extStatCap * 2;
            ExtStat* ns = (ExtStat*)realloc(stats, nc * sizeof(ExtStat));
            if (!ns) return;
            ctx->extStats = ns;
            stats = ns;
            ctx->extStatCap = nc;
        }
        strncpy(stats[ctx->extStatCount].ext, ext, 31);
        stats[ctx->extStatCount].ext[31] = '\0';
        stats[ctx->extStatCount].totalSize = fileSize;
        stats[ctx->extStatCount].fileCount = 1;
        ctx->extStatCount++;
    }
}

static void fs_add_file(FullScanCtx* ctx, const wchar_t* fullPath, ULONGLONG size) {
    if (ctx->allFileCount >= ctx->allFileCap) {
        int nc = ctx->allFileCap * 2;
        SizedEntry* ns = (SizedEntry*)realloc(ctx->allFiles, nc * sizeof(SizedEntry));
        if (!ns) return;
        ctx->allFiles = ns;
        ctx->allFileCap = nc;
    }
    ctx->allFiles[ctx->allFileCount].size = size;
    wcsncpy(ctx->allFiles[ctx->allFileCount].path, fullPath, DN_MAX_PATH - 1);
    ctx->allFiles[ctx->allFileCount].path[DN_MAX_PATH - 1] = L'\0';
    ctx->allFileCount++;
}

/* Returns total size of the subtree rooted at `path`.
   depth==0 means we are scanning the user-requested root directory. */
static LONGLONG full_scan_recurse(const wchar_t* path, FullScanCtx* ctx, int depth) {
    wchar_t sp[DN_MAX_PATH];
    _snwprintf(sp, DN_MAX_PATH, L"%s\\*", path);

    WIN32_FIND_DATAW fd;
    HANDLE h = FindFirstFileW(sp, &fd);
    if (h == INVALID_HANDLE_VALUE) return 0;

    LONGLONG total = 0;

    do {
        if (wcscmp(fd.cFileName, L".") == 0 || wcscmp(fd.cFileName, L"..") == 0) continue;

        wchar_t full[DN_MAX_PATH];
        _snwprintf(full, DN_MAX_PATH, L"%s\\%s", path, fd.cFileName);

        if (fd.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY) {
            if (fd.dwFileAttributes & FILE_ATTRIBUTE_REPARSE_POINT) continue;

            LONGLONG subSize = full_scan_recurse(full, ctx, depth + 1);
            total += subSize;

            /* Record as an immediate child entry */
            if (depth == 0 && ctx->entryCount < ctx->entryCap) {
                FileEntryNative* e = &ctx->entries[ctx->entryCount];
                wcsncpy(e->path, full, DN_MAX_PATH - 1);
                e->path[DN_MAX_PATH - 1] = L'\0';
                e->size = (ULONGLONG)(subSize > 0 ? subSize : 0);
                e->isDirectory = 1;
                e->attributes = fd.dwFileAttributes;
                ULARGE_INTEGER ft;
                ft.LowPart  = fd.ftLastWriteTime.dwLowDateTime;
                ft.HighPart = fd.ftLastWriteTime.dwHighDateTime;
                e->modifiedTime = ft.QuadPart;
                ctx->entryCount++;
            }
        } else {
            ULARGE_INTEGER sz;
            sz.LowPart  = fd.nFileSizeLow;
            sz.HighPart = fd.nFileSizeHigh;
            ULONGLONG fileSize = sz.QuadPart;
            total += fileSize;
            ctx->totalFileCount++;

            /* Record as an immediate child entry */
            if (depth == 0 && ctx->entryCount < ctx->entryCap) {
                FileEntryNative* e = &ctx->entries[ctx->entryCount];
                wcsncpy(e->path, full, DN_MAX_PATH - 1);
                e->path[DN_MAX_PATH - 1] = L'\0';
                e->size = fileSize;
                e->isDirectory = 0;
                e->attributes = fd.dwFileAttributes;
                ULARGE_INTEGER ft;
                ft.LowPart  = fd.ftLastWriteTime.dwLowDateTime;
                ft.HighPart = fd.ftLastWriteTime.dwHighDateTime;
                e->modifiedTime = ft.QuadPart;
                ctx->entryCount++;
            }

            /* Collect for largest-files and extension stats */
            fs_add_file(ctx, full, fileSize);
            fs_add_ext(ctx, fd.cFileName, fileSize);
        }
    } while (FindNextFileW(h, &fd));

    FindClose(h);
    return total;
}

int dn_full_scan(const wchar_t* path, int maxEntries, int maxLargest,
                 char* jsonOut, int maxJsonLen) {
    if (!path || !jsonOut) return DN_ERR_INVALID_PARAM;

    FullScanCtx ctx;
    memset(&ctx, 0, sizeof(ctx));

    ctx.entryCap = maxEntries;
    ctx.entries  = (FileEntryNative*)calloc(maxEntries, sizeof(FileEntryNative));
    if (!ctx.entries) return DN_ERR_ALLOC_FAILURE;

    ctx.allFileCap = 8192;
    ctx.allFiles   = (SizedEntry*)malloc(ctx.allFileCap * sizeof(SizedEntry));
    if (!ctx.allFiles) { free(ctx.entries); return DN_ERR_ALLOC_FAILURE; }

    ctx.extStatCap = 256;
    ctx.extStats   = malloc(ctx.extStatCap * sizeof(ExtStat));
    if (!ctx.extStats) { free(ctx.entries); free(ctx.allFiles); return DN_ERR_ALLOC_FAILURE; }

    /* ---------- single recursive walk ---------- */
    LONGLONG totalSize = full_scan_recurse(path, &ctx, 0);

    /* Sort largest files descending */
    if (ctx.allFileCount > 1)
        qsort(ctx.allFiles, ctx.allFileCount, sizeof(SizedEntry), sized_cmp);

    /* Sort ext stats descending by size */
    ExtStat* extArr = (ExtStat*)ctx.extStats;
    if (ctx.extStatCount > 1)
        qsort(extArr, ctx.extStatCount, sizeof(ExtStat), extstat_cmp);

    /* ---------- Build composite JSON ---------- */
    int pos = 0;
    pos += _snprintf(jsonOut + pos, maxJsonLen - pos, "{\"entries\":[");

    for (int i = 0; i < ctx.entryCount && pos < maxJsonLen - 512; i++) {
        if (i > 0) pos += _snprintf(jsonOut + pos, maxJsonLen - pos, ",");

        char pathUtf8[DN_MAX_PATH * 3];
        WideCharToMultiByte(CP_UTF8, 0, ctx.entries[i].path, -1,
                            pathUtf8, sizeof(pathUtf8), NULL, NULL);
        for (int j = 0; pathUtf8[j]; j++)
            if (pathUtf8[j] == '\\') pathUtf8[j] = '/';

        pos += _snprintf(jsonOut + pos, maxJsonLen - pos,
            "{\"path\":\"%s\",\"size\":%llu,\"dir\":%s,\"modified\":%llu}",
            pathUtf8, (unsigned long long)ctx.entries[i].size,
            ctx.entries[i].isDirectory ? "true" : "false",
            (unsigned long long)ctx.entries[i].modifiedTime);
    }

    pos += _snprintf(jsonOut + pos, maxJsonLen - pos, "],\"largest\":[");

    int largestN = ctx.allFileCount < maxLargest ? ctx.allFileCount : maxLargest;
    for (int i = 0; i < largestN && pos < maxJsonLen - 512; i++) {
        if (i > 0) pos += _snprintf(jsonOut + pos, maxJsonLen - pos, ",");

        char pathUtf8[DN_MAX_PATH * 3];
        WideCharToMultiByte(CP_UTF8, 0, ctx.allFiles[i].path, -1,
                            pathUtf8, sizeof(pathUtf8), NULL, NULL);
        for (int j = 0; pathUtf8[j]; j++)
            if (pathUtf8[j] == '\\') pathUtf8[j] = '/';

        pos += _snprintf(jsonOut + pos, maxJsonLen - pos,
            "{\"path\":\"%s\",\"size\":%llu}",
            pathUtf8, (unsigned long long)ctx.allFiles[i].size);
    }

    pos += _snprintf(jsonOut + pos, maxJsonLen - pos, "],\"extStats\":[");

    int extLimit = ctx.extStatCount < 50 ? ctx.extStatCount : 50;
    for (int i = 0; i < extLimit && pos < maxJsonLen - 256; i++) {
        if (i > 0) pos += _snprintf(jsonOut + pos, maxJsonLen - pos, ",");
        pos += _snprintf(jsonOut + pos, maxJsonLen - pos,
            "{\"ext\":\"%s\",\"size\":%lld,\"count\":%d}",
            extArr[i].ext, (long long)extArr[i].totalSize, extArr[i].fileCount);
    }

    pos += _snprintf(jsonOut + pos, maxJsonLen - pos,
        "],\"totalSize\":%lld,\"fileCount\":%d}",
        (long long)totalSize, ctx.totalFileCount);

    free(ctx.entries);
    free(ctx.allFiles);
    free(ctx.extStats);
    return DN_OK;
}
