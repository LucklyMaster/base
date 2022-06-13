package com.masterchan.lib.sandbox.request

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.masterchan.lib.ext.*
import com.masterchan.lib.sandbox.FileResponse
import java.io.File

/**
 * 文件访问的一些通用实现；不再处理异常，需要调用者自行处理
 * @author: MasterChan
 * @date: 2022-06-09 16:27
 */
abstract class AbsFileRequest : IFileRequest {

    protected val resolver = application.contentResolver!!
    protected val fileUri = MediaStore.Files.getContentUri("external")
    protected val imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    protected val audioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    protected val videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

    override fun exist(uri: Uri): Boolean {
        return DocumentFile.fromSingleUri(application, uri)?.exists() == true
    }

    override fun exist(filePath: String): Boolean {
        return File(obtainPath(filePath)).exists()
    }

    override fun isFile(uri: Uri): Boolean {
        return !isDirectory(uri)
    }

    override fun isFile(filePath: String): Boolean {
        return !isDirectory(filePath)
    }

    override fun isDirectory(uri: Uri): Boolean {
        return DocumentFile.fromSingleUri(application, uri)?.isDirectory == true
    }

    override fun isDirectory(filePath: String): Boolean {
        return File(obtainPath(filePath)).isDirectory
    }

    override fun lastModified(uri: Uri): Long {
        return DocumentFile.fromSingleUri(application, uri)?.lastModified() ?: -1L
    }

    override fun lastModified(filePath: String): Long {
        return File(obtainPath(filePath)).lastModified()
    }

    override fun length(uri: Uri): Long {
        return DocumentFile.fromSingleUri(application, uri)?.length() ?: -1L
    }

    override fun length(filePath: String): Long {
        return File(obtainPath(filePath)).length()
    }

    override fun createDir(filePath: String): Boolean {
        return File(obtainPath(filePath)).createDirs()
    }

    override fun write(uri: Uri, data: ByteArray?, append: Boolean) {
        data?.let { resolver.openOutputStream(uri, if (append) "wa" else "w")?.write(it) }
    }

    override fun write(relativePath: String, data: ByteArray?, append: Boolean) {
        val file = File(obtainPath(relativePath))
        file.parentFile?.createDirs()
        write(file.toUri(), data, append)
    }

    override fun read(uri: Uri): ByteArray? {
        return resolver.openInputStream(uri)?.readBytes()
    }

    override fun read(relativePath: String): ByteArray? {
        val file = File(obtainPath(relativePath)).orNull { exists() } ?: return null
        return read(file.toUri())
    }

    override fun delete(uri: Uri): Boolean {
        return resolver.delete(uri, null, null) >= 0
    }

