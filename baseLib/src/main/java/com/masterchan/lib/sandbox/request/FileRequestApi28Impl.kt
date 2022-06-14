package com.masterchan.lib.sandbox.request

import android.content.ContentValues
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.masterchan.lib.ext.create
import com.masterchan.lib.ext.deleteAll
import com.masterchan.lib.ext.mimeType
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
        val cv = ContentValues()
        File(obtainPath(filePath)).mimeType?.let {
            cv.put(MediaStore.MediaColumns.MIME_TYPE, it)
        }
        //根据MIME_TYPE判断是否需要在文件末尾添加推断的扩展名
        var suffix = ""
        if (args != null) {
            args.invoke(cv)
            cv.getAsString(MediaStore.MediaColumns.MIME_TYPE)?.let {
                suffix = MimeTypeMap.getSingleton().getExtensionFromMimeType(it) ?: ""
                if (suffix.isNotEmpty()) {
                    suffix = ".$suffix"
                }
            }
        }
        val file = File(obtainPath("$filePath$suffix"))
        if (file.exists()) {
            return true
        }
        cv.put(MediaStore.MediaColumns.DATA, file.absolutePath)
        cv.put(MediaStore.MediaColumns.DISPLAY_NAME, file.name)

        val rootPath = "${Environment.getExternalStorageDirectory().absolutePath}/"
        val standardDir = filePath.replace(rootPath, "").split("/").firstOrNull()
        resolver.insert(getSuitableContentUri(standardDir), cv)
        if (!file.create()) {
            return false
        }
        data?.let { file.writeBytes(it) }
        return true
    }

    override fun delete(relativePath: String): Boolean {
        val file = File(obtainPath(relativePath))
        if (!file.exists()) {
            return true
        }
        val selection = "${MediaStore.MediaColumns.DATA} like ?"
        val args = arrayOf("${file.absolutePath}%")
        resolver.delete(fileUri, selection, args)
        return file.deleteAll()
    }

    override fun listFiles(relativePath: String, withChildDir: Boolean): List<FileResponse>? {
        val selection = "${MediaStore.MediaColumns.DATA} like ?"
        val args = arrayOf(obtainPath("$relativePath%"))
        return query(selection, args)
    }
}