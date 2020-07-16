package com.young.weexbase.utils

import android.annotation.SuppressLint
import android.text.TextUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Administrator on 2018/3/11.
 */
object DateUtils {

    private val FORMAT_YMDHMS = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    private val FORMAT_YMDHM = SimpleDateFormat("yyyy-MM-dd HH:mm")
    private val FORMAT_YMD = SimpleDateFormat("yyyy-MM-dd")
    private val FORMAT_YM = SimpleDateFormat("yyyy-MM")
    private val FORMAT_HMS = SimpleDateFormat("HH:mm:ss")
    private val FORMAT_HM = SimpleDateFormat("HH:mm")
    private val FORMAT_MD_CH = SimpleDateFormat("MM月dd日")
    private val FORMAT_MDHM = SimpleDateFormat("MM-dd:HH:mm")
    private val FORMAT_Y = SimpleDateFormat("yyyy")
    private val YMDHMS = SimpleDateFormat("yyyyMMddHHmmss")

    fun getNow4(date: Date): String {
        val now4 = FORMAT_HM.format(date)
        return now4
    }

    fun getDate(dateStr: String): Date {
        var date = Date()
        if (TextUtils.isEmpty(dateStr)) {
            return date
        }
        try {
            date = FORMAT_YMDHMS.parse(dateStr)
            return date
        } catch (e: ParseException) {
            e.printStackTrace()

        }

        return date

    }

    fun getDate_2(dateStr: String): Date {
        var date = Date()
        if (TextUtils.isEmpty(dateStr)) {
            return date
        }
        try {
            date = FORMAT_YMD.parse(dateStr)
            return date
        } catch (e: ParseException) {
            e.printStackTrace()

        }

        return date

    }

    fun getDataString_1(date: Date?): String {
        var date = date
        if (date == null) {
            date = Date()
        }
        val str = FORMAT_YMDHMS.format(date)
        return str

    }

    fun getDataStringYMDHM(timeStamp: Long): String {
        var date = Date(timeStamp)
        return FORMAT_YMDHM.format(date)


    }

    fun getDataString_2(date: Date?): String {

        val str = FORMAT_YMD.format(date ?: Date())
//        LogUtils.d(TAG, str)
        return str

    }

    fun getDateMD_CH(date: Date?): String {

        var str = FORMAT_MD_CH.format(date ?: Date())
        if (str[0].equals("0")) {
            str = str.substring(1, str.length - 1)
        }
        return str

    }

    fun getDataString_MDHM(date: Date?): String {
        var time = ""
        var str = FORMAT_MDHM.format(date ?: Date()).trim()
        if (str[0].equals('0')) {
            time = str.substring(1, str.length)
        } else {
            time = str
        }
        return time

    }

    fun decodeTolong_1(string: String): Long {
        var date: Date? = null
        try {
            date = FORMAT_YMDHMS.parse(string)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date!!.time
    }

    fun decodeTolong_2(string: String): Long {
        var date: Date? = null
        try {
            date = FORMAT_YMD.parse(string)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return date!!.time
    }

    fun getDataString_APMT(date: Date?): String {
        var date = date
        if (date == null) {
            date = Date()
        }
        val str = FORMAT_YM.format(date)
        return str

    }

    fun getDataString_3(date: Date?): String {
        var date = date
        if (date == null) {
            date = Date()
        }
        val str = FORMAT_HMS.format(date)
        return str

    }

    fun getDataString_4(date: Date?): String {
        var date = date
        if (date == null) {
            date = Date()
        }
        val str = FORMAT_HM.format(date)
        return str

    }
    fun getDataStringYMDHMS(date: Date?): String {
        var date = date
        if (date == null) {
            date = Date()
        }
        val str = YMDHMS.format(date)
        return str

    }

    fun dateToWeek(datetime: String): String {
        val f = SimpleDateFormat("yyyy-MM-dd")
        val weekDays = arrayOf("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六")
        val cal = Calendar.getInstance() // 获得一个日历
        var datet: Date? = null
        try {
            datet = f.parse(datetime)
            cal.time = datet
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        var w = cal.get(Calendar.DAY_OF_WEEK) - 1 // 指示一个星期中的某天。
        if (w < 0)
            w = 0
        return weekDays[w]
    }

    fun getDatebyYM(year: Int, month: Int): String {
        val sb = StringBuffer()
        sb.append(year).append("年").append(month).append(month)
        return sb.toString()
    }

    fun formatDuration(s: Int): String {
        var time = ""
        var minute = 0
        var hour = 0
        var secend = s
        if (secend < 3600) {
            time = String.format("%1$02d:%2$02d", secend / 60, secend % 60)
        } else {
            time = String.format("%1$02d:%2$02d:%3$02d", secend / 3600, secend % 3600 / 60, secend % 60)

        }

        return time
    }

    @SuppressLint("WrongConstant")
    fun getSupportBeginDayofMonth(year: Int, monthOfYear: Int): Date {
        val cal = Calendar.getInstance()
        // 不加下面2行，就是取当前时间前一个月的第一天及最后一天
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, monthOfYear)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)

        cal.add(Calendar.DAY_OF_MONTH, -1)
        val lastDate = cal.time

        cal.set(Calendar.DAY_OF_MONTH, 1)
        val firstDate = cal.time
        return firstDate
    }

    @SuppressLint("WrongConstant")
    fun getSupportEndDayofMonth(year: Int, monthOfYear: Int): Date {
        val cal = Calendar.getInstance()
        // 不加下面2行，就是取当前时间前一个月的第一天及最后一天
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, monthOfYear)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)

        cal.add(Calendar.DAY_OF_MONTH, -1)
        val lastDate = cal.time

        cal.set(Calendar.DAY_OF_MONTH, 1)
        val firstDate = cal.time
        return lastDate
    }

    /**
     * 1s==1000ms
     */
    private val TIME_MILLISECONDS = 1000

    /**
     * 时间中的分、秒最大值均为60
     */
    private val TIME_NUMBERS = 60

    /**
     * 时间中的小时最大值
     */
    private val TIME_HOURSES = 24

    /**
     * 获取当前时间距离指定日期时差的大致表达形式
     *
     * @param long date 日期
     * @return 时差的大致表达形式
     */
    fun getDetailTimeForToday(date: Long): String {
        val todayStartTime = FORMAT_YMD.parse(FORMAT_YMD.format(Date())).time
        val yestodayStartTime = todayStartTime - TIME_HOURSES * TIME_NUMBERS * TIME_NUMBERS * TIME_MILLISECONDS
        val thisYearStartTime = FORMAT_Y.parse(FORMAT_Y.format(Date())).time

        return when {
            date >= todayStartTime -> getDataString_4(Date(date))
            date in yestodayStartTime until todayStartTime -> "昨天"
            date in thisYearStartTime until yestodayStartTime -> getDateMD_CH(Date(date))
            else -> getDataString_1(Date(date))
        }
    }
     fun getTodayZero(): Long {
        val date = Date()
        val everyDay = (24 * 60 * 60 * 1000).toLong() //每天的毫秒数
        //date.getTime()是现在的毫秒数，它 减去 当天零点到现在的毫秒数（ 现在的毫秒数%一天总的毫秒数，取余。），理论上等于零点的毫秒数，不过这个毫秒数是UTC+0时区的。
        //减8个小时的毫秒值是为了解决时区的问题。
        val nowTime = date.time
        val zeroTime = nowTime!! - (nowTime + 8 * 60 * 60 * 1000) % everyDay
        return zeroTime
    }
}