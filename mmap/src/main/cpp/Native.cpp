//
// Created by MasterChan on 2022-5-30.
//
#include <jni.h>
#include "MPFile.h"
#include <jni.h>

extern "C"
JNIEXPORT jlong

extern "C" jlong
Java_com_masterchan_mmap_MPLog_init(JNIEnv* env, jobject thiz, jstring _cachePath, jstring _logPath,jint _cacheSize)
{
    const char* path_cache = env->GetStringUTFChars(_cachePath, nullptr);
    const char* path_log = env->GetStringUTFChars(_logPath, nullptr);
    auto* mp_file = new MPFile();
    long code = mp_file->init(path_cache, path_log, _cacheSize);
    env->ReleaseStringUTFChars(_cachePath, path_cache);
    env->ReleaseStringUTFChars(_cachePath, path_log);
    if (code != 0) {
        return code;
    }
    return reinterpret_cast<jlong>(mp_file);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_masterchan_mmap_MPLog_write(JNIEnv* env, jobject thiz, jlong handle, jstring _text, jboolean append)
{
    const char* text = env->GetStringUTFChars(_text, nullptr);
    auto* mp_file = (MPFile*) handle;
    if (mp_file->writeable_size() < strlen(text)) {
        mp_file->flush();
    }
    mp_file->write_cache(text, false);
    env->ReleaseStringUTFChars(_text, text);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_masterchan_mmap_MPLog_release(JNIEnv* env, jobject thiz, jlong handle) {
    auto* mp_file = (MPFile*) handle;
    mp_file->release();
}
