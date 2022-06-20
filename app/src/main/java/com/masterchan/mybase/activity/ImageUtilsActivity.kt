package com.masterchan.mybase.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.masterchan.lib.utils.BitmapUtils
import com.masterchan.mybase.GlideEngine
import com.masterchan.mybase.databinding.ActivityImageUtilsBinding


class ImageUtilsActivity : MyBaseActivity<ActivityImageUtilsBinding>(), View.OnClickListener {

    override fun onCreated(savedInstanceState: Bundle?) {
        setOnViewClickListeners(this) {
            arrayOf(btnSelector, btnLoad)
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnSelector -> {
                PictureSelector.create(this)
                    .openGallery(SelectMimeType.ofImage())
                    .setMaxSelectNum(1)
                    .setImageEngine(GlideEngine.createGlideEngine())
                    .forResult(object : OnResultCallbackListener<LocalMedia?> {
                        override fun onResult(result: ArrayList<LocalMedia?>) {
                            val localMedia = result.first()!!
                            binding.tvOri.text =
                                "原图：${localMedia.width}*${localMedia.height},${localMedia.size}k"
                            val bitmap = try {
                                BitmapUtils.getBitmap(localMedia.realPath)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                null
                            }
                            // val bitmap = BitmapFactory.decodeFile(localMedia.realPath)
                            binding.tvSel.text =
                                "加载图：${bitmap!!.width}*${bitmap!!.height},${bitmap.byteCount / 1024}k"
                            binding.ivImage.setImageBitmap(bitmap)
                        }

                        override fun onCancel() {}
                    })
            }
            binding.btnLoad -> {
            }
        }
    }
}