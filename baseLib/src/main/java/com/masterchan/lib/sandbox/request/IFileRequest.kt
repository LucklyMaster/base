package com.masterchan.lib.sandbox.request

import android.content.ContentValues
import android.net.Uri
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
     * 创建并写入内容，如果文件已存在不会继续写入，返回true；如果[filePath]不包含后缀名，在[args]中
     * 添加[MediaStore.MediaColumns.MIME_TYPE]参数，会根据mimeType自动添加文件后缀；如果已经包含了后缀
     * 名，同时添加了[MediaStore.MediaColumns.MIME_TYPE]参数，会在原有后缀上追加根据mimeType推测的后缀；
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

    fun copyTo(uri: Uri, destUri: Uri): Boolean

    fun copyTo(relativePath: String, destRelativePath: String): Boolean

    fun moveTo(uri: Uri, destUri: Uri): Boolean

    fun moveTo(relativePath: String, destRelativePath: String): Boolean

    /**
     * 遍历文件夹中的所有文件，如果[withChildDir]为true，子文件夹中的文件也会参与遍历
     * @param relativePath 共享目录相对路径
     * @param withChildDir 是否包含[relativePath]中的子文件夹，此参数只在API29及以上生效，
     * 其余版本返回全部文件
     * @return List<FileResponse>?
     */
    fun listFiles(relativePath: String, withChildDir: Boolean = true): List<FileResponse>?

    /**
     * 在指定文件夹查找指定文件名称的文件
     * @param relativePath 需要查找文件的文件夹
     * @param fileName 文件名称
     * @param withChildDir 是否包含[relativePath]中的子文件夹
     * @param equalsFileName 是否是查询与[fileName]相等的文件
     * @return List<FileResponse>
     */
    fun queryByName(
        relativePath: String,
        fileName: String,
        withChildDir: Boolean = true,
        equalsFileName: Boolean = false
    ): List<FileResponse>?

    /**
     * 查询指定文件夹下的指定后缀名的文件
     * @param relativePath 要查找文件的文件夹
     * @param extension 后缀名，[.xxx]
     * @param withChildDir 否包含[relativePath]中的子文件夹
     * @return List<FileResponse>
     */
    fun queryByExtension(
        relativePath: String,
        extension: String,
        withChildDir: Boolean = true
    ): List<FileResponse>?
}