#include "disk_native.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>

static LARGE_INTEGER g_freq;
static int g_freqInit = 0;

static void init_freq(void) {
    if (!g_freqInit) {
        QueryPerformanceFrequency(&g_freq);
        g_freqInit = 1;
    }
}

static double elapsed_us(LARGE_INTEGER s, LARGE_INTEGER e) {
    return (double)(e.QuadPart - s.QuadPart) * 1000000.0 / (double)g_freq.QuadPart;
}

static int align_up(int val, int align) {
    return (val + align - 1) & ~(align - 1);
}

static char* bench_path(const char* dir, const char* tag) {
    static char buf[DN_MAX_PATH];
    _snprintf(buf, DN_MAX_PATH, "%s\\_diskanalyzer_%s.tmp", dir, tag);
    return buf;
}

static void fill_pattern(char* buf, int size) {
    unsigned int seed = (unsigned int)GetTickCount();
    for (int i = 0; i < size; i += 4) {
        seed = seed * 1103515245 + 12345;
        if (i + 4 <= size) memcpy(buf + i, &seed, 4);
    }
}

int dn_benchmark_seq_write(const char* dir, int blockKB, int totalMB,
                           BenchmarkResult* r, CancelFlag cf) {
    if (!dir || !r) return DN_ERR_INVALID_PARAM;
    init_freq();
    memset(r, 0, sizeof(BenchmarkResult));

    int blockSize = align_up(blockKB * 1024, DN_BLOCK_ALIGN);
    LONGLONG totalSize = (LONGLONG)totalMB * 1024 * 1024;
    int iters = (int)(totalSize / blockSize);
    if (iters < 1) iters = 1;
    r->blockSizeBytes = blockSize;

    char* path = bench_path(dir, "seqwr");
    HANDLE h = CreateFileA(path, GENERIC_WRITE, 0, NULL, CREATE_ALWAYS,
                           FILE_FLAG_NO_BUFFERING | FILE_FLAG_WRITE_THROUGH, NULL);
    if (h == INVALID_HANDLE_VALUE) return DN_ERR_IO_FAILURE;

    char* buf = (char*)VirtualAlloc(NULL, blockSize, MEM_COMMIT | MEM_RESERVE, PAGE_READWRITE);
    if (!buf) { CloseHandle(h); DeleteFileA(path); return DN_ERR_ALLOC_FAILURE; }
    fill_pattern(buf, blockSize);

    double minLat = 1e15, maxLat = 0, totalLat = 0;
    LARGE_INTEGER tStart, tEnd;
    QueryPerformanceCounter(&tStart);

    for (int i = 0; i < iters; i++) {
        if (cf && *cf) { VirtualFree(buf, 0, MEM_RELEASE); CloseHandle(h); DeleteFileA(path); return DN_ERR_CANCELLED; }
        LARGE_INTEGER os, oe;
        DWORD written;
        QueryPerformanceCounter(&os);
        if (!WriteFile(h, buf, blockSize, &written, NULL)) break;
        QueryPerformanceCounter(&oe);
        double lat = elapsed_us(os, oe);
        totalLat += lat;
        if (lat < minLat) minLat = lat;
        if (lat > maxLat) maxLat = lat;
    }

    QueryPerformanceCounter(&tEnd);
    double elapsed = elapsed_us(tStart, tEnd);

    r->elapsedSeconds         = elapsed / 1e6;
    r->totalBytesTransferred  = (LONGLONG)iters * blockSize;
    r->throughputMBps         = (r->totalBytesTransferred / (1024.0 * 1024.0)) / r->elapsedSeconds;
    r->avgLatencyUs           = totalLat / iters;
    r->minLatencyUs           = minLat;
    r->maxLatencyUs           = maxLat;
    r->iops                   = iters / r->elapsedSeconds;
    r->operationCount         = iters;

    VirtualFree(buf, 0, MEM_RELEASE);
    CloseHandle(h);
    DeleteFileA(path);
    return DN_OK;
}

