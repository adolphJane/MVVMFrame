package com.adolph.project.baseutils

import android.util.Log
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object TimeUtil {
    private var formatBuilder: SimpleDateFormat? = null
    private val CHINESE_ZODIAC = arrayOf("猴", "鸡", "狗", "猪", "鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊")
    private val ZODIAC = arrayOf("水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "魔羯座")
    private val WEEK = arrayOf("周日", "周一", "周二", "周三", "周四", "周五", "周六")
    private const val WEEKDAYS = 7
    private val ZODIAC_FLAGS = intArrayOf(20, 19, 21, 21, 21, 22, 23, 23, 23, 24, 23, 22)

    /**
     * 英文简写如：2010
     */
    val FORMAT_Y = "yyyy"

    /**
     * 英文简写如：12:01
     */
    val FORMAT_HM = "HH:mm"

    /**
     * 英文简写如：12:01
     */
    val FORMAT_HMS = "HH:mm:ss"

    /**
     * 英文简写如：1-12 12:01
     */
    val FORMAT_MDHM = "MM-dd HH:mm"

    /**
     * 英文简写（默认）如：2010-12-01
     */
    val FORMAT_YMD = "yyyy-MM-dd"

    /**
     * 英文简写（默认）如：2010/12/01
     */
    val FORMAT_YMD_EXCEL = "yyyy/MM/dd"

    /**
     * 英文全称  如：2010-12-01 23:15
     */
    val FORMAT_YMDHM = "yyyy-MM-dd HH:mm"

    /**
     * 英文全称  如：2010-12-01 23:15:06
     */
    val FORMAT_YMDHMS = "yyyy-MM-dd HH:mm:ss"

    /**
     * 英文全称  如：2010-12-01-23-15-06
     */
    val FORMAT_YMDHMS_FILE = "yyyy-MM-dd-HH-mm-ss"

    /**
     * 精确到毫秒的完整时间    如：yyyy-MM-dd HH:mm:ss.S
     */
    val FORMAT_FULL = "yyyy-MM-dd HH:mm:ss.S"

    /**
     * 精确到毫秒的完整时间    如：yyyy-MM-dd HH:mm:ss.S
     */
    val FORMAT_FULL_SN = "yyyyMMddHHmmssS"

    /**
     * 中文简写  如：2010年12月01日
     */
    val FORMAT_YMD_CN = "yyyy年MM月dd日"

    /**
     * 中文简写  如：2010年12月01日  12时
     */
    val FORMAT_YMDH_CN = "yyyy年MM月dd日 HH时"

    /**
     * 中文简写  如：2010年12月01日  12时12分
     */
    val FORMAT_YMDHM_CN = "yyyy年MM月dd日 HH时mm分"

    /**
     * 中文全称  如：2010年12月01日  23时15分06秒
     */
    val FORMAT_YMDHMS_CN = "yyyy年MM月dd日  HH时mm分ss秒"

    /**
     * 精确到毫秒的完整中文时间
     */
    val FORMAT_FULL_CN = "yyyy年MM月dd日  HH时mm分ss秒SSS毫秒"


    /////////////////////////////////////////////////////////////////
    //                          Function                           //
    /////////////////////////////////////////////////////////////////

    /**
     * 获取北京时区
     * @return
     */
    fun getBeijingTimeZone(): TimeZone {
        return TimeZone.getTimeZone("GMT+8:00")
    }

    /**
     * 获取当前手机对应的系统时区
     *
     */
    fun getPhoneTimeZone(): TimeZone {
        return TimeZone.getDefault()
    }

    /**
     * 以“GMT+8：00”形式返回当前系统对应的时区
     * @return
     */
    fun getCurrentTimeZoneStr(): String {
        val tz = TimeZone.getDefault()
        return createGmtOffsetString(true, true, tz.rawOffset)
    }

    fun createGmtOffsetString(
        includeGmt: Boolean,
        includeMinuteSeparator: Boolean, offsetMillis: Int
    ): String {
        var offsetMinutes = offsetMillis / 60000
        var sign = '+'
        if (offsetMinutes < 0) {
            sign = '-'
            offsetMinutes = -offsetMinutes
        }
        val builder = StringBuilder(9)
        if (includeGmt) {
            builder.append("GMT")
        }
        builder.append(sign)
        appendNumber(builder, 2, offsetMinutes / 60)
        if (includeMinuteSeparator) {
            builder.append(':')
        }
        appendNumber(builder, 2, offsetMinutes % 60)
        return builder.toString()
    }

    private fun appendNumber(builder: StringBuilder, count: Int, value: Int) {
        val string = Integer.toString(value)
        for (i in 0 until count - string.length) {
            builder.append('0')
        }
        builder.append(string)
    }

    /**
     * 获取更改时区后的时间
     * @param date 时间
     * @param oldZone 旧时区
     * @param newZone 新时区
     * @return 时间
     */
    fun changeTimeZone(date: Date?, oldZone: TimeZone, newZone: TimeZone): Date? {
        var dateTmp: Date? = null
        if (date != null) {
            val timeOffset = oldZone.rawOffset - newZone.rawOffset
            dateTmp = Date(date.time - timeOffset)
        }
        return dateTmp
    }

    /**
     * 将北京时区的时间转化为当前系统对应时区的时间
     * @param beijingTime
     * @param format
     * @return
     */
    fun beijingTime2PhoneTime(beijingTime: String, format: String): String {
        val beijingDate = parseToDate(beijingTime, format)
        val phoneDate = changeTimeZone(beijingDate, getBeijingTimeZone(), getPhoneTimeZone())
        return formatDateToStr(phoneDate, format)
    }

    /**
     * 将日期字符串转换为Date对象
     * @param date 日期字符串，必须为"yyyy-MM-dd HH:mm:ss"
     * @param format 格式化字符串
     * @return 日期字符串的Date对象表达形式
     */
    fun parseToDate(date: String, format: String): Date? {
        var dt: Date? = null
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        try {
            dt = dateFormat.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return dt
    }

    /**
     * 将date----->String
     * 将Date对象转换为指定格式的字符串
     * @param date Date对象
     * @param //String format 格式化字符串
     * @return Date对象的字符串表达形式
     */
    fun formatDateToStr(date: Date?, format: String): String {
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        return dateFormat.format(date)
    }

    /** 格式化日期的标准字符串  */
    val Detail_Format = "yyyy-MM-dd HH:mm:ss"

    /**
     * 将本地系统对应时区的时间转换为上北京时区对应的时间
     * @param phoneTime
     * @return
     */
    fun phoneTime2BeijingTime(phoneTime: String): String {
        val phoneDate = parseToDate(phoneTime, Detail_Format)
        val beijingDate = changeTimeZone(phoneDate, getPhoneTimeZone(), getBeijingTimeZone())
        return formatDateToStr(beijingDate, Detail_Format)
    }


    /**
     * 当天的年月日
     * @return
     */
    fun todayYyyyMmDd(): String {
        formatBuilder = SimpleDateFormat(FORMAT_YMD, Locale.getDefault())
        return formatBuilder?.format(Date()) ?: ""
    }

    /**
     * 当天的时分秒
     * @return
     */
    fun todayHhMmSs(): String {
        formatBuilder = SimpleDateFormat(FORMAT_HMS, Locale.getDefault())

        return formatBuilder?.format(Date()) ?: ""
    }

    /**
     * 当天的年月日时分秒
     * @return
     */
    fun todayYyyyMmDdHhMmSs(): String {
        formatBuilder = SimpleDateFormat(FORMAT_YMDHMS, Locale.getDefault())

        return formatBuilder?.format(Date()) ?: ""
    }

    /**
     * 获取年
     * @param dateTime
     * @return
     */
    fun parseYyyy(dateTime: String): Int {
        return try {
            val e = Calendar.getInstance()
            formatBuilder = SimpleDateFormat(FORMAT_YMDHMS, Locale.getDefault())
            val date = formatBuilder?.parse(dateTime)
            e.time = date
            e.get(1)
        } catch (var3: ParseException) {
            var3.printStackTrace()
            0
        }

    }

    /**
     * 获取年
     * @param date
     * @return
     */
    fun parseYyyy(date: Date): Int {
        val cal = Calendar.getInstance()
        cal.time = date
        return cal.get(1)
    }

    /**
     * 获取月
     * @param dateTime
     * @return
     */
    fun parseMm(dateTime: String): Int {
        return try {
            val e = Calendar.getInstance()
            formatBuilder = SimpleDateFormat(FORMAT_YMDHMS, Locale.getDefault())
            val date = formatBuilder?.parse(dateTime)
            e.time = date
            e.get(2)
        } catch (var3: ParseException) {
            var3.printStackTrace()
            0
        }

    }

    /**
     * 获取月
     * @param date
     * @return
     */
    fun parseMm(date: Date): Int {
        val cal = Calendar.getInstance()
        cal.time = date
        return cal.get(2)
    }

    /**
     * 获取日
     * @param dateTime
     * @return
     */
    fun parseDd(dateTime: String): Int {
        return try {
            val e = Calendar.getInstance()
            formatBuilder = SimpleDateFormat(FORMAT_YMDHMS, Locale.getDefault())
            val date = formatBuilder?.parse(dateTime)
            e.time = date
            e.get(5)
        } catch (var3: ParseException) {
            var3.printStackTrace()
            0
        }

    }

    /**
     * 获取日
     * @param date
     * @return
     */
    fun parseDd(date: Date): Int {
        val cal = Calendar.getInstance()
        cal.time = date
        return cal.get(5)
    }

    /**
     * 获取年月日
     * @param dateTime
     * @return
     */
    fun parseYyyyMmDd(dateTime: String): String {
        var result = ""

        try {
            formatBuilder = SimpleDateFormat(FORMAT_YMD, Locale.getDefault())
            result = formatBuilder?.format(dateTime) ?: ""
        } catch (var3: ParseException) {
            var3.printStackTrace()
        }

        return result
    }

    /**
     * 获取年月日
     * @param date
     * @return
     */
    fun parseYyyyMmDd(date: Date): String {
        formatBuilder = SimpleDateFormat(FORMAT_YMD, Locale.getDefault())
        return formatBuilder?.format(date) ?: ""
    }

    /**
     * 时分秒
     * @param dateTime
     * @return
     */
    fun parseHhMmSs(dateTime: String): String {
        return try {
            formatBuilder = SimpleDateFormat(FORMAT_HMS, Locale.getDefault())
            formatBuilder?.format(dateTime) ?: ""
        } catch (var2: ParseException) {
            var2.printStackTrace()
            ""
        }

    }

    /**
     * 时分秒
     * @param date
     * @return
     */
    fun parseHhMmSs(date: Date): String {
        formatBuilder = SimpleDateFormat(FORMAT_HMS, Locale.getDefault())
        return formatBuilder?.format(date) ?: ""
    }

    /**
     * 获取星期几
     * @param dateTime
     * @return
     */
    fun getWeekNumber(dateTime: String): Int {
        formatBuilder = SimpleDateFormat(FORMAT_YMDHMS, Locale.getDefault())
        return getWeekNumber(string2Date(dateTime, formatBuilder))
    }

    /**
     * 获取星期几
     * @param dateTime
     * @param simpleDateFormat
     * @return
     */
    fun getWeekNumber(dateTime: String, simpleDateFormat: SimpleDateFormat): Int {
        return getWeekNumber(string2Date(dateTime, simpleDateFormat))
    }

    /**
     * 获取星期几
     * @param date
     * @return
     */
    fun getWeekNumber(date: Date?): Int {
        val cal = Calendar.getInstance()
        cal.time = date
        return cal.get(7)
    }

    /**
     * 日期中某个月份的第几周
     * @param dateTime
     * @return
     */
    fun getWeekOfMonth(dateTime: String): Int {
        formatBuilder = SimpleDateFormat(FORMAT_YMDHMS, Locale.getDefault())
        return getWeekOfMonth(string2Date(dateTime, formatBuilder))
    }

    /**
     * 日期中某个月份的第几周
     * @param dateTime
     * @param simpleDateFormat
     * @return
     */
    fun getWeekOfMonth(dateTime: String, simpleDateFormat: SimpleDateFormat): Int {
        return getWeekOfMonth(string2Date(dateTime, simpleDateFormat))
    }

    /**
     * 日期中某个月份的第几周
     * @param date
     * @return
     */
    fun getWeekOfMonth(date: Date?): Int {
        val cal = Calendar.getInstance()
        cal.time = date
        return cal.get(4)
    }

    /**
     * 日期中某个年份的第几周
     * @param time
     * @return
     */
    fun getWeekOfYear(time: String): Int {
        formatBuilder = SimpleDateFormat(FORMAT_YMDHMS, Locale.getDefault())
        return getWeekOfYear(string2Date(time, formatBuilder))
    }

    /**
     * 日期中某个年份的第几周
     * @param time
     * @param simpleDateFormat
     * @return
     */
    fun getWeekOfYear(time: String, simpleDateFormat: SimpleDateFormat): Int {
        return getWeekOfYear(string2Date(time, simpleDateFormat))
    }

    /**
     * 日期中某个年份的第几周
     * @param date
     * @return
     */
    fun getWeekOfYear(date: Date?): Int {
        val cal = Calendar.getInstance()
        cal.time = date
        return cal.get(3)
    }

    /**
     * 将年月日时分秒转成Long类型
     * @param dateTime
     * @return
     */
    fun dateTimeToTimeStamp(dateTime: String): Long {
        return try {
            formatBuilder = SimpleDateFormat(FORMAT_YMDHMS, Locale.getDefault())
            val e = formatBuilder?.parse(dateTime)
            java.lang.Long.valueOf(e?.time ?: 0L / 1000L)
        } catch (var2: ParseException) {
            var2.printStackTrace()
            java.lang.Long.valueOf(0L)
        }

    }

    /**
     * 将Long类型转成年月日时分秒
     * @param timeStamp
     * @return
     */
    fun timeStampToDateTime(timeStamp: Long): String {
        formatBuilder = SimpleDateFormat(FORMAT_YMDHMS, Locale.getDefault())
        return formatBuilder?.format(Date(timeStamp * 1000L)) ?: ""
    }

    /**
     * 将年月日时分秒转成Date类型
     * @param time
     * @return
     */
    fun string2Date(time: String): Date? {
        formatBuilder = SimpleDateFormat(FORMAT_YMDHMS, Locale.getDefault())
        return string2Date(time, formatBuilder)
    }

    /**
     * 将年月日时分秒转成Date类型
     * @param time
     * @param simpleDateFormat
     * @return
     */
    fun string2Date(time: String, simpleDateFormat: SimpleDateFormat?): Date? {
        return try {
            simpleDateFormat?.parse(time)
        } catch (var3: ParseException) {
            var3.printStackTrace()
            null
        }

    }

    /**
     * 将Date类型转成年月日时分秒
     * @param date
     * @return
     */
    fun date2String(date: Date): String {
        formatBuilder = SimpleDateFormat(FORMAT_YMDHMS, Locale.getDefault())
        return date2String(date, formatBuilder)
    }

    /**
     * 将Date类型转成年月日时分秒
     * @param date
     * @param simpleDateFormat
     * @return
     */
    fun date2String(date: Date, simpleDateFormat: SimpleDateFormat?): String {
        return simpleDateFormat?.format(date) ?: ""
    }

    /**
     * 比较日期
     * @param standDate
     * @param desDate
     * @return
     */
    fun dateIsBefore(standDate: String, desDate: String): Boolean {
        return try {
            formatBuilder = SimpleDateFormat(FORMAT_YMDHMS, Locale.getDefault())
            formatBuilder?.run {
                parse(desDate).before(parse(standDate))
            }
            false
        } catch (var3: ParseException) {
            var3.printStackTrace()
            false
        }

    }

    /**
     * 相差多少分钟
     * @param beginDate
     * @param endDate
     * @return
     */
    fun minutesBetweenTwoDate(beginDate: String, endDate: String): Long {
        val millisBegin = dateTimeToTimeStamp(beginDate)
        val millisEnd = dateTimeToTimeStamp(endDate)
        return (millisEnd - millisBegin) / 60L
    }

    /**
     * 获取日期中的生肖
     * @param dateTime
     * @return
     */
    fun getChineseZodiac(dateTime: String): String {
        val yyyy = parseYyyy(dateTime)
        return getChineseZodiac(yyyy)
    }

    /**
     * 获取日期中的生肖
     * @param date
     * @return
     */
    fun getChineseZodiac(date: Date): String {
        val yyyy = parseYyyy(date)
        return getChineseZodiac(yyyy)
    }

    /**
     * 获取日期中的生肖
     * @param year
     * @return
     */
    fun getChineseZodiac(year: Int): String {
        return CHINESE_ZODIAC[year % 12]
    }

    /**
     * 获取日期中的星座
     * @param dateTime
     * @return
     */
    fun getZodiac(dateTime: String): String {
        val dd = parseDd(dateTime)
        val month = parseMm(dateTime)
        return getZodiac(month, dd)
    }

    /**
     * 获取日期中的星座
     * @param date
     * @return
     */
    fun getZodiac(date: Date): String {
        val dd = parseDd(date)
        val month = parseMm(date)
        return getZodiac(month, dd)
    }

    /**
     * 获取日期中的星座
     * @param month
     * @param day
     * @return
     */
    fun getZodiac(month: Int, day: Int): String {
        return ZODIAC[if (day >= ZODIAC_FLAGS[month - 1]) month - 1 else (month + 10) % 12]
    }

    /**
     * 获取日期
     *
     * @param offset 表示偏移天数
     * @return
     */
    fun getNowDayOffset(offset: Int): String {
        val mCalendar = Calendar.getInstance()
        var time = mCalendar.timeInMillis
        time += offset * 24 * 3600 * 1000
        val myDate = Date(time)
        val df = SimpleDateFormat(FORMAT_YMD, Locale.getDefault())
        return df.format(myDate)
    }

    /**
     * 获取日期
     *
     * @param
     * @return
     */
    fun getTime(time: Long): String {
        val myDate = Date(time)
        val df = SimpleDateFormat(FORMAT_YMDHMS, Locale.getDefault())
        return df.format(myDate)
    }

    /**
     * 使指定日期向前走一天，变成“明天”的日期
     *
     * @param cal 等处理日期
     */
    fun forward(cal: Calendar) {
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)//0到11
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val days = getDaysOfMonth(year, month + 1)
        if (day == days) {//如果是本月最后一天，还要判断年份是不是要向前滚
            if (month == 11) {//如果是12月份，年份要向前滚
                cal.roll(Calendar.YEAR, true)
                cal.set(Calendar.MONTH, 0)//月份，第一月是0
                cal.set(Calendar.DAY_OF_MONTH, 1)

            } else {//如果不是12月份
                cal.roll(Calendar.MONTH, true)
                cal.set(Calendar.DAY_OF_MONTH, 1)
            }

        } else {
            cal.roll(Calendar.DAY_OF_MONTH, 1)//如果是月内，直接天数加1
        }
    }

    /**
     * 使日期倒一天
     *
     * @param cal
     */
    fun backward(cal: Calendar) {
        //计算上一月有多少天
        val month = cal.get(Calendar.MONTH)//0到11
        val year = cal.get(Calendar.YEAR)
        val days = getDaysOfMonth(year, month)//上个月的天数
        val day = cal.get(Calendar.DAY_OF_MONTH)
        if (day == 1) {//如果是本月第一天，倒回上一月
            if (month == 0) {//如果是本年第一个月，年份倒一天
                cal.roll(Calendar.YEAR, false)
                cal.set(Calendar.MONTH, 11)//去年最后一个月是12月
                cal.set(Calendar.DAY_OF_MONTH, 31)//12月最后一天总是31号
            } else {//月份向前
                cal.roll(Calendar.MONTH, false)
                cal.set(Calendar.DAY_OF_MONTH, days)//上个月最后一天
            }
        } else {
            cal.roll(Calendar.DAY_OF_MONTH, false)//如果是月内，日期倒一天
        }
    }

    /**
     * 判断平年闰年
     *
     * @param year
     * @return true表示闰年，false表示平年
     */
    fun isLeapYear(year: Int): Boolean {
        if (year % 400 == 0) {
            return true
        } else if (year % 100 != 0 && year % 4 == 0) {
            return true
        }
        return false
    }

    /**
     * 计算某月的天数
     *
     * @param year
     * @param month 现实生活中的月份，不是系统存储的月份，从1到12
     * @return
     */

    fun getDaysOfMonth(year: Int, month: Int): Int {
        if (month < 1 || month > 12) {
            return 0
        }
        val isLeapYear = isLeapYear(year)
        var daysOfMonth = 0
        when (month) {
            1, 3, 5, 7, 8, 10, 12 -> daysOfMonth = 31
            4, 6, 9, 11 -> daysOfMonth = 30
            2 -> if (isLeapYear) {
                daysOfMonth = 29
            } else {
                daysOfMonth = 28
            }
        }
        return daysOfMonth
    }

    /**
     * 获取当天凌晨的秒数
     *
     * @return
     */
    fun secondsMorning(c: Calendar): Long {
        val tempCalendar = Calendar.getInstance()
        tempCalendar.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0, 0)
        return tempCalendar.timeInMillis
    }

    /**
     * 获取第二天凌晨的秒数
     *
     * @return
     */
    fun secondsNight(c: Calendar): Long {
        val tempCalendar = Calendar.getInstance()
        tempCalendar.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0, 0)
        forward(tempCalendar)
        return tempCalendar.timeInMillis
    }

    /**
     * 判断某两天是不是同一天
     *
     * @param c1
     * @param c2
     * @return
     */
    fun isSameDay(c1: Calendar, c2: Calendar): Boolean {

        if (c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR))
            return false
        if (c1.get(Calendar.MONTH) != c2.get(Calendar.MONTH))
            return false
        if (c1.get(Calendar.DAY_OF_MONTH) != c2.get(Calendar.DAY_OF_MONTH))
            return false
        return true
    }

    private val MINUTE = (60 * 1000).toLong()// 1分钟
    private val HOUR = 60 * MINUTE// 1小时
    private val DAY = 24 * HOUR// 1天
    private val MONTH = 31 * DAY// 月
    private val YEAR = 12 * MONTH// 年

    /** Log输出标识  */
    private val TAG = TimeUtil.javaClass.simpleName

    /**
     * 将日期格式化成友好的字符串：几分钟前、几小时前、几天前、几月前、几年前、刚刚
     *
     * @param date
     * @return
     */
    fun formatFriendly(date: Date?): String? {
        if (date == null) {
            return null
        }
        val diff = Date().time - date.time
        var r: Long = 0
        if (diff > YEAR) {
            r = diff / YEAR
            return r.toString() + "年前"
        }
        if (diff > MONTH) {
            r = diff / MONTH
            return r.toString() + "个月前"
        }
        if (diff > DAY) {
            r = diff / DAY
            return r.toString() + "天前"
        }
        if (diff > HOUR) {
            r = diff / HOUR
            return r.toString() + "个小时前"
        }
        if (diff > MINUTE) {
            r = diff / MINUTE
            return r.toString() + "分钟前"
        }
        return "刚刚"
    }

    /**
     * 将日期以yyyy-MM-dd HH:mm:ss格式化
     *
     * @param dateL
     * 日期
     * @return
     */
    fun formatDateTime(dateL: Long): String {
        val sdf = SimpleDateFormat(FORMAT_YMDHMS, Locale.getDefault())
        val date = Date(dateL)
        return sdf.format(date)
    }

    /**
     * 将日期以yyyy-MM-dd HH:mm:ss格式化
     *
     * @param dateL
     * 日期
     * @return
     */
    fun formatDateTime(dateL: Long, formater: String): String {
        val sdf = SimpleDateFormat(formater, Locale.getDefault())
        return sdf.format(Date(dateL))
    }

    /**
     * 将日期以yyyy-MM-dd HH:mm:ss格式化
     *
     * @param formater
     * 日期
     * @return
     */
    fun formatDateTime(date: Date, formater: String): String {
        val sdf = SimpleDateFormat(formater, Locale.getDefault())
        return sdf.format(date)
    }

    /**
     * 将日期字符串转成日期
     *
     * @param strDate
     * 字符串日期
     * @return java.util.date日期类型
     */

    fun parseDate(strDate: String): Date? {
        val dateFormat = SimpleDateFormat(FORMAT_YMDHMS, Locale.getDefault())
        var returnDate: Date? = null
        try {
            returnDate = dateFormat.parse(strDate)
        } catch (e: ParseException) {
            LogUtils.v(TAG, "parseDate failed !")

        }

        return returnDate

    }

    /**
     * 获取系统当前日期
     *
     * @return
     */
    fun gainCurrentDate(): Date {
        return Date()
    }

    /**
     * 验证日期是否比当前日期早
     *
     * @param target1
     * 比较时间1
     * @param target2
     * 比较时间2
     * @return true 则代表target1比target2晚或等于target2，否则比target2早
     */
    fun compareDate(target1: Date, target2: Date): Boolean {
        var flag = false
        try {
            val target1DateTime = formatDateTime(
                target1,
                FORMAT_YMDHMS
            )
            val target2DateTime = formatDateTime(
                target2,
                FORMAT_YMDHMS
            )
            if (target1DateTime.compareTo(target2DateTime) <= 0) {
                flag = true
            }
        } catch (e1: Exception) {
            LogUtils.e("比较失败，原因：" + e1.message)
        }

        return flag
    }

    /**
     * 对日期进行增加操作
     *
     * @param target
     * 需要进行运算的日期
     * @param hour
     * 小时
     * @return
     */
    fun addDateTime(target: Date?, hour: Double): Date? {
        return if (null == target || hour < 0) {
            target
        } else Date(target.time + (hour * 60.0 * 60.0 * 1000.0).toLong())

    }

    /**
     * 对日期进行相减操作
     *
     * @param target
     * 需要进行运算的日期
     * @param hour
     * 小时
     * @return
     */
    fun subDateTime(target: Date?, hour: Double): Date? {
        return if (null == target || hour < 0) {
            target
        } else Date(target.time - (hour * 60.0 * 60.0 * 1000.0).toLong())

    }

    /**
     * 格式化excel中的时间
     * @param date
     * @return
     */
    fun formatDateForExcelDate(date: Date): String {
        formatBuilder = SimpleDateFormat(FORMAT_YMD_EXCEL, Locale.getDefault())
        return formatBuilder?.format(date) ?: ""
    }

    /**
     * 将日期格式化作为文件名
     * @param date
     * @return
     */
    fun formatDateForFileName(date: Date): String {
        formatBuilder = SimpleDateFormat(FORMAT_YMDHMS_FILE, Locale.getDefault())
        return formatBuilder?.format(date) ?: ""
    }

    /**
     * 格式化日期(精确到秒)
     *
     * @param date
     * @return
     */
    fun formatDateSecond(date: Date): String {
        formatBuilder = SimpleDateFormat(FORMAT_YMDHMS, Locale.getDefault())
        return formatBuilder?.format(date) ?: ""
    }

    /**
     * 格式化日期(精确到秒)
     *
     * @param date
     * @return
     */
    fun tempDateSecond(date: Date): String {
        formatBuilder = SimpleDateFormat(FORMAT_YMDHMS, Locale.getDefault())
        return formatBuilder?.format(date) ?: ""
    }

    /**
     * 格式化日期(精确到秒)
     *
     * @param str
     * @return
     */
    fun tempDateSecond(str: String): Date {
        try {
            formatBuilder = SimpleDateFormat(FORMAT_YMDHMS, Locale.getDefault())
            formatBuilder?.run {
                return parse(str)
            }
            return Date()
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return Date()
    }

    /**
     * 格式化日期(精确到天)
     *
     * @param date
     * @return
     */
    fun formatDateDay(date: Date): String {
        formatBuilder = SimpleDateFormat(FORMAT_YMD, Locale.getDefault())
        return formatBuilder?.format(date) ?: ""
    }

    /**
     * 格式化日期(精确到天)
     *
     * @param date
     * @return
     */
    fun formatDateDetailDay(date: Date): String {
        formatBuilder = SimpleDateFormat(FORMAT_YMD_CN, Locale.getDefault())
        return formatBuilder?.format(date) ?: ""
    }

    /**
     * 将double类型的数字保留两位小数（四舍五入）
     *
     * @param number
     * @return
     */
    fun formatNumber(number: Double): String {
        val df = DecimalFormat()
        df.applyPattern("#0.00")
        return df.format(number)
    }

    /**
     * 将字符串转换成日期
     *
     * @param date
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun formateDate(date: String): Date {
        formatBuilder = SimpleDateFormat(FORMAT_YMD, Locale.getDefault())
        return formatBuilder?.parse(date) ?: Date()
    }

    /**
     * 将字符日期转换成Date
     * @param date
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun parseStringToDate(date: String): Date {
        formatBuilder = SimpleDateFormat(FORMAT_YMD, Locale.getDefault())
        return formatBuilder?.parse(date) ?: Date()
    }

    /**
     * 将double日期转换成String
     * @param number
     * @return
     */
    fun formatDoubleNumber(number: Double): String {
        val df = DecimalFormat("#")
        return df.format(number)
    }

    /**
     * 获得指定Date类型的毫秒数
     * @param date 指定的Date
     * @return 指定Date类型的毫秒数
     */
    fun getTimeMillis(date: Date): Long {
        return date.time
    }

    /**
     * 获得当前时间的毫秒数
     * @return 当前时间的毫秒数
     */
    fun getCurrentDayTimeMillis(): Long {
        return System.currentTimeMillis()
    }

    /**
     * 将格式化过的时间串转换成毫秒
     * @param day 将格式化过的时间
     * @param format 格式化字符串
     * @return 毫秒
     */
    fun convertMillisecond(day: String?, format: String?): Long {
        if (day == null || format == null)
            return 0
        val formatter = SimpleDateFormat(format, Locale.getDefault())
        try {
            val dt = formatter.parse(day)
            return dt.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return 0
    }

    /**
     * 得到两个日期的天数
     * @param sdate1 日期一
     * @param sdate2 日期二
     * @return 天数
     */
    fun getDateInterval(sdate1: String, sdate2: String): Int {
        var date1: Date? = null
        var date2: Date? = null
        var betweenDays: Long = 0

        try {
            date1 = SimpleDateFormat(FORMAT_YMD, Locale.getDefault()).parse(sdate1)
            date2 = SimpleDateFormat(FORMAT_YMD, Locale.getDefault()).parse(sdate2)

            val beginTime = date1!!.time
            val endTime = date2!!.time
            betweenDays = (endTime - beginTime) / (1000 * 60 * 60 * 24)

        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return betweenDays.toInt()
    }

    /**
     * 时间比较
     * @param format 格式化字符串
     * @param time1 时间1
     * @param time2 时间2
     * @return time1比time2早返回-1,time1与time2相同返回0,time1比time2晚返回1
     */
    fun compareTime(format: String?, time1: String?, time2: String?): Int {
        if (format == null || time1 == null || time2 == null)
            return 0
        val formatter = SimpleDateFormat(format, Locale.getDefault())
        val c1 = Calendar.getInstance()
        val c2 = Calendar.getInstance()

        try {
            c1.time = formatter.parse(time1)
            c2.time = formatter.parse(time2)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return c1.compareTo(c2)
    }

    /**
     * 获得时区偏移量 相对GMT RFC 82
     *
     * @param date
     * @return
     */
    fun time_offset(date: Date): String {
        return String.format("%tz", date)
    }

    /**
     * 获得下午或上午
     *
     * @param date
     * @return
     */
    fun am_or_pm(date: Date): String {
        return String.format("%tp", date)
    }

    /**
     * 获得当前微妙数 9位
     *
     * @param date
     * @return
     */
    fun subtle(date: Date): String {
        return String.format("%tN", date)
    }

    /**
     * 获得当前毫秒数 3位
     *
     * @param date
     * @return
     */
    fun mill(date: Date): String {
        return String.format("%tL", date)
    }

    /**
     * 获得当前秒 2位
     *
     * @param date
     * @return
     */
    fun second(date: Date): String {
        return String.format("%tS", date)
    }

    /**
     * 获得当前分钟 2位
     *
     * @param date
     * @return
     */
    fun minute(date: Date): String {
        return String.format("%tM", date)
    }

    /**
     * 获得当前小时 1-12
     *
     * @param date
     * @return
     */
    fun hour_l(date: Date): String {
        return String.format("%tl", date)
    }

    /**
     * 获得当前小时 00-23
     *
     * @param date
     * @return
     */
    fun hour_H(date: Date): String {
        return String.format("%tH", date)
    }

    /**
     * 获得当前时间 15:25
     *
     * @param date
     * @return
     */
    fun hour_minute(date: Date): String {
        return String.format("%tR", date)
    }

    /**
     * 获得当前时间 15:23:50
     *
     * @param date
     * @return
     */
    fun hour_minute_second(date: Date): String {
        return String.format("%tT", date)
    }

    /**
     * 获得当前时间 03:22:06 下午
     *
     * @param date
     * @return
     */
    fun hour_minute_second_pm_or_am(date: Date): String {
        return String.format("%tr", date)
    }

    /**
     * 获取当前时间到日 03/25/08（月/日/年）
     *
     * @param date
     * @return
     */
    fun mdy(date: Date): String {
        return String.format("%tD", date)
    }

    /**
     * 获取当前时间到日 2008-03-25 年—月—日
     *
     * @param date
     * @return
     */
    fun ymd(date: Date): String {
        return String.format("%tF", date)
    }

    /**
     * 获得日期天 1-31
     *
     * @param date
     * @return
     */
    fun day_one(date: Date): String {
        return String.format("%te", date)
    }

    /**
     * 获得日期天 01-31
     *
     * @param date
     * @return
     */
    fun day_two(date: Date): String {
        return String.format("%td", date)
    }

    /**
     * 一年中的第几天 085
     *
     * @param date
     * @return
     */
    fun day_to_year(date: Date): String {
        return String.format("%tj", date)
    }

    /**
     * 获得月份简称
     */
    fun month_referred(date: Date): String {
        return String.format("%tb", date)
    }

    /**
     * 获得月份全称
     *
     * @param date
     * @return
     */
    fun month_full_name(date: Date): String {
        return String.format("%tB", date)
    }

    /**
     * 获得月份 01-12
     *
     * @param date
     * @return
     */
    fun month(date: Date): String {
        return String.format("%tm", date)
    }

    /**
     * 获得星期简称
     *
     * @param date
     * @return
     */
    fun week_referred(date: Date): String {
        return String.format("%ta", date)
    }

    /**
     * 获得星期全称
     *
     * @param date
     * @return
     */
    fun week_full_name(date: Date): String {
        return String.format("%tA", date)
    }

    /**
     * 获得年简称 16
     *
     * @param date
     * @return
     */
    fun year_referred(date: Date): String {
        return String.format("%ty", date)
    }

    /**
     * 获得年全称 2016
     *
     * @param date
     * @return
     */
    fun year_full_name(date: Date): String {
        return String.format("%tY", date)
    }

    /**
     * 星期二 三月 25 13:37:22 CST 2016
     *
     * @param date
     * @return
     */
    fun time(date: Date): String {
        return String.format("%tc", date)
    }

    /**
     * 获取时间戳到秒
     *
     * @param date
     * @return
     */
    fun time_to_second(date: Date): String {
        return String.format("%ts", date)
    }

    /**
     * 获取时间戳到毫秒
     *
     * @param date
     * @return
     */
    fun time_to_mill(date: Date): String {
        return String.format("%tQ", date)
    }

    /**
     * 获取时间戳到毫秒
     *
     * @return
     */
    fun time_to_mill(): Long {
        return System.currentTimeMillis()
    }


    /**
     * 身份证号转生日
     *
     * @param identityCard 身份证
     * @return 生日
     */
    fun identityCard2Date(identityCard: String): Date? {
        try {
            val dateStr: String
            if (identityCard.length == 18) {
                dateStr = identityCard.substring(6, 14)// 截取18位身份证身份证中生日部分
                return formatDateString(dateStr, "yyyyMMdd")
            }
            if (identityCard.length == 15) {
                dateStr = identityCard.substring(6, 12)// 截取15位身份证中生日部分
                return formatDateString(dateStr, "yyMMdd")
            }
            return null
        } catch (e: Exception) {
            return null
        }

    }

    /**
     * 格式化日期时间字符串
     *
     * @param dateString 日期时间字符串
     * @param pattern    模式
     * @return Date对象
     */
    fun formatDateString(dateString: String, pattern: String): Date? {
        return try {
            val format = SimpleDateFormat(pattern, Locale.getDefault())
            format.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * UTM转换成带描述的日期
     *
     * @param milliseconds milliseconds
     * @return UTM转换成带描述的日期
     */

    fun getDisplayTimeAndDesc(milliseconds: Long): String {
        val formatBuilder = SimpleDateFormat(FORMAT_HM, Locale.getDefault())
        val time = formatBuilder.format(milliseconds)
        val sb = StringBuffer()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliseconds
        val hour = calendar.get(Calendar.HOUR_OF_DAY).toLong()
        Log.v("---------->---", System.currentTimeMillis().toString() + "----------" + milliseconds)
        val datetime = System.currentTimeMillis() - milliseconds
        val day = Math.ceil((datetime.toFloat() / 24f / 60f / 60f / 1000.0f).toDouble()).toLong()// 天前
        Log.v("day---hour---time---", day.toString() + "----" + hour + "-----" + time)

        if (day <= 7) {// 一周内
            if (day == 0L) {// 今天
                if (hour <= 4) {
                    sb.append(" 凌晨 $time")
                } else if (hour in 5..6) {
                    sb.append(" 早上 $time")
                } else if (hour in 7..11) {
                    sb.append(" 上午 $time")
                } else if (hour in 12..13) {
                    sb.append(" 中午 $time")
                } else if (hour in 14..18) {
                    sb.append(" 下午 $time")
                } else if (hour in 19..19) {
                    sb.append(" 傍晚 $time")
                } else if (hour in 20..24) {
                    sb.append(" 晚上 $time")
                } else {
                    sb.append("今天 $time")
                }
            } else if (day == 1L) {// 昨天
                sb.append("昨天 $time")
            } else if (day == 2L) {// 前天
                sb.append("前天 $time")
            } else {
                sb.append(DateToWeek(milliseconds)!! + time)
            }
        } else {// 一周之前
            sb.append((day % 7).toString() + "周前")
        }
        Log.v("sb---", sb.toString() + "")
        return sb.toString()

    }

    /**
     * 日期变量转成对应的星期字符串
     *
     * @param milliseconds data
     * @return 日期变量转成对应的星期字符串
     */
    fun DateToWeek(milliseconds: Long): String? {

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliseconds
        val dayIndex = calendar.get(Calendar.DAY_OF_WEEK)
        return if (dayIndex < 1 || dayIndex > WEEKDAYS) {
            null
        } else WEEK[dayIndex - 1]

    }

    /**
     * 将时间间隔转换成描述性字符串，如2天前，3月1天后等。
     *
     * @param toDate 相对的日期
     * @param isFull 是否全部显示 true 全部显示 false 简单显示
     * @return 将时间间隔转换成描述性字符串，如2天前，3月1天后等。
     */
    fun diffDateAsDesc(toDate: Date, isFull: Boolean): String {
        var diffDesc = ""
        var fix = ""
        var diffTime: Long?
        val curDate = Date()
        if (curDate.time > toDate.time) {
            diffTime = curDate.time - toDate.time
            fix = "前"
        } else {
            diffTime = toDate.time - curDate.time
            fix = "后"
        }

        //换算成分钟数，防止Int溢出。
        diffTime = diffTime / 1000 / 60

        val year = diffTime / (60 * 24 * 30 * 12)
        diffTime %= (60 * 24 * 30 * 12)
        if (year > 0) {
            diffDesc = diffDesc + year + "年"
            if (!isFull) {
                return diffDesc + fix
            }
        }

        val month = diffTime / (60 * 24 * 30)
        diffTime %= (60 * 24 * 30)
        if (month > 0) {
            diffDesc = diffDesc + month + "月"
            if (!isFull) {
                return diffDesc + fix
            }
        }

        val day = diffTime / 60 / 24
        diffTime %= (60 * 24)
        if (day > 0) {
            diffDesc = diffDesc + day + "天"
            if (!isFull) {
                return diffDesc + fix
            }
        }

        val hour = diffTime / 60
        diffTime %= 60
        if (hour > 0) {
            diffDesc = diffDesc + hour + "时"
            if (!isFull) {
                return diffDesc + fix
            }
        }

        if (diffTime > 0) {
            diffDesc = diffDesc + diffTime + "分"
            if (!isFull) {
                return diffDesc + fix
            }
        }

        return diffDesc + fix
    }


    /**
     * 返回两个距离某个值得时间差
     *
     * @param nowTime 毫秒值
     * @param startTIme1 毫秒值
     */
    fun timeDifference(nowTime: Long, startTIme1: String): String? {
        val formatBuilder = SimpleDateFormat(FORMAT_YMDHMS,Locale.getDefault())
        val startTime = formatBuilder.format(nowTime)
        val endTime = formatBuilder
            .format(Date(java.lang.Long.parseLong(startTIme1)))//如果服务器返回的时间是unix时间，单位是秒，而java中获取得单位是毫秒，需要注意
        // 按照传入的格式生成一个simpledateformate对象
        val sd = SimpleDateFormat(FORMAT_YMDHMS, Locale.getDefault())
        val nd = (1000 * 24 * 60 * 60).toLong()// 一天的毫秒数
        val nh = (1000 * 60 * 60).toLong()// 一小时的毫秒数
        val nm = (1000 * 60).toLong()// 一分钟的毫秒数
        val ns: Long = 1000// 一秒钟的毫秒数
        val diff: Long
        return try {
            // 获得两个时间的毫秒时间差异
            diff = sd.parse(endTime).time - sd.parse(startTime).time
            val day = diff / nd// 计算差多少天
            val hour = diff % nd / nh// 计算差多少小时
            val min = diff % nd % nh / nm// 计算差多少分钟
            val sec = diff % nd % nh % nm / ns// 计算差多少秒
            day.toString() + "天" + hour + "时" + min + "分" + sec + "秒"
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 检查日期是否有效
     * @param year 年
     * @param month 月
     * @param day 日
     * @return boolean
     */
    fun getDateIsTrue(year: String, month: String, day: String): Boolean {
        try {
            val data = year + month + day
            formatBuilder = SimpleDateFormat(FORMAT_YMD, Locale.getDefault())
            formatBuilder?.isLenient = false
            formatBuilder?.parse(data)
        } catch (e: ParseException) {
            e.printStackTrace()
            LogUtils.e("TimeUtil -->>getDateIsTrue", e.message.toString())
            return false
        }

        return true
    }

    /**
     * 获取当前时间的下个月份(格式：yyyy-MM-dd)
     * @return String 当前时间下个月份
     */
    fun getCalendarTodayNextMonth(): String {
        var year = 0
        var moth = 0
        var day = 0
        val c = Calendar.getInstance()
        year = c.get(Calendar.YEAR)
        moth = c.get(Calendar.MONTH) + 1
        day = c.get(Calendar.DAY_OF_MONTH) + 2
        return year.toString() + "-" + moth + "-" + day
    }

    /**
     * 判断日期是否属于今天日期(精确到天)
     * @param sDate 日期值
     * @return boolean 返回true表示是，false表示不是
     */
    fun getSysIsToday(sDate: String): Boolean {
        var falg = false
        try {
            var date: Date? = null
            formatBuilder = SimpleDateFormat(FORMAT_YMDHMS, Locale.getDefault())
            date = formatBuilder?.parse(sDate)
            val today = Date()
            if (date != null) {
                val nowDate = formatBuilder?.format(today)
                val timeDate = formatBuilder?.format(date)
                if (nowDate == timeDate) {
                    falg = true
                }
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return falg
    }

    /**
     * 判断两个字符串日期的前后
     * @param strdate1  字符串时间1
     * @param strdate2  字符串时间2
     * @return boolean
     * 日期与时间
     */
    fun getDateIsBefore(strdate1: String, strdate2: String): Boolean {
        try {
            val df = SimpleDateFormat(FORMAT_YMDHMS, Locale.getDefault())
            LogUtils.i("AppSysDateMgr-->>getDateIsBefore-->>strdate1: ", strdate1)
            LogUtils.i("AppSysDateMgr-->>getDateIsBefore-->>strdate2: ", strdate2)
            return df.parse(strdate1).before(df.parse(strdate2))
        } catch (e: ParseException) {
            e.printStackTrace()
            LogUtils.e("AppSysDateMgr-->>getDateIsBefore", e.message.toString())
            return false
        }

    }

    /**
     * 判断两个字符串日期的前后
     * @param strdate1  字符串时间1
     * @param strdate2  字符串时间2
     * @return boolean
     */
    fun getDateIsEqual(strdate1: String, strdate2: String): Boolean {
        try {
            val df = SimpleDateFormat(FORMAT_YMDHMS, Locale.getDefault())
            return df.parse(strdate1) == df.parse(strdate2)
        } catch (e: ParseException) {
            e.printStackTrace()
            LogUtils.e("AppSysDateMgr-->>getDateIsBefore", e.message.toString())
            return false
        }

    }

    /**
     * 判断两个字符串日期的前后
     * @param Longdate1  字符串时间1
     * @param Longdate2  字符串时间2
     * @return boolean
     */
    fun getDateIsBefore(Longdate1: Long?, Longdate2: Long?): Boolean {
        var Longdate1 = Longdate1
        var Longdate2 = Longdate2
        try {
            LogUtils.i("AppSysDateMgr-->>getDateIsBefore-->>strdate1: ", Longdate1!!.toString() + "")
            LogUtils.i("AppSysDateMgr-->>getDateIsBefore-->>strdate2: ", Longdate2!!.toString() + "")
            Longdate1 = Longdate1 ?: 0
            Longdate2 = Longdate2 ?: 0
            return if (Longdate1 > Longdate2) true else false
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtils.e("AppSysDateMgr-->>getDateIsBefore", e.message.toString())
            return false
        }

    }

    /**
     * 判断两个时间日期的前后
     * @param date1  日期1
     * @param date2  日期2
     * @return boolean
     */
    fun getDateIsBefore(date1: Date, date2: Date): Boolean {
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return getDateIsBefore(df.format(date1), df.format(date2))
    }
}