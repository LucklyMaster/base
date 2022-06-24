package com.master.lib.ext

import android.webkit.MimeTypeMap
import androidx.documentfile.provider.DocumentFile
import java.io.File

/**
 * 根据文件扩展名，推测mimeType，如果是自定义的扩展名，则返回null
 * @receiver File
 * @return String?
 */
val File.mimeType: String?
    get() {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }

fun File.createDirs(): Boolean {
    if (exists()) {
        return true
    }
    return mkdirs()
}

/**
 * 创建文件或文件夹，创建文件时，如果路径中的文件夹不存在，一同创建
 * @receiver File
 * @return Boolean
 */
fun File.create(): Boolean {
    if (exists()) {
        return true
    }
    if (isDirectory) {
        return this.createDirs()
    }
    val parent = parentFile
    if (parent != null && !parent.exists()) {
        if (!parent.createDirs()) {
            return false
        }
    }
    return createNewFile()
}

/**
 * 获取文件夹下包括子文件夹的所有文件
 * @receiver File
 * @return List<File>
 */
fun File.listFilesWithChildDir(): List<File> {
    val fileList = mutableListOf<File>()
    val pathList = listFiles()
    if (!pathList.isNullOrEmpty()) {
        pathList.forEach {
            fileList.add(it)
            if (it.isDirectory) {
                fileList.addAll(it.listFilesWithChildDir())
            }
        }
    }
    return fileList
}

/**
 * 递归删除当前文件/文件夹下的所有文件
 * @receiver File
 * @return Boolean
 */
fun File.deleteAll(): Boolean {
    if (!exists()) {
        return true
    }
    if (!isDirectory) {
        return delete()
    }
    val fileList = listFiles()
    fileList?.forEach {
        if (it.isDirectory) it.deleteAll() else it.delete()
    }
    return delete()
}

fun File.renameTo(fileName: String): Boolean {
    return DocumentFile.fromFile(this).renameTo(fileName)
}

fun File.copyTo(path: String): Boolean {
    return renameTo(File(path))
}

fun File.moveTo(path: String): Boolean {
    return copyTo(path) && delete()
}