int dn_benchmark_seq_read(const char* dir, int blockKB, int totalMB,
                          BenchmarkResult* r, CancelFlag cf) {
    if (!dir || !r) return DN_ERR_INVALID_PARAM;
    init_freq();
    memset(r, 0, sizeof(BenchmarkResult));

    int blockSize = align_up(blockKB * 1024, DN_BLOCK_ALIGN);
    LONGLONG totalSize = (LONGLONG)totalMB * 1024 * 1024;
    int iters = (int)(totalSize / blockSize);
    if (iters < 1) iters = 1;
    r->blockSizeBytes = blockSize;

    char* path = bench_path(dir, "seqrd");

    HANDLE hw = CreateFileA(path, GENERIC_WRITE, 0, NULL, CREATE_ALWAYS,
                            FILE_FLAG_NO_BUFFERING | FILE_FLAG_WRITE_THROUGH, NULL);
    if (hw == INVALID_HANDLE_VALUE) return DN_ERR_IO_FAILURE;

    char* buf = (char*)VirtualAlloc(NULL, blockSize, MEM_COMMIT | MEM_RESERVE, PAGE_READWRITE);
    if (!buf) { CloseHandle(hw); DeleteFileA(path); return DN_ERR_ALLOC_FAILURE; }
    fill_pattern(buf, blockSize);

    DWORD bw;
    for (int i = 0; i < iters; i++) {
        if (cf && *cf) { VirtualFree(buf, 0, MEM_RELEASE); CloseHandle(hw); DeleteFileA(path); return DN_ERR_CANCELLED; }
        if (!WriteFile(hw, buf, blockSize, &bw, NULL)) break;
    }
    CloseHandle(hw);

    HANDLE hr2 = CreateFileA(path, GENERIC_READ, 0, NULL, OPEN_EXISTING,
                             FILE_FLAG_NO_BUFFERING | FILE_FLAG_SEQUENTIAL_SCAN, NULL);
    if (hr2 == INVALID_HANDLE_VALUE) { VirtualFree(buf, 0, MEM_RELEASE); DeleteFileA(path); return DN_ERR_IO_FAILURE; }

    double minLat = 1e15, maxLat = 0, totalLat = 0;
    LARGE_INTEGER tStart, tEnd;
    QueryPerformanceCounter(&tStart);

    for (int i = 0; i < iters; i++) {
        if (cf && *cf) { VirtualFree(buf, 0, MEM_RELEASE); CloseHandle(hr2); DeleteFileA(path); return DN_ERR_CANCELLED; }
        LARGE_INTEGER os, oe;
        DWORD br;
        QueryPerformanceCounter(&os);
        if (!ReadFile(hr2, buf, blockSize, &br, NULL)) break;
        QueryPerformanceCounter(&oe);
        double lat = elapsed_us(os, oe);
        totalLat += lat;
        if (lat < minLat) minLat = lat;
        if (lat > maxLat) maxLat = lat;
    }

    QueryPerformanceCounter(&tEnd);
    double elapsed = elapsed_us(tStart, tEnd);

    r->elapsedSeconds         = elapsed / 1e6;
    r->totalBytesTransferred  = (LONGLONG)iters * blockSize;
    r->throughputMBps         = (r->totalBytesTransferred / (1024.0 * 1024.0)) / r->elapsedSeconds;
    r->avgLatencyUs           = totalLat / iters;
    r->minLatencyUs           = minLat;
    r->maxLatencyUs           = maxLat;
    r->iops                   = iters / r->elapsedSeconds;
    r->operationCount         = iters;

    VirtualFree(buf, 0, MEM_RELEASE);
    CloseHandle(hr2);
    DeleteFileA(path);
    return DN_OK;
}

