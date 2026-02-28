#include "disk_native.h"
#include <winioctl.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/* ------------------------------------------------------------------ */
/*  Get fragment count for a single file using FSCTL_GET_RETRIEVAL_POINTERS */
/* ------------------------------------------------------------------ */
int dn_get_file_fragments(const wchar_t* filePath) {
    HANDLE hFile = CreateFileW(filePath, 0,
        FILE_SHARE_READ | FILE_SHARE_WRITE | FILE_SHARE_DELETE,
        NULL, OPEN_EXISTING, 0, NULL);
    if (hFile == INVALID_HANDLE_VALUE) return -1;

    STARTING_VCN_INPUT_BUFFER startVcn;
    startVcn.StartingVcn.QuadPart = 0;

    BYTE buffer[64 * 1024];
    DWORD bytesReturned = 0;
    int totalExtents = 0;

    while (1) {
        BOOL ok = DeviceIoControl(hFile, FSCTL_GET_RETRIEVAL_POINTERS,
            &startVcn, sizeof(startVcn),
            buffer, sizeof(buffer),
            &bytesReturned, NULL);

        if (ok || GetLastError() == ERROR_MORE_DATA) {
            RETRIEVAL_POINTERS_BUFFER* rpb = (RETRIEVAL_POINTERS_BUFFER*)buffer;
            totalExtents += rpb->ExtentCount;
            if (ok) break;
            /* Move to next VCN for continuation */
            startVcn.StartingVcn = rpb->Extents[rpb->ExtentCount - 1].NextVcn;
        } else {
            /* FSCTL failed (e.g., compressed/sparse/resident file) */
            if (totalExtents == 0) totalExtents = 1;
            break;
        }
    }

    CloseHandle(hFile);
    return totalExtents;
}

/* ------------------------------------------------------------------ */
/*  Internal: recursive file scan for fragmentation analysis          */
/* ------------------------------------------------------------------ */
typedef struct {
    wchar_t  path[DN_MAX_PATH];
    int      fragments;
    ULONGLONG fileSize;
} FragFileInfo;

static int ffi_compare(const void* a, const void* b) {
    const FragFileInfo* fa = (const FragFileInfo*)a;
    const FragFileInfo* fb = (const FragFileInfo*)b;
    if (fb->fragments != fa->fragments) return fb->fragments - fa->fragments;
    return (fb->fileSize > fa->fileSize) ? 1 : -1;
}

static void scan_for_fragmentation(const wchar_t* dir,
    int* totalFiles, int* fragFiles, int* totalFrags,
    FragFileInfo* topList, int topMax, int* topCount,
    int maxFiles, int* scanned, CancelFlag cf)
{
    if (cf && *cf) return;
    if (*scanned >= maxFiles) return;

    wchar_t pattern[DN_MAX_PATH];
    _snwprintf(pattern, DN_MAX_PATH, L"%s\\*", dir);

    WIN32_FIND_DATAW fd;
    HANDLE hFind = FindFirstFileW(pattern, &fd);
    if (hFind == INVALID_HANDLE_VALUE) return;

    do {
        if (cf && *cf) break;
        if (*scanned >= maxFiles) break;

        if (fd.cFileName[0] == L'.' &&
            (fd.cFileName[1] == L'\0' ||
             (fd.cFileName[1] == L'.' && fd.cFileName[2] == L'\0')))
            continue;

        wchar_t fullPath[DN_MAX_PATH];
        _snwprintf(fullPath, DN_MAX_PATH, L"%s\\%s", dir, fd.cFileName);

        if (fd.dwFileAttributes & FILE_ATTRIBUTE_REPARSE_POINT) continue;

        if (fd.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY) {
            scan_for_fragmentation(fullPath, totalFiles, fragFiles, totalFrags,
                topList, topMax, topCount, maxFiles, scanned, cf);
        } else {
            ULONGLONG fsize = ((ULONGLONG)fd.nFileSizeHigh << 32) | fd.nFileSizeLow;
            if (fsize < 4096) continue; /* Skip tiny files (often MFT-resident) */

            (*totalFiles)++;
            (*scanned)++;

            int frags = dn_get_file_fragments(fullPath);
            if (frags < 1) frags = 1;

            (*totalFrags) += frags;
            if (frags > 1) {
                (*fragFiles)++;

                /* Track top fragmented */
                if (*topCount < topMax) {
                    wcsncpy(topList[*topCount].path, fullPath, DN_MAX_PATH - 1);
                    topList[*topCount].path[DN_MAX_PATH - 1] = L'\0';
                    topList[*topCount].fragments = frags;
                    topList[*topCount].fileSize = fsize;
                    (*topCount)++;
                    if (*topCount == topMax)
                        qsort(topList, *topCount, sizeof(FragFileInfo), ffi_compare);
                } else if (frags > topList[topMax - 1].fragments) {
                    wcsncpy(topList[topMax - 1].path, fullPath, DN_MAX_PATH - 1);
                    topList[topMax - 1].path[DN_MAX_PATH - 1] = L'\0';
                    topList[topMax - 1].fragments = frags;
                    topList[topMax - 1].fileSize = fsize;
                    qsort(topList, topMax, sizeof(FragFileInfo), ffi_compare);
                }
            }
        }
    } while (FindNextFileW(hFind, &fd));

    FindClose(hFind);
}

