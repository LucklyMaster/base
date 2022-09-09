package com.master.mybase.activity

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import com.master.lib.base.BaseVBActivity
import com.master.lib.ext.*
import com.master.mybase.R
import com.master.mybase.databinding.ActivitySpanBinding

class SpanActivity : BaseVBActivity<ActivitySpanBinding>(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setOnViewClickListeners(this) {
            arrayOf(btnOne, btnTwo, btnThree, btnFour, btnFive, btnSix, btnSeven)
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnOne -> {
                binding.tvContent.text = "今天天气好".toSizeSpan(IntRange(0, 1), sp2px(30f))
                    .toColorSpan(IntRange(1, 2), Color.RED)
                    .toBackgroundColorSpan(IntRange(2, 3), Color.BLUE)
                    .toStrikethroughSpan(IntRange(3, 4))
                    .toClickSpan(IntRange(3, 4)) { toast("点击了文字") }
                    .toStyleSpan(IntRange(4, 5), Typeface.BOLD)
                binding.tvContent.movementMethod = LinkMovementMethod.getInstance()
            }
            binding.btnTwo -> {
                binding.tvContent.text = "正在测试"
                binding.tvContent.appendSizeSpan("富", 20f)
                    .appendSizeSpan("文本".toUnderlineSpan(0..2), 16f)
                    .appendColorSpan("扩展", Color.RED)
                    .appendImageSpan(
                        R.mipmap.ic_launcher, imageWidth = dp2pxi(20), imageHeight = dp2pxi(20)
                    )
                    .appendSizeSpan("，", 16f)
                    .appendStyleSpan("请", Typeface.BOLD)
                    .appendBackgroundColorSpan("点击", Color.RED)
                    .appendClickSpan("此处") { toast("检测到点击事件") }
            }
        }
    }

}