int dn_benchmark_rand_read(const char* dir, int blockKB, int iters,
                           BenchmarkResult* r, CancelFlag cf) {
    if (!dir || !r) return DN_ERR_INVALID_PARAM;
    init_freq();
    memset(r, 0, sizeof(BenchmarkResult));

    int blockSize = align_up(blockKB * 1024, DN_BLOCK_ALIGN);
    r->blockSizeBytes = blockSize;

    LONGLONG fileSize = (LONGLONG)blockSize * iters;
    if (fileSize < 64LL * 1024 * 1024) fileSize = 64LL * 1024 * 1024;
    int totalBlocks = (int)(fileSize / blockSize);

    char* path = bench_path(dir, "rndrd");

    HANDLE hw = CreateFileA(path, GENERIC_WRITE, 0, NULL, CREATE_ALWAYS,
                            FILE_FLAG_NO_BUFFERING | FILE_FLAG_WRITE_THROUGH, NULL);
    if (hw == INVALID_HANDLE_VALUE) return DN_ERR_IO_FAILURE;

    char* buf = (char*)VirtualAlloc(NULL, blockSize, MEM_COMMIT | MEM_RESERVE, PAGE_READWRITE);
    if (!buf) { CloseHandle(hw); DeleteFileA(path); return DN_ERR_ALLOC_FAILURE; }
    fill_pattern(buf, blockSize);

    DWORD bw;
    for (int i = 0; i < totalBlocks; i++) {
        if (cf && *cf) { VirtualFree(buf, 0, MEM_RELEASE); CloseHandle(hw); DeleteFileA(path); return DN_ERR_CANCELLED; }
        if (!WriteFile(hw, buf, blockSize, &bw, NULL)) break;
    }
    CloseHandle(hw);

    HANDLE hr2 = CreateFileA(path, GENERIC_READ, 0, NULL, OPEN_EXISTING,
                             FILE_FLAG_NO_BUFFERING | FILE_FLAG_RANDOM_ACCESS, NULL);
    if (hr2 == INVALID_HANDLE_VALUE) { VirtualFree(buf, 0, MEM_RELEASE); DeleteFileA(path); return DN_ERR_IO_FAILURE; }

    srand((unsigned)GetTickCount());
    double minLat = 1e15, maxLat = 0, totalLat = 0;
    LARGE_INTEGER tStart, tEnd;
    QueryPerformanceCounter(&tStart);

    for (int i = 0; i < iters; i++) {
        if (cf && *cf) { VirtualFree(buf, 0, MEM_RELEASE); CloseHandle(hr2); DeleteFileA(path); return DN_ERR_CANCELLED; }
        LONGLONG offset = (LONGLONG)(rand() % totalBlocks) * blockSize;
        LARGE_INTEGER li;
        li.QuadPart = offset;
        SetFilePointerEx(hr2, li, NULL, FILE_BEGIN);

        LARGE_INTEGER os, oe;
        DWORD br;
        QueryPerformanceCounter(&os);
        ReadFile(hr2, buf, blockSize, &br, NULL);
        QueryPerformanceCounter(&oe);

        double lat = elapsed_us(os, oe);
        totalLat += lat;
        if (lat < minLat) minLat = lat;
        if (lat > maxLat) maxLat = lat;
    }

    QueryPerformanceCounter(&tEnd);
    double elapsed = elapsed_us(tStart, tEnd);

    r->elapsedSeconds         = elapsed / 1e6;
    r->totalBytesTransferred  = (LONGLONG)iters * blockSize;
    r->throughputMBps         = (r->totalBytesTransferred / (1024.0 * 1024.0)) / r->elapsedSeconds;
    r->avgLatencyUs           = totalLat / iters;
    r->minLatencyUs           = minLat;
    r->maxLatencyUs           = maxLat;
    r->iops                   = iters / r->elapsedSeconds;
    r->operationCount         = iters;

    VirtualFree(buf, 0, MEM_RELEASE);
    CloseHandle(hr2);
    DeleteFileA(path);
    return DN_OK;
}