/* ------------------------------------------------------------------ */
/*  Unicode to UTF-8 helper (for JSON output)                        */
/* ------------------------------------------------------------------ */
static int wchar_to_utf8_frag(const wchar_t* wstr, char* buf, int bufLen) {
    int needed = WideCharToMultiByte(CP_UTF8, 0, wstr, -1, NULL, 0, NULL, NULL);
    if (needed <= 0 || needed > bufLen) return 0;
    WideCharToMultiByte(CP_UTF8, 0, wstr, -1, buf, bufLen, NULL, NULL);
    return needed - 1;
}

/* Escape backslashes and quotes for JSON */
static void json_escape_path(const char* in, char* out, int maxOut) {
    int j = 0;
    for (int i = 0; in[i] && j < maxOut - 2; i++) {
        if (in[i] == '\\' || in[i] == '"') {
            out[j++] = '\\';
        }
        out[j++] = in[i];
    }
    out[j] = '\0';
}

/* ------------------------------------------------------------------ */
/*  Analyze fragmentation of a volume or directory (JSON output)      */
/* ------------------------------------------------------------------ */
int dn_analyze_fragmentation(const wchar_t* path, int maxFiles, char* jsonOut, int maxLen) {
    if (!path || !jsonOut || maxLen < 256) return DN_ERR_INVALID_PARAM;

    int totalFiles = 0, fragFiles = 0, totalFrags = 0;
    int topCount = 0, scanned = 0;
    FragFileInfo topList[50];
    memset(topList, 0, sizeof(topList));

    scan_for_fragmentation(path, &totalFiles, &fragFiles, &totalFrags,
        topList, 50, &topCount, maxFiles > 0 ? maxFiles : 10000, &scanned, NULL);

    /* Sort top list final time */
    if (topCount > 1)
        qsort(topList, topCount, sizeof(FragFileInfo), ffi_compare);

    double fragPct = totalFiles > 0 ? (double)fragFiles * 100.0 / totalFiles : 0.0;
    double avgFrags = totalFiles > 0 ? (double)totalFrags / totalFiles : 1.0;

    /* Build JSON */
    int off = 0;
    off += snprintf(jsonOut + off, maxLen - off,
        "{\"totalFiles\":%d,\"fragmentedFiles\":%d,\"totalFragments\":%d,"
        "\"fragmentationPct\":%.2f,\"avgFragmentsPerFile\":%.2f,\"topFragmented\":[",
        totalFiles, fragFiles, totalFrags, fragPct, avgFrags);

    int limit = topCount > 30 ? 30 : topCount;
    for (int i = 0; i < limit && off < maxLen - 512; i++) {
        char utf8path[DN_MAX_PATH * 3];
        char escaped[DN_MAX_PATH * 3];
        wchar_to_utf8_frag(topList[i].path, utf8path, sizeof(utf8path));
        json_escape_path(utf8path, escaped, sizeof(escaped));

        if (i > 0) off += snprintf(jsonOut + off, maxLen - off, ",");
        off += snprintf(jsonOut + off, maxLen - off,
            "{\"path\":\"%s\",\"fragments\":%d,\"size\":%I64uu}",
            escaped, topList[i].fragments, (unsigned long long)topList[i].fileSize);
    }

    off += snprintf(jsonOut + off, maxLen - off, "]}");
    jsonOut[off] = '\0';
    return DN_OK;
}
