package com.example.library;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    static final String[] rWeekStrings = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
    static final String FORMAT_ONE = "yyyy-MM-dd HH:mm:ss";
    static final String FORMAT_TWO = "yyyy-MM-dd";
    static final String FORMAT_THREE = "HH:mm";

    /**
     * 获取日期工具类
     *
     * @return 年－月－日 小时：分钟：秒
     */
    public static String getCurrentDateFormatOne() {
        Calendar today = Calendar.getInstance();
        return dateToString(today.getTime(), FORMAT_ONE);
    }

    /**
     * 获取日期工具类
     *
     * @return 年－月－日
     */
    public static String getCurrentDateFormatTwo() {
        Calendar today = Calendar.getInstance();
        return dateToString(today.getTime(), FORMAT_TWO);
    }

    /**
     * 把日期转换为字符串
     *
     * @param date   当前日期
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

    /**
     * 判断日期是否与当前日期差7天
     *
     * @param date 年-月-日
     * @return true
     */
    public static boolean isDateOutDate(String date) {
        try {
            SimpleDateFormat formater = new SimpleDateFormat(FORMAT_TWO, Locale.getDefault());
            if (((new Date()).getTime() - formater.parse(date).getTime())
                    > 7 * 24 * 60 * 60 * 1000) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 返回当前的时间
     *
     * @return 时：分
     */
    public static String getCurrentDateFormatThree() {
        SimpleDateFormat formatter = new SimpleDateFormat(FORMAT_THREE, Locale.getDefault());
        return formatter.format(System.currentTimeMillis());
    }

    /**
     * 获取运动记录是周几，今天则返回具体时间，其他则返回具体周几
     * @param dateStr 年-月-日
     * @return dateStr是周几
     */
    public static String getWeekStr(String dateStr) {
        SimpleDateFormat formatter =
                new SimpleDateFormat(FORMAT_TWO, Locale.getDefault());

        String todayStr = formatter.format(Calendar.getInstance().getTime());
        if (todayStr.equals(dateStr)) {
            return "今天 " + todayStr;
        }

        Calendar preCalendar = Calendar.getInstance();
        preCalendar.add(Calendar.DATE, -1); // 日期减一天
        String yesterdayStr = formatter.format(preCalendar.getTime());
        if (yesterdayStr.equals(dateStr)) {
            return "昨天";
        }

        int weekIndex = 0;
        try {
            Date date = formatter.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            if (date != null) {
                calendar.setTime(date);
            }
            weekIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            if (weekIndex < 0) {
                weekIndex = 0;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        };

        return rWeekStrings[weekIndex];
    }

    /**
     * 获取当前是一个月的几号
     *
     * @return dd
     */
    public static int getCurrentMonthDay() {
        return Calendar.getInstance().get(Calendar.DATE);
    }

}
