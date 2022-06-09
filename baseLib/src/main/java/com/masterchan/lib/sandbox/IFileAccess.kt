package com.masterchan.lib.sandbox

import android.net.Uri

/**
 * 沙盒文件访问接口
 * @author: MasterChan
 * @date: 2022-06-09 14:36
 */
interface IFileAccess {

    fun exist(uri: Uri): Boolean

    fun isFile(uri: Uri): Boolean

    fun isDirectory(uri: Uri): Boolean

    fun lastModified(uri: Uri): Long

    fun length(uri: Uri): Long

    fun createDir(publicDir: String, dirName: String): Boolean

    fun createFile(publicDir: String, fileName: String, vararg args: Pair<String, String>): Boolean

    fun insert(publicDir: String, fileName: String, data: ByteArray?): Boolean

    fun insert(
        publicDir: String,
        fileName: String,
        data: ByteArray?,
        vararg args: Pair<String, String>
    ): Boolean

    fun write(uri: Uri, data: ByteArray?)

    fun read(uri: Uri): ByteArray?

    fun delete(uri: Uri)

    fun queryByName(publicDir: String, fileName: String): List<AbsFileBean>

    fun queryByExtension(publicDir: String, extension: String): List<AbsFileBean>

    fun renameTo(name: String)

    /**
     * 获取数据库中某个文件夹下的所有文件
     * @param publicDir 公共目录，如果为空，返回数据库的全部文件
     * @return AbsFileBean
     */
    fun listFile(publicDir: String): AbsFileBean?
}