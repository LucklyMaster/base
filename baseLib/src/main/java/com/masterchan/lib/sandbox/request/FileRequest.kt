package com.masterchan.lib.sandbox.request

import android.annotation.SuppressLint
import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import com.masterchan.lib.ext.mimeType
import com.masterchan.lib.sandbox.FileResponse
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
        data: ByteArray?,
        args: (ContentValues.() -> Unit)?
    ): Boolean {
        val file = File(obtainPath(filePath))
        // 文件已存在，直接返回
        // 这里无法判断没有后缀，添加了mimeType的情况
        if (file.exists()) {
            return true
        }

        //将filePath拆分成Standard Dir的相对路径
        var dir = filePath
        val rootPath = "${Environment.getExternalStorageDirectory().absolutePath}/"
        if (dir.startsWith(rootPath)) {
            dir = dir.replace(rootPath, "")
        }
        dir = dir.substringBefore("/${file.name}")

        //添加参数，并自动推断其mimeType
        val cv = ContentValues()
        cv.put(MediaStore.MediaColumns.RELATIVE_PATH, dir)
        cv.put(MediaStore.MediaColumns.DISPLAY_NAME, file.name)
        file.mimeType?.let {
            cv.put(MediaStore.MediaColumns.MIME_TYPE, it)
        }
        args?.invoke(cv)

        //判断没有后缀，添加了mimeType的情况
        val split = filePath.split("/")
        if (split.isNotEmpty() && split.last().contains(".")) {
            val mimeType = cv.getAsString(MediaStore.MediaColumns.MIME_TYPE)
            if (mimeType != null) {
                val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
                if (File("${file.absolutePath}.$extension").exists()) {
                    return true
                }
            }
        }
        try {
            val uri = resolver.insert(
                getSuitableContentUri(dir.split("/").first()), cv
            ) ?: return false
            write(uri, data ?: "".toByteArray())
            return true
        } catch (t: Throwable) {
            t.printStackTrace()
            return false
        }
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

    override fun queryByName(
        relativePath: String,
        fileName: String,
        withChildDir: Boolean,
        equalsFileName: Boolean
    ): List<FileResponse>? {
        val operate = if (equalsFileName) "like" else "="
        var selection = "${MediaStore.MediaColumns.DISPLAY_NAME} $operate ? "
        if (!withChildDir) {
            selection = selection.plus("${MediaStore.MediaColumns.RELATIVE_PATH}=?")
        }
        val args = if (!withChildDir) {
            arrayOf(if (equalsFileName) "%$fileName%" else fileName, "$relativePath/")
        } else {
            arrayOf(if (equalsFileName) "%$fileName%" else fileName)
        }
        return query(selection, args)
    }

    override fun queryByExtension(
        relativePath: String,
        extension: String,
        withChildDir: Boolean
    ): List<FileResponse>? {
        var selection = "${MediaStore.MediaColumns.DISPLAY_NAME} like ? "
        if (!withChildDir) {
            selection = selection.plus("${MediaStore.MediaColumns.RELATIVE_PATH}=?")
        }
        val args = if (!withChildDir) {
            arrayOf("%$extension", "$relativePath/")
        } else {
            arrayOf("%$extension")
        }
        return query(selection, args)
    }
}