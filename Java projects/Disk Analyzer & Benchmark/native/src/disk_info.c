#include "disk_native.h"
#include <stdio.h>
#include <string.h>

#ifndef IOCTL_STORAGE_QUERY_PROPERTY
#define IOCTL_STORAGE_QUERY_PROPERTY 0x002D1400
#endif

#pragma pack(push, 1)
typedef struct {
    DWORD PropertyId;
    DWORD QueryType;
    BYTE  AdditionalParameters[1];
} StoragePropQuery;

typedef struct {
    DWORD Version;
    DWORD Size;
    DWORD IncursSeekPenalty;
} SeekPenaltyDesc;

typedef struct {
    DWORD Version;
    DWORD Size;
    DWORD TrimEnabled;
} TrimDesc;
#pragma pack(pop)

#define SPID_SEEK_PENALTY 7
#define SPID_TRIM         8

int dn_get_available_drives(char* driveLetters, int maxDrives) {
    DWORD mask = GetLogicalDrives();
    int count = 0;
    for (int i = 0; i < 26 && count < maxDrives; i++) {
        if (mask & (1u << i)) {
            driveLetters[count++] = (char)('A' + i);
        }
    }
    return count;
}

int dn_get_disk_info(char driveLetter, DiskInfoNative* info) {
    if (!info) return DN_ERR_INVALID_PARAM;
    memset(info, 0, sizeof(DiskInfoNative));
    info->driveLetter = driveLetter;

    char rootPath[4] = { driveLetter, ':', '\\', '\0' };
    info->driveType = GetDriveTypeA(rootPath);

    char volName[256] = {0}, fsName[32] = {0};
    DWORD serial = 0, maxComp = 0, fsFlags = 0;
    if (GetVolumeInformationA(rootPath, volName, sizeof(volName),
                              &serial, &maxComp, &fsFlags,
                              fsName, sizeof(fsName))) {
        strncpy(info->volumeName, volName, sizeof(info->volumeName) - 1);
        strncpy(info->fileSystem, fsName, sizeof(info->fileSystem) - 1);
        _snprintf(info->serialNumber, sizeof(info->serialNumber) - 1, "%08lX", serial);
    }

    ULARGE_INTEGER freeAvail, totalBytes, totalFree;
    if (GetDiskFreeSpaceExA(rootPath, &freeAvail, &totalBytes, &totalFree)) {
        info->totalBytes = totalBytes.QuadPart;
        info->freeBytes  = totalFree.QuadPart;
        info->usedBytes  = totalBytes.QuadPart - totalFree.QuadPart;
    }

    DWORD spc, bps, fc, tc;
    if (GetDiskFreeSpaceA(rootPath, &spc, &bps, &fc, &tc)) {
        info->sectorSize  = bps;
        info->clusterSize = spc * bps;
    }

    info->isSSD = dn_detect_ssd(driveLetter);
    return DN_OK;
}

int dn_detect_ssd(char driveLetter) {
    char devPath[32];
    _snprintf(devPath, sizeof(devPath), "\\\\.\\%c:", driveLetter);

    HANDLE h = CreateFileA(devPath, 0,
                           FILE_SHARE_READ | FILE_SHARE_WRITE,
                           NULL, OPEN_EXISTING, 0, NULL);
    if (h == INVALID_HANDLE_VALUE) return -1;

    StoragePropQuery q;
    memset(&q, 0, sizeof(q));
    q.PropertyId = SPID_SEEK_PENALTY;
    q.QueryType  = 0;

    SeekPenaltyDesc spd;
    DWORD ret = 0;
    if (DeviceIoControl(h, IOCTL_STORAGE_QUERY_PROPERTY,
                        &q, sizeof(q), &spd, sizeof(spd), &ret, NULL)
        && ret >= sizeof(spd)) {
        CloseHandle(h);
        return spd.IncursSeekPenalty ? 0 : 1;
    }

    q.PropertyId = SPID_TRIM;
    TrimDesc td;
    ret = 0;
    if (DeviceIoControl(h, IOCTL_STORAGE_QUERY_PROPERTY,
                        &q, sizeof(q), &td, sizeof(td), &ret, NULL)
        && ret >= sizeof(td)) {
        CloseHandle(h);
        return td.TrimEnabled ? 1 : 0;
    }

    CloseHandle(h);
    return -1;
}

int dn_get_memory_info(MemoryInfoNative* info) {
    if (!info) return DN_ERR_INVALID_PARAM;
    memset(info, 0, sizeof(MemoryInfoNative));

    MEMORYSTATUSEX ms;
    ms.dwLength = sizeof(ms);
    if (!GlobalMemoryStatusEx(&ms)) return DN_ERR_IO_FAILURE;

    info->totalPhysical     = ms.ullTotalPhys;
    info->availablePhysical = ms.ullAvailPhys;
    info->totalPageFile     = ms.ullTotalPageFile;
    info->availablePageFile = ms.ullAvailPageFile;
    info->memoryLoad        = ms.dwMemoryLoad;
    return DN_OK;
}
