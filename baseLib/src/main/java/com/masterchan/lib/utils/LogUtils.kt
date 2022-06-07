package com.masterchan.lib.utils

import android.util.Log
import java.io.*
import java.util.concurrent.*

/**
 * 日志操作类
 * @author MasterChan
 * @date 20.8.26 16:09
 */
@Suppress("MemberVisibilityCanBePrivate")
class LogUtils private constructor() {

    private val mInitBorder = " " + """
^^^^^^^^^^^^^less code,less bug^^^^^^^^^^^^^^
                   _ooOoo_
                  o8888888o
                  88" . "88
                  (| -_- |)
                  O\  =  /O
               ____/`---'\____
             .'  \\|     |//  `.
            /  \\|||  :  |||//  \
           /  _||||| -:- |||||-  \
           |   | \\\  -  /// |   |
           | \_|  ''\---/''  |   |
           \  .-\__  `-`  ___/-. /
         ___`. .'  /--.--\  `. . __
      ."" '<  `.___\_<|>_/___.'  >'"".
     | | :  `- \`.;`\ _ /`;.`/ - ` : | |
     \  \ `-.   \_ __\ /__ _/   .-` /  /
======`-.____`-.___\_____/___.-`____.-'======
                   `=---='
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
            佛祖保佑       永无BUG
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
"""
    private val mBorderLength = 104
    private val mTextBeforeSpace = 2
    private val mTopBorder = "┏" + "━".repeat(mBorderLength - 2) + "┓"
    private val mBottomBorder = "┗" + "━".repeat(mBorderLength - 2) + "┛"
    private val mDivider = "-".repeat(mBorderLength - 2)
    private val mNormalBorder = "┃"
    private var mTag = javaClass.simpleName
    private var mDebug = true
    private var mSaveLog = false
    private var mLogSuffix = "txt"
    private var mLogFileDir = ""
    private var mSaveHandler = SaveHandler()

