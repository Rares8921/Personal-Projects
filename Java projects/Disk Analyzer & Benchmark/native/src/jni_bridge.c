#include <jni.h>
#include "disk_native.h"
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

static void throw_runtime(JNIEnv* env, const char* msg) {
    jclass cls = (*env)->FindClass(env, "java/lang/RuntimeException");
    if (cls) (*env)->ThrowNew(env, cls, msg);
}

static jstring wchar_to_jstring(JNIEnv* env, const wchar_t* ws) {
    int len = (int)wcslen(ws);
    jchar* buf = (jchar*)malloc(len * sizeof(jchar));
    if (!buf) return (*env)->NewStringUTF(env, "");
    for (int i = 0; i < len; i++) buf[i] = (jchar)ws[i];
    jstring s = (*env)->NewString(env, buf, len);
    free(buf);
    return s;
}

static void jstring_to_wchar(JNIEnv* env, jstring js, wchar_t* out, int maxLen) {
    const jchar* chars = (*env)->GetStringChars(env, js, NULL);
    int len = (*env)->GetStringLength(env, js);
    if (len >= maxLen) len = maxLen - 1;
    for (int i = 0; i < len; i++) out[i] = (wchar_t)chars[i];
    out[len] = L'\0';
    (*env)->ReleaseStringChars(env, js, chars);
}

JNIEXPORT jobjectArray JNICALL
Java_com_diskanalyzer_core_NativeBridge_getAvailableDrives(JNIEnv* env, jclass cls) {
    char letters[DN_MAX_DRIVES];
    int n = dn_get_available_drives(letters, DN_MAX_DRIVES);

    jclass strCls = (*env)->FindClass(env, "java/lang/String");
    jobjectArray arr = (*env)->NewObjectArray(env, n, strCls, NULL);
    for (int i = 0; i < n; i++) {
        char s[2] = { letters[i], '\0' };
        (*env)->SetObjectArrayElement(env, arr, i, (*env)->NewStringUTF(env, s));
    }
    return arr;
}

JNIEXPORT jstring JNICALL
Java_com_diskanalyzer_core_NativeBridge_getDiskInfoJson(JNIEnv* env, jclass cls, jchar drive) {
    DiskInfoNative info;
    int rc = dn_get_disk_info((char)drive, &info);
    if (rc != DN_OK) {
        throw_runtime(env, "Failed to get disk info");
        return (*env)->NewStringUTF(env, "{}");
    }

    const char* typeStr = "Unknown";
    switch (info.driveType) {
        case 2: typeStr = "Removable"; break;
        case 3: typeStr = "Fixed"; break;
        case 4: typeStr = "Network"; break;
        case 5: typeStr = "Optical"; break;
        case 6: typeStr = "RAMDisk"; break;
    }

    char buf[2048];
    _snprintf(buf, sizeof(buf),
        "{\"drive\":\"%c\",\"volumeName\":\"%s\",\"fileSystem\":\"%s\","
        "\"serialNumber\":\"%s\",\"totalBytes\":%llu,\"freeBytes\":%llu,"
        "\"usedBytes\":%llu,\"clusterSize\":%lu,\"sectorSize\":%lu,"
        "\"driveType\":\"%s\",\"isSSD\":%s}",
        info.driveLetter, info.volumeName, info.fileSystem,
        info.serialNumber, (unsigned long long)info.totalBytes,
        (unsigned long long)info.freeBytes, (unsigned long long)info.usedBytes,
        (unsigned long)info.clusterSize, (unsigned long)info.sectorSize,
        typeStr, info.isSSD == 1 ? "true" : (info.isSSD == 0 ? "false" : "null"));

    return (*env)->NewStringUTF(env, buf);
}

JNIEXPORT jlongArray JNICALL
Java_com_diskanalyzer_core_NativeBridge_getDiskSpace(JNIEnv* env, jclass cls, jchar drive) {
    DiskInfoNative info;
    dn_get_disk_info((char)drive, &info);

    jlongArray arr = (*env)->NewLongArray(env, 3);
    jlong vals[3] = { (jlong)info.totalBytes, (jlong)info.freeBytes, (jlong)info.usedBytes };
    (*env)->SetLongArrayRegion(env, arr, 0, 3, vals);
    return arr;
}

