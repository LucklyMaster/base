package com.mc.lib.log

import com.mc.lib.utils.DateUtils
import kotlin.math.min

/**
 * 实现打印策略接口
 * @author: MasterChan
 * @date: 2022-05-29 12:43
 */
open class MPrintStrategy(private val printer: IPrinter) : IPrintStrategy {

    /**
     * 方框的线条长度
     */
    private val borderLength = 160

    /**
     * 文本起始打印位置与border开始处的距离
     */
    private val beforeTextSpaceLength = 1

    /**
     * 文本开始打印位置与border开始处的空格
     */
    private val beforeTextSpace by lazy { " ".repeat(beforeTextSpaceLength) }

    /**
     * 方框坐标线条
     */
    private val leftBorder = "┃"

    private val normalBorder by lazy { "━".repeat(borderLength - 2) }

    /**
     * 方框顶部线条┏━━━━━━━━━━━━━━━━━━━━━━━━━━┓
     */
    private val topBorder by lazy { "┏$normalBorder┓" }

    /**
     * 方框底部线条┗━━━━━━━━━━━━━━━━━━━━━━━━━━┛
     */
    private val bottomBorder by lazy { "┗$normalBorder┛" }

    /**
     * 调用栈和打印文本之间的分割线
     */
    private val divider by lazy { "-".repeat(borderLength - 2) }

    /**
     * [normalBorder]的字节长度
     */
    private val borderByteSize by lazy { normalBorder.toByteArray(Charsets.UTF_16BE).size }

    override fun println(
        priority: Int,
        tag: String,
        content: String,
        stackTrace: StackTraceElement?
    ) {
        //打印顶部
        printLog(priority, tag, topBorder)
        //打印调用栈
        printStackTrace(priority, tag, stackTrace)
        //打印分割线
        printLog(priority, tag, leftBorder.plus(divider))
        //打印content
        printContent(priority, tag, content)
        //打印底部
        printLog(priority, tag, bottomBorder)
    }

    private fun printLog(priority: Int, tag: String, content: String) {
        printer.println(priority, tag, content)
    }

    /**
     * 打印调用堆栈
     * @param priority Int
     * @param tag String
     */
    protected fun printStackTrace(priority: Int, tag: String, stackTrace: StackTraceElement?) {
        val element = stackTrace ?: getTargetStackTraceElement()
        var content = "$leftBorder$beforeTextSpace${DateUtils.now("yyyy-MM-dd HH:mm:ss.SS")}"
        if (element == null) {
            printer.println(priority, tag, content)
            return
        }
        val className = element.className.substringAfterLast(".")
        content = content.plus(
            " $className.${element.methodName}(${element.fileName}:${element.lineNumber})"
        )
        printer.println(priority, tag, content)
    }

    /**
     * 获取当前的调用堆栈
     * @return StackTraceElement?
     */
    protected fun getTargetStackTraceElement(): StackTraceElement? {
        var traceElement: StackTraceElement? = null
        var shouldTrace = false
        val stackTrace = Thread.currentThread().stackTrace
        for (stackTraceElement in stackTrace) {
            val isLogMethod = stackTraceElement.className == MLog.javaClass.name
            if (shouldTrace && !isLogMethod) {
                traceElement = stackTraceElement
                break
            }
            shouldTrace = isLogMethod
        }
        return traceElement
    }

    protected fun printContent(priority: Int, tag: String, content: String) {
        //先按照默认的一行打印
        val lines = content.split(System.getProperty("line.separator") ?: "\n")
        lines.forEach {
            if (it.isEmpty()) {
                return@forEach
            }
            //当前需要打印的内容
            val contentByte = it.toByteArray(Charsets.UTF_16BE)
            //logcat中显示日志时，中文与"━"字符是2:1的关系，即2个"━"
            //设置一个换行的比例
            var maxPrintLength = (borderByteSize * 0.8f).toInt()
            //UTF_16BE一个中文、英文都为2字节，为了保证截取后打印汉字没有乱码，截取的长度必须为偶数
            maxPrintLength = if (maxPrintLength % 2 == 0) maxPrintLength else maxPrintLength - 1

            //如果当前行的内容长度大于了最大可打印长度，截取最大可打印长度分行打印
            if (contentByte.size > maxPrintLength) {
                //内容一共被截取的长度
                var tempLen = 0
                while (tempLen < contentByte.size) {
                    //内容被截取后剩余长度
                    val leftLen = contentByte.size - tempLen
                    //内容可以被截取的长度
                    val subLen = min(leftLen, maxPrintLength)
                    //截取内容
                    val subContent = String(contentByte, tempLen, subLen, Charsets.UTF_16BE)
                    //打印内容
                    printLog(priority, tag, leftBorder.plus(beforeTextSpace).plus(subContent))
                    tempLen += subLen
                }
            } else {
                printLog(priority, tag, leftBorder.plus(beforeTextSpace).plus(it))
            }
        }
    }
}