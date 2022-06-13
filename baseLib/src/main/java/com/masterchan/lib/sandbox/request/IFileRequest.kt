package com.masterchan.lib.sandbox.request

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.masterchan.lib.sandbox.FileResponse

/**
 * 分区存储文件访问接口
 * @author: MasterChan
 * @date: 2022-06-09 14:36
 */
interface IFileRequest {

    fun exist(uri: Uri): Boolean

    fun exist(filePath: String): Boolean

    fun isFile(uri: Uri): Boolean

    fun isFile(filePath: String): Boolean

    fun isDirectory(uri: Uri): Boolean

    fun isDirectory(filePath: String): Boolean

    fun lastModified(uri: Uri): Long

    fun lastModified(filePath: String): Long

    fun length(uri: Uri): Long

    fun length(filePath: String): Long

    fun createDir(filePath: String): Boolean

    /**
     * 创建并写入内容，如果文件已存在不会继续写入，返回true；
     *
     * 特别需要注意的是，此方法在[Build.VERSION_CODES.Q]以下也会将创建的文件写入到数据库
     *
     * 如果[filePath]不包含后缀名，在[args]中添加[MediaStore.MediaColumns.MIME_TYPE]参数，会根据
     * mimeType自动添加文件后缀；如果已经包含了后缀名，同时添加了[MediaStore.MediaColumns.MIME_TYPE]
     * 参数，会在原有后缀上追加根据mimeType推测的后缀；
     * 如果只是想写入内容，使用[write]方法；
     * @param filePath 文件夹路径
     * @param data 需要写入的内容
     * @param args 自定义需要写入到文件的参数[ContentValues]
     * @return Boolean
     */
    fun createFile(
        filePath: String,
        data: ByteArray? = null,
        args: (ContentValues.() -> Unit)? = null
    ): Boolean

    fun write(uri: Uri, data: ByteArray?, append: Boolean = false)

    fun write(relativePath: String, data: ByteArray?, append: Boolean = false)

    fun read(uri: Uri): ByteArray?

    fun read(relativePath: String): ByteArray?

    fun delete(uri: Uri): Boolean

    fun delete(relativePath: String): Boolean

    fun renameTo(uri: Uri, name: String): Boolean

    fun renameTo(relativePath: String, destName: String): Boolean

    fun copyTo(uri: Uri, destPath: String): Boolean

    fun copyTo(relativePath: String, destRelativePath: String): Boolean

    fun moveTo(uri: Uri, destPath: String): Boolean

    fun moveTo(relativePath: String, destRelativePath: String): Boolean

    fun getResponse(path: String): FileResponse?

    fun getResponse(uri: Uri): FileResponse?

    fun listFiles(uri: Uri): List<FileResponse>?

    /**
     * 遍历文件夹中的所有文件，如果[withChildDir]为true，子文件夹中的文件也会参与遍历
     * @param relativePath 共享目录相对路径
     * @param withChildDir 是否包含[relativePath]中的子文件夹，此参数只在API29及以上生效，
     * 其余版本返回全部文件
     * @return List<FileResponse>?
     */
    fun listFiles(relativePath: String, withChildDir: Boolean = true): List<FileResponse>?
}