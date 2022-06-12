package com.masterchan.lib.sandbox.request

import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.masterchan.lib.ext.create
import com.masterchan.lib.ext.listFilesWithChildDir
import com.masterchan.lib.sandbox.FileResponse
import java.io.File

/**
 * API28及以下文件访问实现
 * @author: MasterChan
 * @date: 2022-06-10 16:25
 */
open class FileRequestApi28Impl : AbsFileRequest() {

    override fun createFile(
        filePath: String,
        data: ByteArray?,
        args: (ContentValues.() -> Unit)?
    ): Boolean {
        var suffix = ""
        args?.let {
            val cv = ContentValues()
            args.invoke(cv)
            val mimeType = cv.getAsString(MediaStore.MediaColumns.MIME_TYPE)
            mimeType?.let {
                suffix = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: ""
                if (suffix.isNotEmpty()) {
                    suffix = ".".plus(suffix)
                }
            }
        }
        val file = File(obtainPath("$filePath$suffix"))
        if (file.exists()) {
            return true
        }
        val cv = ContentValues()
        cv.put(MediaStore.MediaColumns.DATA, file.absolutePath)
        cv.put(MediaStore.MediaColumns.DISPLAY_NAME, file.name)
        resolver.insert(MediaStore.Files.getContentUri("external"), cv)
        if (!file.create()) {
            return false
        }
        data?.let { file.writeBytes(it) }
        return true
    }

    override fun renameTo(uri: Uri, name: String): Boolean {
        /*resolver.query(
            MediaStore.Files.getContentUri("external"), arrayOf(MediaStore.MediaColumns.DATA),
            "${MediaStore.MediaColumns.}"
        )
        return false*/
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

    override fun queryByName(
        relativePath: String,
        fileName: String,
        withChildDir: Boolean,
        equalsFileName: Boolean
    ): List<FileResponse> {
        val resultList = mutableListOf<FileResponse>()
        getListFiles(relativePath, withChildDir)?.forEach {
            if (equalsFileName && it.name == fileName) {
                resultList.add(obtainFileResponse(it))
            } else if (!equalsFileName && it.name.contains(fileName)) {
                resultList.add(obtainFileResponse(it))
            }
        }
        return resultList
    }

    override fun queryByExtension(
        relativePath: String,
        extension: String,
        withChildDir: Boolean
    ): List<FileResponse> {
        val resultList = mutableListOf<FileResponse>()
        getListFiles(relativePath, withChildDir)?.forEach {
            if (it.extension == extension) {
                resultList.add(obtainFileResponse(it))
            }
        }
        return resultList
    }

    override fun listFiles(relativePath: String, withChildDir: Boolean): List<FileResponse>? {
        val selection = "${MediaStore.MediaColumns.DATA} like ?"
        val args = arrayOf(obtainPath("$relativePath%"))
        return query(selection, args)
    }

    protected fun getListFiles(relativePath: String, withChildDir: Boolean): List<File>? {
        val file = File(obtainPath(relativePath))
        return if (withChildDir) {
            file.listFilesWithChildDir()
        } else {
            file.listFiles()?.toList()
        }
    }

    protected fun obtainFileResponse(file: File): FileResponse {
        return FileResponse(null, file)
    }
}