    /**
     * 设置log文件大小 k
     */
    private var mLogSize = 2 * 1024 * 1024L
    private var mExecutor: ExecutorService? = null

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            LogUtils()
        }
    }

    inner class SaveHandler {
        fun handler(log: String): String {
            return log
        }
    }

    init {
        Log.d(mTag, mInitBorder)
    }

    fun debug(debug: Boolean): LogUtils {
        mDebug = debug
        return this
    }

    fun setTag(tag: String): LogUtils {
        mTag = tag
        return this
    }

    fun saveLog(saveLog: Boolean): LogUtils {
        mSaveLog = saveLog
        return this
    }

    fun setLogSize(logSize: Int): LogUtils {
        mLogSize = logSize.toLong()
        return this
    }

    fun setLogSuffix(suffix: String): LogUtils {
        mLogSuffix = suffix
        return this
    }

    fun setLogDir(logDir: String): LogUtils {
        if (logDir.isNotEmpty()) {
            mLogFileDir = logDir
        }
        return this
    }

    fun setSaveHandler(handler: SaveHandler): LogUtils {
        mSaveHandler = handler
        return this
    }

    fun v(any: Any) {
        v(mTag, any)
    }

    fun d(any: Any) {
        d(mTag, any)
    }

    fun i(any: Any) {
        i(mTag, any)
    }

    fun w(any: Any) {
        w(mTag, any)
    }

    fun e(any: Any) {
        e(mTag, any)
    }

    fun t(t: Throwable) {
        t(mTag, t)
    }

    fun v(tag: String, any: Any) {
        print(tag, any, Log.VERBOSE)
    }

    fun d(tag: String, any: Any) {
        print(tag, any, Log.DEBUG)
    }

    fun i(tag: String, any: Any) {
        print(tag, any, Log.INFO)
    }

    fun w(tag: String, any: Any) {
        print(tag, any, Log.WARN)
    }

    fun e(tag: String, any: Any) {
        print(tag, any, Log.ERROR)
    }

    fun t(tag: String, throwable: Throwable) {
        val stackTrace = getTargetStackTraceMsg()
        val space = " ".repeat(mTextBeforeSpace)
        val sb = StringBuilder(" \n$mTopBorder\n")
            .append("$mNormalBorder$space${DateUtils.now()} $stackTrace\n")
            .append("$mNormalBorder$mDivider\n")
        val throwableMsg = getThrowableMsg(throwable)
        throwableMsg.lines().filter { it.isNotEmpty() }.forEach {
            sb.append(mNormalBorder.plus(it).plus("\n"))
        }
        sb.append(mBottomBorder)
        if (mDebug) {
            Log.e(tag, sb.toString())
        }
        if (mSaveLog) {
            doSaveLog(stackTrace, throwableMsg)
        }
    }

    private fun print(tag: String, any: Any, logLevel: Int) {
        val text = any2String(any)
        val stackTrace = getTargetStackTraceMsg()
        if (mDebug) {
            printString(tag, stackTrace, text, logLevel)
        }
        if (mSaveLog) {
            doSaveLog(stackTrace, text)
        }
    }

    private fun any2String(any: Any): String {
        return when (any) {
            is Array<*> -> any.contentToString()
            else -> any.toString()
        }
    }

    private fun printString(tag: String, stackTrace: String, msg: String, logLevel: Int) {
        var newMsg = msg
        var text = " \n$mTopBorder\n"
        val space = " ".repeat(mTextBeforeSpace)
        text += "$mNormalBorder$space${DateUtils.now()} $stackTrace\n"
        text += "$mNormalBorder$mDivider\n"
        //最大可打印长度
        var maxPrintLength = mBorderLength - 1 - mTextBeforeSpace
        //打印时一个汉字为两个字节，为了保证截取后没有乱码，截取的长度必须为偶数
        maxPrintLength = if (maxPrintLength % 2 == 0) maxPrintLength else maxPrintLength - 1
        //当前需要打印文本的长度
        var msgLength = newMsg.toByteArray(charset("GB2312"))
            .toString(Charsets.ISO_8859_1).length
        while (msgLength > maxPrintLength) {
            //将文本按照最大可打印长度分割
            var tempMsg = newMsg.toByteArray(charset("GB2312"))
                .toString(Charsets.ISO_8859_1)
            tempMsg = tempMsg.substring(0, maxPrintLength)
                .toByteArray(Charsets.ISO_8859_1)
                .toString(charset("GB2312"))

            text += "$mNormalBorder$space$tempMsg\n"
            newMsg = newMsg.replace(tempMsg, "")
            msgLength = newMsg.toByteArray(charset("GB2312"))
                .toString(Charsets.ISO_8859_1).length
        }
        text += "$mNormalBorder$space$newMsg\n"
        text += mBottomBorder

        when (logLevel) {
            Log.VERBOSE -> Log.v(tag, text)
            Log.DEBUG -> Log.d(tag, text)
            Log.INFO -> Log.i(tag, text)
            Log.WARN -> Log.w(tag, text)
            Log.ERROR -> Log.e(tag, text)
        }
    }

    private fun getThrowableMsg(throwable: Throwable): String {
        val writer: Writer = StringWriter()
        val printWriter = PrintWriter(writer)
        throwable.printStackTrace(printWriter)
        printWriter.close()
        val reader = BufferedReader(StringReader(writer.toString()))
        val sb = StringBuilder()
        var text: String?
        try {
            while (reader.readLine().also { text = it } != null) {
                sb.append("\n$text")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return sb.toString()
    }

    private fun printThrowable(tag: String, stackTrace: String, throwable: Throwable) {
        val writer: Writer = StringWriter()
        val printWriter = PrintWriter(writer)
        throwable.printStackTrace(printWriter)
        printWriter.close()
        val reader = BufferedReader(StringReader(writer.toString()))
        val space = " ".repeat(mTextBeforeSpace)
        val sb = StringBuilder(" \n$mTopBorder\n").append(space)
            .append("$mNormalBorder$space${DateUtils.now()} $stackTrace\n")
            .append("$mNormalBorder$mDivider\n")
        var text: String?
        try {
            while (reader.readLine().also { text = it } != null) {
                sb.append("\n").append(mNormalBorder).append(text)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        sb.append("\n").append(mBottomBorder)
        Log.e(tag, sb.toString())
    }

    private fun getTargetStackTraceMsg(): String {
        val element = getTargetStackTraceElement()
        return if (element != null) {
            var className = element.className
            val index = className.lastIndexOf(".")
            className = className.substring(index + 1)
            "$className.${element.methodName}(${element.fileName}:${element.lineNumber})"
        } else {
            ""
        }
    }

    private fun getTargetStackTraceElement(): StackTraceElement? {
        var targetStackTrace: StackTraceElement? = null
        var shouldTrace = false
        val stackTrace = Thread.currentThread().stackTrace
        for (stackTraceElement in stackTrace) {
            val isLogMethod = stackTraceElement.className == LogUtils::class.java.name
            if (shouldTrace && !isLogMethod) {
                targetStackTrace = stackTraceElement
                break
            }
            shouldTrace = isLogMethod
        }
        return targetStackTrace
    }

    private fun doSaveLog(stackTrace: String, log: String) {
        if (mExecutor == null) {
            mExecutor = ThreadPoolExecutor(
                0,
                20,
                30L,
                TimeUnit.SECONDS,
                SynchronousQueue(),
                ThreadFactory { Thread(it, "saveLog") })
        }
        mExecutor?.execute {
            val date = DateUtils.now("yyyy-MM-dd")
            val fileList = FileUtils.filter(FileUtils.orderByDate(File(mLogFileDir), true), date)
            val filepath: String
            if (fileList.isNotEmpty()) {
                val length = FileUtils.getFileSize(fileList[0].absolutePath)
                if (length > mLogSize) {
                    val index = fileList[0].nameWithoutExtension.replace("log_${date}_", "").toInt()
                    filepath = "$mLogFileDir/log_${date}_${index + 1}.$mLogSuffix"
                    FileUtils.createFile(filepath)
                } else {
                    filepath = fileList[0].absolutePath
                }
            } else {
                filepath = "$mLogFileDir/log_${date}_1.$mLogSuffix"
                FileUtils.createFile(filepath)
            }
            var text = "${DateUtils.now()} $stackTrace\n"
            text += "$log\n"
            text = mSaveHandler.handler(text)
            FileUtils.writeFile(filepath, text, true)
        }
    }
}