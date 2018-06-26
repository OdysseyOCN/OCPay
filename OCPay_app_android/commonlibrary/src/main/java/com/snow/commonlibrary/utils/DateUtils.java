package com.snow.commonlibrary.utils;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static android.text.format.DateUtils.FORMAT_ABBREV_RELATIVE;
import static android.text.format.DateUtils.MINUTE_IN_MILLIS;
import static com.snow.commonlibrary.utils.StringUtil.isBlank;
import static com.snow.commonlibrary.utils.StringUtil.replaceBlank;

public class DateUtils {

    public static String defaultDatePattern = "yyyy-MM-dd";

    public static String yMdHmsDatePattern = "yyyy-MM-dd HH:mm:ss";


    public static String dMyDatePattern = "dd/MM/yyyy";

    public static String ydDatePattern = "yyyy-MM";

    public static String dmyhmsDatePattern = "dd/MM/yyyy  HH:mm:ss";

    public static String ymdhmDatePattern = "yyyy-MM-dd  HH:mm";

    public static String MdDatePattern = "MM/dd";


    public static String hhmmss = "HH:mm:ss";


    public static Long DAY = 1000l * 60 * 60 * 24;

    /**
     * 获得默认的 date pattern
     */
    public static String getDatePattern() {
        return defaultDatePattern;
    }

    /**
     * 返回预设Format的当前日期字符串
     */
    public static String getToday() {
        Date today = new Date();
        return format(today);
    }

    /**
     * 使用预设Format格式化Date成字符串
     */
    public static String formatYMDHMS(Date date) {
        return date == null ? " " : format(date, yMdHmsDatePattern);
    }

    /**
     * 使用预设Format格式化Date成字符串
     */
    public static String format(Date date) {
        return date == null ? " " : format(date, getDatePattern());
    }

    /**
     * 使用参数Format格式化Date成字符串
     */
    public static String format(Date date, String pattern) {
        return date == null ? " " : new SimpleDateFormat(pattern).format(date);
    }


    /**
     * 在日期上增加数个整月
     */
    public static Date addMonth(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, n);
        return cal.getTime();
    }

    public static String getLastDayOfMonth(String year, String month) {
        Calendar cal = Calendar.getInstance();
        // 年
        cal.set(Calendar.YEAR, Integer.parseInt(year));
        // 月，因为Calendar里的月是从0开始，所以要-1
        // cal.set(Calendar.MONTH, Integer.parseInt(month) - 1);
        // 日，设为一号
        cal.set(Calendar.DATE, 1);
        // 月份加一，得到下个月的一号
        cal.add(Calendar.MONTH, 1);
        // 下一个月减一为本月最后一天
        cal.add(Calendar.DATE, -1);
        return String.valueOf(cal.get(Calendar.DAY_OF_MONTH));// 获得月末是几号
    }


    public static Date addDay(Date date, int n) {

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE, n);//把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime();   //这个时间就是日期往后推一天的结果
        return date;
    }

    public static Date getDayWithAssignTime(int hour, int minute, int second) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);
        Date date = c.getTime();
        return date;
    }

    /**
     * 使用预设格式将字符串转为Date
     */
    public static Date parse(String strDate) throws ParseException {
        return isBlank(strDate) ? null : parse(strDate,
                getDatePattern());
    }

    /**
     * 使用参数Format将字符串转为Date
     */
    public static Date parse(String strDate, String pattern)
            throws ParseException {
        return isBlank(strDate) ? null : new SimpleDateFormat(
                pattern).parse(strDate);
    }

    public static Integer getAge(Date birthday) {
        Calendar calendar = new GregorianCalendar();
        Date now = new Date();
        calendar.setTime(now);
        int nowYear = calendar.get(Calendar.YEAR);
        calendar.setTime(birthday);
        int birthdayYear = calendar.get(Calendar.YEAR);

        return nowYear - birthdayYear;
    }

    public static Date addMinute(Date time, int minute) {
        Calendar c = Calendar.getInstance();
        c.setTime(time);
        c.add(Calendar.MINUTE, minute);
        Date date = c.getTime();
        return date;
    }

    public static Date addHour(Date time, int hour) {
        Calendar c = Calendar.getInstance();
        c.setTime(time);
        c.add(Calendar.HOUR_OF_DAY, hour);
        Date date = c.getTime();
        return date;
    }


    public static String formatStringDate(String time) {
        if (time == null) return null;
        return time.substring(0, 10);
    }

    /**
     * 获取指定时间的前一天
     *
     * @param date   日期
     * @param format 日期格式
     * @return
     */
    public static String getYesterday(Date date, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        date = calendar.getTime();
        return format(date, format);
    }


    /**
     * 获取当前时间的前几天或者后几天
     *
     * @param date 当前时间
     * @param days 要增加或减少的天数，正数为当前日期以后，负数为当前日期之前
     * @return
     */
    public static Date getCurrentBeforeOrAfter(Date date, int days) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        date = calendar.getTime();
        return date;
    }

    /**
     * @param date
     * @return
     */
    public static String getEnglishDate(Date date) {
        DateFormat df = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
        return df.format(date);
    }

    /**
     * unix时间戳转换成yMdHmsDatePattern
     *
     * @param timeStamp
     * @return
     */
    public static String TimeStamp2Date(Long timeStamp) throws ParseException {
        Long longTime = timeStamp * 1000;
        String date = new SimpleDateFormat(dMyDatePattern).format(new Date(longTime));
        return date.toString();
    }


    /**
     * unix时间戳转换成yMdHmsDatePattern
     *
     * @param timeStamp
     * @return
     */
    public static String TimeStamp2Custom(Long timeStamp, String formate) throws ParseException {
        Long longTime = getMillisTime(timeStamp);
        String date = new SimpleDateFormat(formate).format(new Date(longTime));
        return date.toString();
    }


    public static CharSequence getRelativeTimeSpan(Long time) {
        Long timeStamp = Long.valueOf(time);
        if (System.currentTimeMillis() / timeStamp > 100) {
            timeStamp = timeStamp * 1000;
        }
        return android.text.format.DateUtils.getRelativeTimeSpanString(timeStamp, System.currentTimeMillis(), MINUTE_IN_MILLIS, FORMAT_ABBREV_RELATIVE);
    }

    /**
     * custom  1day  is relative time  other
     */
    public static String getRelativeAndCustomSpan(Long timeStamp) throws ParseException {
        Long millisTime = getMillisTime(timeStamp);
        if (System.currentTimeMillis() - millisTime > DAY) {
            return TimeStamp2Custom(millisTime, MdDatePattern);
        } else {
            return getRelativeTimeSpan(millisTime).toString();

        }
    }

    public static Long getMillisTime(Long timestamp) {
        if (timestamp == 0) return System.currentTimeMillis();
        if (System.currentTimeMillis() / timestamp > 100) {
            timestamp = timestamp * 1000;
        }
        return timestamp;
    }

}
