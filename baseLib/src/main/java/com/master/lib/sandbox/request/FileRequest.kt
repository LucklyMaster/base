package com.master.lib.sandbox.request

import android.annotation.SuppressLint
import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.master.lib.ext.deleteAll
import com.master.lib.ext.mimeType
import com.master.lib.sandbox.FileResponse
import java.io.File

/**
 * API29及以上文件访问实现
 * @author: MasterChan
 * @date: 2022-06-11 17:13
 */
@RequiresApi(Build.VERSION_CODES.Q)
open class FileRequest : AbsFileRequest() {

    override fun createFile(
        filePath: String,
        fileName: String,
        data: ByteArray?,
        args: (ContentValues.() -> Unit)?
    ): Boolean {
        val file = File(obtainPath(filePath))
        check(file.extension.isEmpty()) { "Only allow folders，the file is ${file.path}" }

        val rootPath = "${Environment.getExternalStorageDirectory().absolutePath}/"
        val relativePath = file.absolutePath.replace(rootPath, "")

        val cv = ContentValues()
        cv.put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
        cv.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        args?.invoke(cv)
        var mimeType = cv.getAsString(MediaStore.MediaColumns.MIME_TYPE)
        if (mimeType.isNullOrEmpty()) {
            mimeType = File("${file.absolutePath}/$fileName").mimeType
            cv.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        }

        val uri = resolver.insert(
            getSuitableContentUri(relativePath.split("/").first(), mimeType ?: ""), cv
        ) ?: return false
        write(uri, data ?: "".toByteArray())
        return true
    }

    override fun delete(uri: Uri): Boolean {
        return resolver.delete(uri, null, null) > 0
    }

    override fun delete(filePath: String): Boolean {
        val file = File(obtainPath(filePath))
        if (!file.exists()) {
            return true
        }
        if (file.isDirectory) {
            val where = "${MediaStore.MediaColumns.DATA} like ?"
            val args = arrayOf("${file.absolutePath}%")
            resolver.delete(fileUri, where, args)
            file.deleteAll()
            //非共享目录的文件或文件夹无法删除
            if (!file.exists() || file.listFiles()?.isEmpty() == true) {
                return true
            }
            return false
        }
        val where = "${MediaStore.MediaColumns.DATA} = ?"
        val args = arrayOf(file.absolutePath)
        return resolver.delete(fileUri, where, args) > 0
    }

    override fun renameTo(uri: Uri, name: String): Boolean {
        return try {
            val cv = ContentValues()
            cv.put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            resolver.update(uri, cv, null, null)
            true
        } catch (t: Throwable) {
            t.printStackTrace()
            false
        }
    }

    override fun renameTo(filePath: String, destName: String): Boolean {
        val file = File(obtainPath(filePath))
        if (!file.exists()) {
            return false
        }
        val response = getResponse(filePath) ?: return false
        val cv = ContentValues()
        cv.put(MediaStore.MediaColumns.DISPLAY_NAME, destName)
        return resolver.update(response.uri, cv, null, null) > 0
    }

    @SuppressLint("Range")
    override fun listFiles(relativePath: String, withChildDir: Boolean): List<FileResponse>? {
        val selection = if (!withChildDir) {
            "${MediaStore.MediaColumns.RELATIVE_PATH} = ?"
        } else {
            "${MediaStore.MediaColumns.RELATIVE_PATH} like ?"
        }
        val args = if (!withChildDir) arrayOf("$relativePath/") else arrayOf("$relativePath/%")
        return query(selection, args)
    }
}