static jstring bench_result_json(JNIEnv* env, BenchmarkResult* r, const char* type) {
    char buf[1024];
    _snprintf(buf, sizeof(buf),
        "{\"type\":\"%s\",\"throughputMBps\":%.2f,\"avgLatencyUs\":%.2f,"
        "\"minLatencyUs\":%.2f,\"maxLatencyUs\":%.2f,\"iops\":%.2f,"
        "\"totalBytes\":%lld,\"elapsed\":%.4f,\"blockSize\":%d,\"ops\":%d}",
        type, r->throughputMBps, r->avgLatencyUs,
        r->minLatencyUs, r->maxLatencyUs, r->iops,
        (long long)r->totalBytesTransferred, r->elapsedSeconds,
        r->blockSizeBytes, r->operationCount);
    return (*env)->NewStringUTF(env, buf);
}

JNIEXPORT jstring JNICALL
Java_com_diskanalyzer_core_NativeBridge_benchmarkSeqRead(JNIEnv* env, jclass cls,
        jstring dir, jint blockKB, jint totalMB) {
    const char* d = (*env)->GetStringUTFChars(env, dir, NULL);
    BenchmarkResult r;
    int rc = dn_benchmark_seq_read(d, blockKB, totalMB, &r, NULL);
    (*env)->ReleaseStringUTFChars(env, dir, d);
    if (rc != DN_OK) { throw_runtime(env, "Sequential read benchmark failed"); return (*env)->NewStringUTF(env, "{}"); }
    return bench_result_json(env, &r, "SEQ_READ");
}

JNIEXPORT jstring JNICALL
Java_com_diskanalyzer_core_NativeBridge_benchmarkSeqWrite(JNIEnv* env, jclass cls,
        jstring dir, jint blockKB, jint totalMB) {
    const char* d = (*env)->GetStringUTFChars(env, dir, NULL);
    BenchmarkResult r;
    int rc = dn_benchmark_seq_write(d, blockKB, totalMB, &r, NULL);
    (*env)->ReleaseStringUTFChars(env, dir, d);
    if (rc != DN_OK) { throw_runtime(env, "Sequential write benchmark failed"); return (*env)->NewStringUTF(env, "{}"); }
    return bench_result_json(env, &r, "SEQ_WRITE");
}

JNIEXPORT jstring JNICALL
Java_com_diskanalyzer_core_NativeBridge_benchmarkRandRead(JNIEnv* env, jclass cls,
        jstring dir, jint blockKB, jint iters) {
    const char* d = (*env)->GetStringUTFChars(env, dir, NULL);
    BenchmarkResult r;
    int rc = dn_benchmark_rand_read(d, blockKB, iters, &r, NULL);
    (*env)->ReleaseStringUTFChars(env, dir, d);
    if (rc != DN_OK) { throw_runtime(env, "Random read benchmark failed"); return (*env)->NewStringUTF(env, "{}"); }
    return bench_result_json(env, &r, "RAND_READ");
}

JNIEXPORT jstring JNICALL
Java_com_diskanalyzer_core_NativeBridge_benchmarkRandWrite(JNIEnv* env, jclass cls,
        jstring dir, jint blockKB, jint iters) {
    const char* d = (*env)->GetStringUTFChars(env, dir, NULL);
    BenchmarkResult r;
    int rc = dn_benchmark_rand_write(d, blockKB, iters, &r, NULL);
    (*env)->ReleaseStringUTFChars(env, dir, d);
    if (rc != DN_OK) { throw_runtime(env, "Random write benchmark failed"); return (*env)->NewStringUTF(env, "{}"); }
    return bench_result_json(env, &r, "RAND_WRITE");
}