    override fun delete(relativePath: String): Boolean {
        val response = getResponse(relativePath) ?: return false
        return delete(response.uri)
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

    override fun renameTo(relativePath: String, destName: String): Boolean {
        val file = File(obtainPath(relativePath))
        if (!file.exists()) {
            return false
        }
        val response = getResponse(relativePath) ?: return false
        val cv = ContentValues()
        cv.put(MediaStore.MediaColumns.DISPLAY_NAME, destName)
        return resolver.update(response.uri, cv, null, null) >= 0
    }

    override fun copyTo(uri: Uri, destPath: String): Boolean {
        return try {
            write(destPath, read(uri))
            true
        } catch (t: Throwable) {
            t.printStackTrace()
            false
        }
    }

    override fun copyTo(
        relativePath: String,
        destRelativePath: String
    ): Boolean {
        val srcFile = File(obtainPath(relativePath))
        if (!srcFile.exists()) {
            return false
        }
        return try {
            val destFile = File(obtainPath(destRelativePath)).apply { create() }
            resolver.openInputStream(srcFile.toUri())?.readBytes().let {
                resolver.openOutputStream(destFile.toUri())?.write(it)
            }
            true
        } catch (t: Throwable) {
            t.printStackTrace()
            false
        }
    }

    override fun moveTo(uri: Uri, destPath: String): Boolean {
        return if (copyTo(uri, destPath)) delete(uri) else false
    }

    override fun moveTo(
        relativePath: String,
        destRelativePath: String
    ): Boolean {
        return if (copyTo(relativePath, destRelativePath)) {
            delete(relativePath)
        } else {
            false
        }
    }

    override fun listFiles(uri: Uri): List<FileResponse>? {
        val file = uri.toFileResponse()?.file ?: return null
        val path = if (file.isDirectory) {
            file.absolutePath
        } else {
            file.parentFile?.absolutePath ?: return null
        }
        return listFiles(path)
    }

    override fun getResponse(path: String): FileResponse? {
        val selection = "${MediaStore.MediaColumns.DATA} = ?"
        val args = arrayOf(obtainPath(path))
        val list = query(selection, args)
        if (!list.isNullOrEmpty()) {
            return list.first()
        }
        return null
    }

    override fun getResponse(uri: Uri): FileResponse? {
        check(uri.authority == "content://") { "only support the 'content://' authority" }
        val cursor = resolver.query(
            uri, getProjection(), null, null, null
        ) ?: return null
        val list = read2Response(cursor)
        if (list.isNotEmpty()) {
            return list.first()
        }
        return null
    }

    /**
     * 路径组装，将传入的路径组装为已[Environment.getExternalStorageDirectory]开头
     * @param relativePath 组装的路径
     * @return String
     */
    protected fun obtainPath(relativePath: String): String {
        val rootPath = Environment.getExternalStorageDirectory().absolutePath
        return if (relativePath.startsWith(rootPath)) {
            relativePath
        } else {
            "$rootPath/$relativePath"
        }
    }

    /**
     * ContentProvider查询方法
     * @param selection 查询条件
     * @param args 查询参数
     * @return List<FileResponse>?
     */
    protected fun query(selection: String?, args: Array<String>?): List<FileResponse>? {
        val cursor = resolver.query(
            fileUri, getProjection(), selection, args, null
        ) ?: return null
        return read2Response(cursor)
    }

    protected fun getProjection(): Array<String> {
        val projection = mutableListOf<String>()
        projection.add(MediaStore.MediaColumns._ID)
        projection.add(MediaStore.MediaColumns.DATA)
        projection.add(MediaStore.MediaColumns.DATE_ADDED)
        projection.add(MediaStore.MediaColumns.WIDTH)
        projection.add(MediaStore.MediaColumns.HEIGHT)
        projection.add(MediaStore.Images.Media.LATITUDE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            projection.add(MediaStore.MediaColumns.DURATION)
        }
        return projection.toTypedArray()
    }

    @SuppressLint("Range")
    protected fun read2Response(cursor: Cursor): MutableList<FileResponse> {
        val list = mutableListOf<FileResponse>()
        try {
            cursor.moveToFirst()
            (0 until cursor.count).forEach { _ ->
                val id = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
                val path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))
                val date = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.DATE_ADDED))
                val width = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns.WIDTH))
                val height = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns.HEIGHT))
                Log.d("经纬度:" + cursor.getColumnIndex(MediaStore.Images.Media.LATITUDE))
                val duration = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.DURATION))
                } else {
                    FileResponse.NOT_SUPPORT
                }
                val uri = ContentUris.withAppendedId(fileUri, id)
                list.add(FileResponse(uri, File(path), date, duration, width, height))
                cursor.moveToNext()
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        } finally {
            cursor.close()
        }
        return list
    }

    /**
     * 根据传入的标准目录，选择合适的数据库
     * @param standardDir String
     * @return Uri
     */
    protected fun getSuitableContentUri(standardDir: String?): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            when (standardDir) {
                Environment.DIRECTORY_DCIM,
                Environment.DIRECTORY_SCREENSHOTS,
                Environment.DIRECTORY_PICTURES -> imageUri
                Environment.DIRECTORY_DOWNLOADS,
                Environment.DIRECTORY_DOCUMENTS -> fileUri
                Environment.DIRECTORY_ALARMS,
                Environment.DIRECTORY_AUDIOBOOKS,
                Environment.DIRECTORY_PODCASTS,
                Environment.DIRECTORY_MUSIC,
                Environment.DIRECTORY_NOTIFICATIONS -> audioUri
                Environment.DIRECTORY_MOVIES -> videoUri
                else -> fileUri
            }
        } else {
            when (standardDir) {
                Environment.DIRECTORY_DCIM,
                Environment.DIRECTORY_PICTURES -> imageUri
                Environment.DIRECTORY_DOWNLOADS,
                Environment.DIRECTORY_DOCUMENTS -> fileUri
                Environment.DIRECTORY_ALARMS,
                Environment.DIRECTORY_PODCASTS,
                Environment.DIRECTORY_MUSIC,
                Environment.DIRECTORY_NOTIFICATIONS -> audioUri
                Environment.DIRECTORY_MOVIES -> videoUri
                else -> fileUri
            }
        }
    }

    protected fun getStandardDir(file: File): String {
        //将filePath拆分成Standard Dir的相对路径
        var dir = file.absolutePath
        val rootPath = "${Environment.getExternalStorageDirectory().absolutePath}/"
        if (dir.startsWith(rootPath)) {
            dir = dir.replace(rootPath, "")
        }
        return dir.split("/").first()
    }
}