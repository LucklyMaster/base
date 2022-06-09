package com.masterchan.lib.sandbox

import android.os.Build
import android.os.Environment.*

/**
 *
 * @author: MasterChan
 * @date: 2022-06-09 16:27
 */
abstract class AbsFileAccess : IFileAccess {

    protected val publicDirs: Array<String> by lazy { getPublicDirList().toTypedArray() }

    protected open fun checkPublicDir(dir: String?) {
        if (!dir.isNullOrEmpty() && !isPublicDir(dir)) {
            throw ArithmeticException("the dir is not a public dir")
        }
    }

    protected open fun isPublicDir(dir: String?): Boolean {
        return publicDirs.contains(dir)
    }

    private fun getPublicDirList(): List<String> {
        val list = mutableListOf(
            DIRECTORY_MUSIC, DIRECTORY_PODCASTS, DIRECTORY_ALARMS, DIRECTORY_RINGTONES,
            DIRECTORY_NOTIFICATIONS, DIRECTORY_PICTURES, DIRECTORY_MOVIES, DIRECTORY_DOWNLOADS,
            DIRECTORY_DCIM, DIRECTORY_DOCUMENTS
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            list.add(DIRECTORY_AUDIOBOOKS)
        }
        return list
    }
}