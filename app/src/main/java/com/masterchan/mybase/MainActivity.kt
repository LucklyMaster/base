package com.masterchan.mybase

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import com.masterchan.lib.ext.setOnClickListeners
import com.masterchan.lib.sandbox.Sandbox
import com.masterchan.mybase.databinding.ActivityMainBinding
import java.io.ByteArrayOutputStream

class MainActivity : MyBaseActivity<ActivityMainBinding>(), View.OnClickListener {

    override fun onCreated(savedInstanceState: Bundle?) {
        setOnClickListeners(this, binding.keyboard, binding.imageUtils)
    }

    override fun onClick(v: View?) {
        val clazz = when (v) {
            binding.keyboard -> AutoFoldKeyboardActivity::class.java
            binding.imageUtils -> ImageUtilsActivity::class.java
            else -> MainActivity::class.java
        }
        // startActivity(clazz)
       /* val cv = ContentValues()
        cv.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        cv.put(MediaStore.MediaColumns.DISPLAY_NAME, "a.png")
        val uri = contentResolver.insert(MediaStore.Images.Media.getContentUri("external"), cv)
        val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.img)
        val os = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
        contentResolver.openOutputStream(uri!!)?.write(os.toByteArray())*/
        Sandbox.FILES.listFile()
        /* val cv = ContentValues()
         // cv.put(MediaStore.Files.FileColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
         // cv.put(MediaStore.Files.FileColumns.DISPLAY_NAME, "c.txt")
         // val uri = contentResolver.insert(MediaStore.Files.getContentUri("external"), cv)
         // Log.d("写入文件uri = $uri")
         // Log.d("写入文件path = ${uri!!.path}")
         // contentResolver.openOutputStream(uri!!)?.write("正在测试中".toByteArray())
         val selection = MediaStore.Files.FileColumns.RELATIVE_PATH + " like ?"
         // val args = arrayOf("${Environment.DIRECTORY_DOCUMENTS}")
         val args = arrayOf(Environment.DIRECTORY_DOCUMENTS+"%")
         val cursor = contentResolver.query(
             MediaStore.Files.getContentUri("external"), null, selection, args, null
         )
         if (cursor != null && cursor.moveToFirst()) {
             do {
                 val index = cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)
                 val id = cursor.getLong(index)
                 val uri = MediaStore.Files.getContentUri("external", id)
                 MLog.d("uri =${DocumentFile.fromSingleUri(this, uri)?.name}")

                 val relativePath = cursor.getColumnIndex(MediaStore.Files.FileColumns.RELATIVE_PATH)
                 Log.d("relativePath = ${cursor.getString(relativePath)}")
                 val data = cursor.getColumnIndex(MediaStore.MediaColumns.DATA)
                 // Log.d("path = ${cursor.getString(relativePath)}${cursor.getString(name)}")
                 Log.d("path2 = ${cursor.getString(data)}")

             } while (cursor.moveToNext())
         }
         cursor?.close()*/

        // val content = contentResolver.openInputStream(uri2!!)?.readBytes()?.toString(Charsets.UTF_8)
        // Log.d("content = ${content}")
    }

    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }
}