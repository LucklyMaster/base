package com.masterchan.lib.sandbox

import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.masterchan.lib.ext.Log
import com.masterchan.lib.ext.application

/**
 * SDK在29以上的实现
 * @author: MasterChan
 * @date: 2022-06-09 16:34
 */
@RequiresApi(Build.VERSION_CODES.Q)
class FileAccess29Impl {

    private val resolver = application.contentResolver

    fun listFile(dir: String?) {
        var selection: String? = null
        var args: Array<String>? = null
        dir?.let {
            selection = MediaStore.Files.FileColumns.RELATIVE_PATH + " like ?"
            args = arrayOf(Environment.DIRECTORY_DOCUMENTS + "%")
        }

        val cursor = resolver.query(
            MediaStore.Files.getContentUri("external"), null, selection, args, null
        )

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Log.d(cursor?.getString(cursor?.getColumnIndex(MediaStore.MediaColumns.DATA)))
            } while (cursor.moveToNext())
        }

        cursor?.close()
    }
}