int dn_benchmark_rand_write(const char* dir, int blockKB, int iters,
                            BenchmarkResult* r, CancelFlag cf) {
    if (!dir || !r) return DN_ERR_INVALID_PARAM;
    init_freq();
    memset(r, 0, sizeof(BenchmarkResult));

    int blockSize = align_up(blockKB * 1024, DN_BLOCK_ALIGN);
    r->blockSizeBytes = blockSize;

    LONGLONG fileSize = (LONGLONG)blockSize * iters;
    if (fileSize < 64LL * 1024 * 1024) fileSize = 64LL * 1024 * 1024;
    int totalBlocks = (int)(fileSize / blockSize);

    char* path = bench_path(dir, "rndwr");

    HANDLE hw = CreateFileA(path, GENERIC_WRITE, 0, NULL, CREATE_ALWAYS,
                            FILE_FLAG_NO_BUFFERING | FILE_FLAG_WRITE_THROUGH, NULL);
    if (hw == INVALID_HANDLE_VALUE) return DN_ERR_IO_FAILURE;

    char* buf = (char*)VirtualAlloc(NULL, blockSize, MEM_COMMIT | MEM_RESERVE, PAGE_READWRITE);
    if (!buf) { CloseHandle(hw); DeleteFileA(path); return DN_ERR_ALLOC_FAILURE; }
    fill_pattern(buf, blockSize);

    DWORD bw;
    for (int i = 0; i < totalBlocks; i++) {
        if (cf && *cf) { VirtualFree(buf, 0, MEM_RELEASE); CloseHandle(hw); DeleteFileA(path); return DN_ERR_CANCELLED; }
        if (!WriteFile(hw, buf, blockSize, &bw, NULL)) break;
    }
    CloseHandle(hw);

    HANDLE hf = CreateFileA(path, GENERIC_WRITE, 0, NULL, OPEN_EXISTING,
                            FILE_FLAG_NO_BUFFERING | FILE_FLAG_WRITE_THROUGH | FILE_FLAG_RANDOM_ACCESS, NULL);
    if (hf == INVALID_HANDLE_VALUE) { VirtualFree(buf, 0, MEM_RELEASE); DeleteFileA(path); return DN_ERR_IO_FAILURE; }

    srand((unsigned)GetTickCount());
    double minLat = 1e15, maxLat = 0, totalLat = 0;
    LARGE_INTEGER tStart, tEnd;
    QueryPerformanceCounter(&tStart);

    for (int i = 0; i < iters; i++) {
        if (cf && *cf) { VirtualFree(buf, 0, MEM_RELEASE); CloseHandle(hf); DeleteFileA(path); return DN_ERR_CANCELLED; }
        fill_pattern(buf, blockSize);
        LONGLONG offset = (LONGLONG)(rand() % totalBlocks) * blockSize;
        LARGE_INTEGER li;
        li.QuadPart = offset;
        SetFilePointerEx(hf, li, NULL, FILE_BEGIN);

        LARGE_INTEGER os, oe;
        QueryPerformanceCounter(&os);
        WriteFile(hf, buf, blockSize, &bw, NULL);
        QueryPerformanceCounter(&oe);

        double lat = elapsed_us(os, oe);
        totalLat += lat;
        if (lat < minLat) minLat = lat;
        if (lat > maxLat) maxLat = lat;
    }

    QueryPerformanceCounter(&tEnd);
    double elapsed = elapsed_us(tStart, tEnd);

    r->elapsedSeconds         = elapsed / 1e6;
    r->totalBytesTransferred  = (LONGLONG)iters * blockSize;
    r->throughputMBps         = (r->totalBytesTransferred / (1024.0 * 1024.0)) / r->elapsedSeconds;
    r->avgLatencyUs           = totalLat / iters;
    r->minLatencyUs           = minLat;
    r->maxLatencyUs           = maxLat;
    r->iops                   = iters / r->elapsedSeconds;
    r->operationCount         = iters;

    VirtualFree(buf, 0, MEM_RELEASE);
    CloseHandle(hf);
    DeleteFileA(path);
    return DN_OK;
}