JNIEXPORT jlong JNICALL
Java_com_diskanalyzer_core_NativeBridge_getDirectorySize(JNIEnv* env, jclass cls, jstring path) {
    wchar_t wpath[DN_MAX_PATH];
    jstring_to_wchar(env, path, wpath, DN_MAX_PATH);
    return (jlong)dn_get_directory_size(wpath);
}

JNIEXPORT jint JNICALL
Java_com_diskanalyzer_core_NativeBridge_getFileCount(JNIEnv* env, jclass cls, jstring path) {
    wchar_t wpath[DN_MAX_PATH];
    jstring_to_wchar(env, path, wpath, DN_MAX_PATH);
    return (jint)dn_get_file_count(wpath);
}

JNIEXPORT jstring JNICALL
Java_com_diskanalyzer_core_NativeBridge_scanDirectory(JNIEnv* env, jclass cls,
        jstring path, jint maxEntries) {
    wchar_t wpath[DN_MAX_PATH];
    jstring_to_wchar(env, path, wpath, DN_MAX_PATH);

    if (maxEntries > DN_MAX_ENTRIES) maxEntries = DN_MAX_ENTRIES;
    FileEntryNative* entries = (FileEntryNative*)calloc(maxEntries, sizeof(FileEntryNative));
    if (!entries) { throw_runtime(env, "Allocation failed"); return (*env)->NewStringUTF(env, "[]"); }

    int count = 0;
    dn_scan_directory(wpath, entries, maxEntries, &count);

    int bufSize = count * 512 + 64;
    char* json = (char*)malloc(bufSize);
    if (!json) { free(entries); return (*env)->NewStringUTF(env, "[]"); }

    int pos = 0;
    pos += _snprintf(json + pos, bufSize - pos, "[");
    for (int i = 0; i < count && pos < bufSize - 256; i++) {
        if (i > 0) pos += _snprintf(json + pos, bufSize - pos, ",");

        char pathUtf8[DN_MAX_PATH * 3];
        WideCharToMultiByte(CP_UTF8, 0, entries[i].path, -1, pathUtf8, sizeof(pathUtf8), NULL, NULL);

        for (int j = 0; pathUtf8[j]; j++) {
            if (pathUtf8[j] == '\\') pathUtf8[j] = '/';
        }

        pos += _snprintf(json + pos, bufSize - pos,
            "{\"path\":\"%s\",\"size\":%llu,\"dir\":%s,\"modified\":%llu}",
            pathUtf8, (unsigned long long)entries[i].size,
            entries[i].isDirectory ? "true" : "false",
            (unsigned long long)entries[i].modifiedTime);
    }
    pos += _snprintf(json + pos, bufSize - pos, "]");

    jstring result = (*env)->NewStringUTF(env, json);
    free(json);
    free(entries);
    return result;
}

JNIEXPORT jstring JNICALL
Java_com_diskanalyzer_core_NativeBridge_findLargestFiles(JNIEnv* env, jclass cls,
        jstring path, jint maxResults) {
    wchar_t wpath[DN_MAX_PATH];
    jstring_to_wchar(env, path, wpath, DN_MAX_PATH);

    if (maxResults > 1000) maxResults = 1000;
    FileEntryNative* results = (FileEntryNative*)calloc(maxResults, sizeof(FileEntryNative));
    if (!results) { throw_runtime(env, "Allocation failed"); return (*env)->NewStringUTF(env, "[]"); }

    int count = 0;
    dn_find_largest_files(wpath, results, maxResults, &count);

    int bufSize = count * 512 + 64;
    char* json = (char*)malloc(bufSize);
    if (!json) { free(results); return (*env)->NewStringUTF(env, "[]"); }

    int pos = 0;
    pos += _snprintf(json + pos, bufSize - pos, "[");
    for (int i = 0; i < count && pos < bufSize - 256; i++) {
        if (i > 0) pos += _snprintf(json + pos, bufSize - pos, ",");

        char pathUtf8[DN_MAX_PATH * 3];
        WideCharToMultiByte(CP_UTF8, 0, results[i].path, -1, pathUtf8, sizeof(pathUtf8), NULL, NULL);
        for (int j = 0; pathUtf8[j]; j++)
            if (pathUtf8[j] == '\\') pathUtf8[j] = '/';

        pos += _snprintf(json + pos, bufSize - pos,
            "{\"path\":\"%s\",\"size\":%llu}",
            pathUtf8, (unsigned long long)results[i].size);
    }
    pos += _snprintf(json + pos, bufSize - pos, "]");

    jstring result = (*env)->NewStringUTF(env, json);
    free(json);
    free(results);
    return result;
}

