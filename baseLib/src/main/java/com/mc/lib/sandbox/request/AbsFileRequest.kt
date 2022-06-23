package com.mc.lib.sandbox.request

import android.annotation.SuppressLint
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.mc.lib.ext.application
import com.mc.lib.ext.createDirs
import com.mc.lib.ext.orNull
import com.mc.lib.ext.toFileResponse
import com.mc.lib.sandbox.FileResponse
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
        return uri.toFileResponse()?.file?.exists() == true
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

    override fun write(filePath: String, data: ByteArray?, append: Boolean) {
        val file = File(obtainPath(filePath))
        if (file.exists()) {
            file.parentFile?.createDirs()
            write(file.toUri(), data, append)
        } else {
            createFile(file.parent!!, file.name, data)
        }
    }

    override fun read(uri: Uri): ByteArray? {
        return resolver.openInputStream(uri)?.readBytes()
    }

    override fun read(filePath: String): ByteArray? {
        val file = File(obtainPath(filePath)).orNull { exists() } ?: return null
        return read(file.toUri())
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

    override fun copyTo(filePath: String, destFilePath: String): Boolean {
        val srcFile = File(obtainPath(filePath))
        if (!srcFile.exists()) {
            return false
        }
        write(obtainPath(destFilePath), read(srcFile.absolutePath))
        return true
    }

    override fun moveTo(uri: Uri, destPath: String): Boolean {
        return if (copyTo(uri, destPath)) delete(uri) else false
    }

    override fun moveTo(
        filePath: String,
        destFilePath: String
    ): Boolean {
        return if (copyTo(filePath, destFilePath)) {
            delete(filePath)
        } else {
            false
        }
    }

    override fun listFiles(uri: Uri): List<FileResponse>? {
        val file = uri.toFileResponse()?.file ?: return null
        if (!file.isDirectory) {
            return null
        }
        return listFiles(file.absolutePath)
    }

    override fun getResponse(path: String): FileResponse? {
        val file = File(obtainPath(path))
        check(!file.isDirectory) { "getResponse not support directory" }
        val selection = "${MediaStore.MediaColumns.DATA} = ?"
        val args = arrayOf(obtainPath(path))
        val list = query(selection, args)
        if (!list.isNullOrEmpty()) {
            return list.first()
        }
        return null
    }

    override fun getResponse(uri: Uri): FileResponse? {
        check(uri.scheme == "content") { "only support the 'content://' scheme" }
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
    override fun query(
        selection: String?,
        args: Array<String>?,
        sort: String?
    ): List<FileResponse>? {
        val cursor = resolver.query(
            fileUri, getProjection(), selection, args, sort
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
    protected fun getSuitableContentUri(standardDir: String?, mimeType: String): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            when (standardDir) {
                Environment.DIRECTORY_DCIM -> {
                    if (mimeType.contains("video")) videoUri else imageUri
                }
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
                Environment.DIRECTORY_DCIM -> {
                    if (mimeType.contains("video")) videoUri else imageUri
                }
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
}