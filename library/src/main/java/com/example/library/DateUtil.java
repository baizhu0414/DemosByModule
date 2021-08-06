package com.example.library;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateUtil {

    public static final String FORMAT_ONE = "yyyy-MM-dd HH:mm:ss";
    /**
     * 获取日期工具类
     * @return 年－月－日 小时：分钟：秒
     */
    public static String getCurrentDateFormatOne() {
        Calendar today = Calendar.getInstance();
        return dateToString(today.getTime(), FORMAT_ONE);
    }

    /**
     * 把日期转换为字符串
     * @param date 当前日期
     * @param format {@see FORMAT_ONE}
     * @return 规定格式的日期字符串
     */
    public static String dateToString(java.util.Date date, String format) {
        String result = "";
        SimpleDateFormat formater = new SimpleDateFormat(format, Locale.getDefault());
        try {
            result = formater.format(date);
        } catch (Exception e) {
            // log.error(e);
        }
        return result;
    }

}
