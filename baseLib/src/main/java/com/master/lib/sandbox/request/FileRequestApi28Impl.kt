package com.master.lib.sandbox.request

import android.content.ContentValues
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.master.lib.ext.*
import com.master.lib.sandbox.FileResponse
import java.io.File

/**
 * API28及以下文件访问实现
 * @author: MasterChan
 * @date: 2022-06-10 16:25
 */
open class FileRequestApi28Impl : AbsFileRequest() {

    override fun createFile(
        filePath: String,
        fileName: String,
        data: ByteArray?,
        args: (ContentValues.() -> Unit)?
    ): Boolean {
        val cv = ContentValues()
        var file = File(obtainPath("$filePath/$fileName"))
        cv.put(MediaStore.MediaColumns.DATA, file.absolutePath)
        cv.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        args?.invoke(cv)

        var mimeType = cv.getAsString(MediaStore.MediaColumns.MIME_TYPE)
        //没有mimeType，自动推测添加一个mimeType
        if (mimeType.isNullOrEmpty()) {
            mimeType = file.mimeType
            cv.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        }
        //有mimeType，组装为包含mimeType推测文件后缀的文件
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
        val split = fileName.split(".")
        if (split.size == 1 || split[1] != extension) {
            file = if (extension.isNullOrEmpty()) {
                File(file.absolutePath)
            } else {
                File("${file.absolutePath}.$extension")
            }
            cv.put(MediaStore.MediaColumns.DATA, file.absolutePath)
        }

        val rootPath = "${Environment.getExternalStorageDirectory().absolutePath}/"
        val standardDir = filePath.replace(rootPath, "").split("/").firstOrNull()
        resolver.insert(getSuitableContentUri(standardDir, mimeType ?: ""), cv)

        if (!file.create()) {
            return false
        }
        data?.let { file.writeBytes(it) }
        return true
    }

    override fun delete(uri: Uri): Boolean {
        val fileResponse = uri.toFileResponse() ?: return false
        return delete(fileResponse.file.absolutePath)
    }

    override fun delete(filePath: String): Boolean {
        val file = File(obtainPath(filePath))
        if (!file.exists()) {
            return true
        }
        return if (file.isDirectory) {
            val selection = "${MediaStore.MediaColumns.DATA} like ?"
            val args = arrayOf("${file.absolutePath}%")
            resolver.delete(fileUri, selection, args)
            file.deleteAll()
        } else {
            val selection = "${MediaStore.MediaColumns.DATA} = ?"
            val args = arrayOf(file.absolutePath)
            resolver.delete(fileUri, selection, args)
            if (file.exists()) {
                file.delete()
            } else {
                true
            }
        }
    }

    override fun renameTo(uri: Uri, name: String): Boolean {
        val fileResponse = uri.toFileResponse() ?: return false
        return try {
            if (!fileResponse.file.renameTo(name)) {
                return false
            }
            val cv = ContentValues()
            cv.put(MediaStore.MediaColumns.DATA, "${fileResponse.file.parent!!}/$name")
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
        if (!file.renameTo(destName)) {
            return false
        }
        val cv = ContentValues()
        cv.put(MediaStore.MediaColumns.DATA, "${file.parent!!}/$destName")
        cv.put(MediaStore.MediaColumns.DISPLAY_NAME, destName)
        resolver.update(response.uri, cv, null, null)
        return true
    }

    override fun listFiles(relativePath: String, withChildDir: Boolean): List<FileResponse>? {
        val selection = "${MediaStore.MediaColumns.DATA} like ?"
        val args = arrayOf(obtainPath("$relativePath%"))
        return query(selection, args)
    }
}