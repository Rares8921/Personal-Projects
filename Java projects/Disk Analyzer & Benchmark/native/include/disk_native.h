#ifndef DISK_NATIVE_H
#define DISK_NATIVE_H

#include <windows.h>

#define DN_MAX_PATH          1024
#define DN_MAX_DRIVES        26
#define DN_MAX_ENTRIES       65536
#define DN_BLOCK_ALIGN       4096
#define DN_JSON_BUF          (1024 * 1024)

#define DN_OK                 0
#define DN_ERR_INVALID_PARAM -1
#define DN_ERR_ACCESS_DENIED -2
#define DN_ERR_NOT_FOUND     -3
#define DN_ERR_IO_FAILURE    -4
#define DN_ERR_ALLOC_FAILURE -5
#define DN_ERR_CANCELLED     -6

typedef struct {
    char     driveLetter;
    char     volumeName[256];
    char     fileSystem[32];
    char     serialNumber[64];
    ULONGLONG totalBytes;
    ULONGLONG freeBytes;
    ULONGLONG usedBytes;
    DWORD    clusterSize;
    DWORD    sectorSize;
    int      driveType;
    int      isSSD;
} DiskInfoNative;

typedef struct {
    double   throughputMBps;
    double   avgLatencyUs;
    double   minLatencyUs;
    double   maxLatencyUs;
    double   iops;
    LONGLONG totalBytesTransferred;
    double   elapsedSeconds;
    int      blockSizeBytes;
    int      operationCount;
} BenchmarkResult;

typedef struct {
    wchar_t  path[DN_MAX_PATH];
    ULONGLONG size;
    int      isDirectory;
    ULONGLONG modifiedTime;
    DWORD    attributes;
} FileEntryNative;

typedef struct {
    ULONGLONG totalPhysical;
    ULONGLONG availablePhysical;
    ULONGLONG totalPageFile;
    ULONGLONG availablePageFile;
    DWORD    memoryLoad;
} MemoryInfoNative;

typedef volatile int* CancelFlag;

int       dn_get_available_drives(char* driveLetters, int maxDrives);
int       dn_get_disk_info(char driveLetter, DiskInfoNative* info);
int       dn_detect_ssd(char driveLetter);

int       dn_benchmark_seq_read(const char* dir, int blockKB, int totalMB, BenchmarkResult* r, CancelFlag cf);
int       dn_benchmark_seq_write(const char* dir, int blockKB, int totalMB, BenchmarkResult* r, CancelFlag cf);
int       dn_benchmark_rand_read(const char* dir, int blockKB, int iters, BenchmarkResult* r, CancelFlag cf);
int       dn_benchmark_rand_write(const char* dir, int blockKB, int iters, BenchmarkResult* r, CancelFlag cf);

LONGLONG  dn_get_directory_size(const wchar_t* path);
int       dn_get_file_count(const wchar_t* path);
int       dn_scan_directory(const wchar_t* path, FileEntryNative* entries, int maxEntries, int* outCount);
int       dn_find_largest_files(const wchar_t* path, FileEntryNative* results, int maxResults, int* outCount);
int       dn_get_extension_stats(const wchar_t* path, char* jsonOut, int maxLen);

int       dn_get_memory_info(MemoryInfoNative* info);

int       dn_full_scan(const wchar_t* path, int maxEntries, int maxLargest,
                      char* jsonOut, int maxJsonLen);

int       dn_get_file_fragments(const wchar_t* filePath);
int       dn_analyze_fragmentation(const wchar_t* path, int maxFiles, char* jsonOut, int maxLen);

#endif