JNIEXPORT jstring JNICALL
Java_com_diskanalyzer_core_NativeBridge_getExtensionStats(JNIEnv* env, jclass cls, jstring path) {
    wchar_t wpath[DN_MAX_PATH];
    jstring_to_wchar(env, path, wpath, DN_MAX_PATH);

    char* json = (char*)malloc(DN_JSON_BUF);
    if (!json) { throw_runtime(env, "Allocation failed"); return (*env)->NewStringUTF(env, "[]"); }

    dn_get_extension_stats(wpath, json, DN_JSON_BUF);
    jstring result = (*env)->NewStringUTF(env, json);
    free(json);
    return result;
}

JNIEXPORT jstring JNICALL
Java_com_diskanalyzer_core_NativeBridge_fullScan(JNIEnv* env, jclass cls,
        jstring path, jint maxEntries, jint maxLargest) {
    wchar_t wpath[DN_MAX_PATH];
    jstring_to_wchar(env, path, wpath, DN_MAX_PATH);

    if (maxEntries > DN_MAX_ENTRIES) maxEntries = DN_MAX_ENTRIES;
    if (maxLargest > 1000) maxLargest = 1000;

    /* 4 MB buffer for composite JSON */
    int bufSize = 4 * 1024 * 1024;
    char* json = (char*)malloc(bufSize);
    if (!json) { throw_runtime(env, "Allocation failed"); return (*env)->NewStringUTF(env, "{}"); }

    int rc = dn_full_scan(wpath, maxEntries, maxLargest, json, bufSize);
    if (rc != DN_OK) {
        free(json);
        throw_runtime(env, "Full scan failed");
        return (*env)->NewStringUTF(env, "{}");
    }

    jstring result = (*env)->NewStringUTF(env, json);
    free(json);
    return result;
}

JNIEXPORT jlongArray JNICALL
Java_com_diskanalyzer_core_NativeBridge_getMemoryInfo(JNIEnv* env, jclass cls) {
    MemoryInfoNative mi;
    dn_get_memory_info(&mi);

    jlongArray arr = (*env)->NewLongArray(env, 5);
    jlong vals[5] = {
        (jlong)mi.totalPhysical, (jlong)mi.availablePhysical,
        (jlong)(mi.totalPhysical - mi.availablePhysical),
        (jlong)mi.memoryLoad,
        (jlong)mi.totalPageFile
    };
    (*env)->SetLongArrayRegion(env, arr, 0, 5, vals);
    return arr;
}

JNIEXPORT jstring JNICALL
Java_com_diskanalyzer_core_NativeBridge_analyzeFragmentation(JNIEnv* env, jclass cls,
        jstring path, jint maxFiles) {
    wchar_t wpath[DN_MAX_PATH];
    jstring_to_wchar(env, path, wpath, DN_MAX_PATH);

    int bufSize = 512 * 1024;
    char* json = (char*)malloc(bufSize);
    if (!json) { throw_runtime(env, "Allocation failed"); return (*env)->NewStringUTF(env, "{}"); }

    int rc = dn_analyze_fragmentation(wpath, (int)maxFiles, json, bufSize);
    if (rc != DN_OK) {
        free(json);
        throw_runtime(env, "Fragmentation analysis failed");
        return (*env)->NewStringUTF(env, "{}");
    }

    jstring result = (*env)->NewStringUTF(env, json);
    free(json);
    return result;
}
