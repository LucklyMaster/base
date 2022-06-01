package com.masterchan.lib.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * @author MasterChan
 * @date 2021-12-01 17:06
 * @describe DateUtils
 */
object DateUtils {
    private const val YMD_HMS = "yyyy-MM-dd HH:mm:ss"

    fun Date.toString(format: String = YMD_HMS): String {
        return SimpleDateFormat(format, Locale.getDefault()).format(this)
    }

    fun String.toDate(format: String = YMD_HMS): Date? {
        return try {
            SimpleDateFormat(format, Locale.getDefault()).parse(this)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun Long.toDate(): Date {
        return Date(this)
    }

    /**
     * 获取当前的日期
     * @param format 日期格式，默认为 yyyy-MM-dd HH:mm:ss
     * @return 以[format]格式化的当前日期
     */
    @JvmStatic
    fun now(format: String = YMD_HMS): String {
        return Date().toString(format)
    }

    /**
     * Date格式化为字符串
     * @param date 需要格式化的日期
     * @param format 日期格式，默认为 yyyy-MM-dd HH:mm:ss
     * @return 以[format]格式化的日期
     */
    @JvmStatic
    fun format(date: Date, format: String = YMD_HMS): String {
        return date.toString(format)
    }

    /**
     * 时间戳格式化为字符串
     * @param mills 时间戳
     * @param format 日期格式，默认为 yyyy-MM-dd HH:mm:ss
     * @return 以[format]格式化的日期
     */
    @JvmStatic
    fun format(mills: Long, format: String = YMD_HMS): String {
        return mills.toDate().toString(format)
    }

    /**
     * 将字符串格式的日期解析为Date格式
     * @param dateStr 日期
     * @param format 日期的格式
     * @return Date? 如果[format]与[dateStr]格式对应错误，解析失败，返回null
     */
    @JvmStatic
    fun parse(dateStr: String, format: String = YMD_HMS): Date? {
        return dateStr.toDate(format)
    }

    /**
     * 获取某年某月的天数
     * @param year Int
     * @param month Int
     * @return 天数
     */
    @JvmStatic
    fun getDaysOfMonth(year: Int, month: Int): Int {
        val calendar = Calendar.getInstance()
        calendar[year, month - 1] = 1
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    /**
     * 获取特定时间对应月份的第几天，如[date]为null，则特定时间为当前时间
     * @param date Date?
     * @return Int
     */
    @JvmStatic
    fun dayOfMonth(date: Date? = null): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date ?: Date()
        return calendar[Calendar.DATE]
    }

    /**
     * 获取特定时间对应年份的第几个月，如[date]为null，则特定时间为当前时间
     * @param date Date?
     * @return Int
     */
    @JvmStatic
    fun monthOfYear(date: Date? = null): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date ?: Date()
        return calendar[Calendar.MONTH] + 1
    }

    /**
     * 获取特定时间的年份，如[date]为null，则特定时间为当前时间
     * @param date Date?
     * @return Int
     */
    @JvmStatic
    fun getYear(date: Date?): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date ?: Date()
        return calendar[Calendar.YEAR]
    }

    /**
     * 计算两个日期的日差，如果[date2]>[date1]，返回正数，否则返回负数
     * @param date1 Date
     * @param date2 Date
     * @return Long
     */
    @JvmStatic
    fun dayDiff(date1: Date, date2: Date): Long {
        return (date2.time - date1.time) / 86400000
    }

    /**
     * 计算两个日期的年差，如果[after]>[before]，返回正数，否则返回负数
     * @param before String
     * @param after String
     * @return Int
     */
    @JvmStatic
    fun yearDiff(before: Date, after: Date): Int {
        return getYear(after) - getYear(before)
    }

    /**
     * 获取某个日期一天的开始日期
     * @param date Date
     * @param format String
     * @return String
     */
    @JvmStatic
    fun getStartTimeOfDate(date: Date, format: String = YMD_HMS): String {
        val formatter = SimpleDateFormat(format, Locale.getDefault())
        val c1 = GregorianCalendar()
        try {
            c1.time = date
            c1[Calendar.HOUR_OF_DAY] = 0
            c1[Calendar.MINUTE] = 0
            c1[Calendar.SECOND] = 0
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return formatter.format(c1.time)
    }

    /**
     * 获得距当天前n天日期
     * @param date Date
     * @param day Int
     * @return Date
     */
    @JvmStatic
    fun getBeforeDate(date: Date, day: Int): Date {
        val cal = Calendar.getInstance()
        cal.timeInMillis = parse(getStartTimeOfDate(date))!!.time - 3600 * 24 * 1000 * day
        return cal.time
    }

    /**
     * 获得距当天后n天时间
     * @param date Date
     * @param day Int
     * @return Date
     */
    @JvmStatic
    fun getAfterDate(date: Date, day: Int): Date {
        val cal = Calendar.getInstance()
        cal.timeInMillis = parse(
            getStartTimeOfDate(date, YMD_HMS)
        )!!.time + 3600 * 24 * 1000 * day
        return cal.time
    }
}