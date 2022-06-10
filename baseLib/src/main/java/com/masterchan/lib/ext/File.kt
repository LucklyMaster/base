package com.masterchan.lib.ext

import android.webkit.MimeTypeMap
import androidx.documentfile.provider.DocumentFile
import java.io.File

/**
 * 推测文件的mimeType
 * @receiver File
 * @return String?
 */
fun File.guessMimeType(): String? {
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
}

/**
 * 创建文件，如果文件夹不存在，和文件夹一起创建
 * @receiver File
 * @return Boolean
 */
fun File.create(): Boolean {
    if (exists()) {
        return true
    }
    val parent = parentFile
    if (parent != null && !parent.exists()) {
        if (!parent.mkdirs()) {
            return false
        }
    }
    return try {
        createNewFile()
    } catch (tr: Throwable) {
        tr.printStackTrace()
        false
    }
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
    if (isFile) {
        return delete()
    }
    listFiles()?.isNotEmpty { forEach { if (it.isFile) it.delete() else it.deleteAll() } }
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