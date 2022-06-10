package com.masterchan.lib.sandbox.access

import android.content.ContentValues
import android.net.Uri
import android.webkit.MimeTypeMap
import com.masterchan.lib.sandbox.response.FileResponse

/**
 * 沙盒文件访问接口
 * @author: MasterChan
 * @date: 2022-06-09 14:36
 */
interface IFileRequest {

    fun exist(uri: Uri): Boolean

    fun isFile(uri: Uri): Boolean

    fun isDirectory(uri: Uri): Boolean

    fun lastModified(uri: Uri): Long

    fun length(uri: Uri): Long

    fun createDir(path: String): Boolean

    fun createDir(publicDir: String, dirName: String): Boolean

    fun createFile(path: String): Boolean

    fun createFile(publicDir: String, filePath: String, vararg args: Pair<String, String>): Boolean

    fun insert(publicDir: String, fileName: String, data: ByteArray?): Boolean

    fun insert(
        publicDir: String,
        fileName: String,
        data: ByteArray?,
        args: ContentValues.() -> Unit
    ): Boolean

    fun write(uri: Uri, data: ByteArray?)

    fun write(publicDir: String, fileName: String, data: ByteArray?)

    fun read(uri: Uri): ByteArray?

    fun read(publicDir: String, fileName: String): ByteArray?

    fun delete(uri: Uri)

    fun delete(publicDir: String, fileName: String)

    fun queryByName(publicDir: String, fileName: String): List<FileResponse>

    fun queryByExtension(publicDir: String, extension: String): List<FileResponse>

    fun renameTo(uri: Uri, name: String)

    fun renameTo(publicDir: String, fileName: String, destName: String)

    fun copyTo(uri: Uri, destUri: Uri)

    fun copyTo(publicDir: String, fileName: String, destDir: String, destName: String)

    fun moveTo(uri: Uri, destUri: Uri)

    fun moveTo(publicDir: String, fileName: String, destDir: String, destName: String)

    /**
     * 获取数据库中某个文件夹下的所有文件
     * @param publicDir 公共目录，如果为空，返回数据库的全部文件
     * @return AbsFileBean
     */
    fun listFile(publicDir: String): FileResponse?
}