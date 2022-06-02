//
// Created by MasterChan on 2022-5-30.
//
#include <jni.h>
#include "MPFile.h"
#include "Log.h"

extern "C"
JNIEXPORT jlong JNICALL
Java_com_masterchan_mmap_MPFile_open(JNIEnv* env, jobject thiz, jstring _cachePath, jstring _logPath)
{
    const char* path = env->GetStringUTFChars(_cachePath, nullptr);
    auto* mp_file = new MPFile();
    string a = path;
    string b = path;
    START_TIMER
    long code = mp_file->init(a, b);
    END_TIMER("初始化耗时")
    env->ReleaseStringUTFChars(_cachePath, path);
    if (code != 0) {
        return code;
    }
    return reinterpret_cast<jlong>(mp_file);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_masterchan_mmap_MPFile_write(JNIEnv* env, jobject thiz, jlong handle, jstring _text, jboolean append)
{
    const char* text = env->GetStringUTFChars(_text, nullptr);
    auto* mp_file = (MPFile*) handle;
    string a = text;
    mp_file->writeCache(a, append);
    env->ReleaseStringUTFChars(_text, text);
}
