package com.master.mybase.activity

import android.os.Bundle
import com.master.lib.base.BaseVBActivity
import com.master.mybase.databinding.ActivityCountdownBinding

class CountdownActivity : BaseVBActivity<ActivityCountdownBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //10秒
        binding.tvSec.setCountMills(10 * 1000)
            .setPattern("ss")
            .setListener { _, complete -> if (complete) binding.tvSec.start() }
            .start()
        //2分12秒
        binding.tvMinute.setCountMills(2 * 60 * 1000 + 12 * 1000)
            .setMinimumNumberDigits(1)
            .setPattern("mm:ss")
            .start()
        //60小时10分45秒
        binding.tvHour.setCountMills(60 * 60 * 60 * 1000 + 10 * 60 * 1000 + 45 * 1000)
            .setInterval(3000)
            .setPattern("HH时mm分ss秒")
            .start()
        //2天1小时1分45秒
        binding.tvDay.setCountMills(
            2 * 24 * 60 * 60 * 1000 + 1 * 60 * 60 * 1000 + 1 * 60 * 1000 + 45 * 1000
        )
            .setPattern("dd:HH:mm:ss")
            .setMinimumNumberDigits(1)
            .start()
        binding.tvSec.setOnClickListener {
            val tag = it.getTag(Int.MAX_VALUE - 1)
            if (tag != true) {
                binding.tvSec.cancel()
                it.setTag(Int.MAX_VALUE - 1, true)
            } else {
                binding.tvSec.start()
                it.setTag(Int.MAX_VALUE - 1, false)
            }
        }
    }
}