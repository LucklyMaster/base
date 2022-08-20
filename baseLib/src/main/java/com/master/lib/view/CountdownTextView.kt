package com.master.lib.view

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import java.text.NumberFormat
import java.util.concurrent.TimeUnit

/**
 * 倒计时View
 * @author: MasterChan
 * @date: 2022-08-19 22:57
 */
open class CountdownTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    protected val thread = HandlerThread(javaClass.simpleName).apply { start() }
    protected val countDownHandler = Handler(thread.looper) { handleMessage(it) }

    /**
     * 输出格式
     */
    protected open var pattern = "ss"

    /**
     * 根据[pattern]得到的格式化类型
     */
    protected open var formatter = SS

    /**
     * 总计时毫秒数
     */
    protected var countMills = 10 * 1000L

    /**
     * 用于具体操作计数的毫秒数
     */
    protected open var operatorMills = countMills

    /**
     * 每次减少的毫秒数
     */
    protected open var interval = 1000L

    /**
     * 倒计时监听
     */
    protected open var onCountDownListener: OnCountDownListener? = null

    /**
     * 用于输出的数字格式化为多少位数
     */
    protected open val numberFormat: NumberFormat by lazy {
        NumberFormat.getNumberInstance().apply {
            minimumIntegerDigits = 2
            isGroupingUsed = false
        }
    }

    private companion object {
        const val DD_HH_MM_SS = 0
        const val HH_MM_SS = 1
        const val MM_SS = 2
        const val SS = 3
    }

    fun interface OnCountDownListener {
        fun onCountDown(current: Long, isCompleted: Boolean)
    }

    /**
     * 发送倒计时消息
     */
    protected open fun countDown() {
        countDownHandler.sendEmptyMessageDelayed(0, 1000)
    }

    /**
     * 设置一共的倒计时毫秒数
     * @param countMills Long
     * @return CountdownTextView
     */
    open fun setCountMills(countMills: Long) = apply {
        this.countMills = countMills
    }

    /**
     * 设置每次减少多少毫秒
     * @param interval Long
     * @return CountdownTextView
     */
    open fun setInterval(interval: Long) = apply {
        this.interval = interval
    }

    /**
     * 设置倒计时输出的格式，支持dd、HH、mm、ss的组合，限定了4种组合方式[SS]、[MM_SS]、
     * [HH_MM_SS]、[DD_HH_MM_SS]，此格式不限制数字位数吗，数字的输出位数由[setMinimumNumberDigits]设置
     * @param pattern String
     * @return CountdownTextView
     */
    open fun setPattern(pattern: String) = apply {
        this.pattern = pattern
        formatter = getExactlyFormatter()
    }

    /**
     * 设置输出的数字的位数，不足的在前面补0
     * @param digits 具体多少位
     * @return CountdownTextView
     */
    open fun setMinimumNumberDigits(digits: Int) = apply {
        numberFormat.minimumIntegerDigits = digits
    }

    open fun setListener(onCountDownListener: OnCountDownListener) = apply {
        this.onCountDownListener = onCountDownListener
    }

    protected open fun reset() {
        operatorMills = countMills
    }

    open fun start() {
        reset()
        formatMills(operatorMills)
        countDown()
    }

    open fun cancel() {
        countDownHandler.removeCallbacksAndMessages(null)
    }

    protected open fun handleMessage(msg: Message): Boolean {
        operatorMills -= interval
        operatorMills = if (operatorMills < 0) 0 else operatorMills
        formatMills(operatorMills)
        val completed = operatorMills <= 0
        onCountDownListener?.onCountDown(operatorMills, completed)
        if (!completed) {
            countDown()
        }
        return true
    }

    protected open fun getExactlyFormatter(): Int {
        val containsSec = pattern.contains("ss")
        val containsMin = pattern.contains("mm")
        val containsHour = pattern.contains("HH")
        val containsDay = pattern.contains("dd")
        if (containsSec && !containsMin && !containsHour && !containsDay) {
            return SS
        }
        if (containsSec && containsMin && !containsHour && !containsDay) {
            return MM_SS
        }
        if (containsSec && containsMin && containsHour && !containsDay) {
            return HH_MM_SS
        }
        if (containsSec && containsMin && containsHour && containsDay) {
            return DD_HH_MM_SS
        }
        return SS
    }

    protected open fun formatMills(mills: Long) {
        text = when (formatter) {
            SS -> {
                val seconds = TimeUnit.MILLISECONDS.toSeconds(mills)
                pattern.replace("ss", seconds.toString())
            }
            MM_SS -> {
                val minute = mills / (1 * 60 * 1000)
                val seconds = (mills % (1 * 60 * 1000)) / 1000
                pattern.replace("ss", "%1\$s")
                    .replace("mm", "%2\$s")
                    .format(numberFormat.format(seconds), numberFormat.format(minute))
            }
            HH_MM_SS -> {
                val hour = mills / (1 * 60 * 60 * 1000)
                val leftMills = mills % (1 * 60 * 60 * 1000)
                val minute = leftMills / (1 * 60 * 1000)
                val seconds = (leftMills % (1 * 60 * 1000)) / 1000
                pattern.replace("ss", "%1\$s")
                    .replace("mm", "%2\$s")
                    .replace("HH", "%3\$s")
                    .format(
                        numberFormat.format(seconds), numberFormat.format(minute),
                        numberFormat.format(hour)
                    )
            }
            DD_HH_MM_SS -> {
                val day = mills / (1 * 24 * 60 * 60 * 1000)
                val hour = (mills / (1 * 60 * 60 * 1000)) % 24
                val leftMills = mills % (1 * 60 * 60 * 1000)
                val minute = leftMills / (1 * 60 * 1000)
                val seconds = (leftMills % (1 * 60 * 1000)) / 1000
                pattern.replace("ss", "%1\$s")
                    .replace("mm", "%2\$s")
                    .replace("HH", "%3\$s")
                    .replace("dd", "%4\$s")
                    .format(
                        numberFormat.format(seconds), numberFormat.format(minute),
                        numberFormat.format(hour), numberFormat.format(day)
                    )
            }
            else -> ""
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        thread.quit()
    }
}