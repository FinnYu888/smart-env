package com.ai.apac.smartenv.common.utils;


import cn.hutool.core.date.DateTime;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

public class TimeUtil {

    public static final String YYYY_MM = "yyyy-MM";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String HH_MM_SS_YYYY_MM_DD = "HH:mm:ss yyyy-MM-dd";
    public static final String YYYYMM = "yyyyMM";
    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss:SSS";
    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    private TimeUtil() {
    }

    /**
     * 传入日期字符串,转成时间戳
     *
     * @param dateStr
     * @param dateStr
     * @return
     */
    public static long getTimestamp(String dateStr, String dateFormat) {
        DateTime time = DateTime.of(dateStr, dateFormat);
        return time.toTimestamp().getTime();
    }

    /**
     * 在一个时间上加上对应的年
     *
     * @param ti long
     * @param i  int
     * @return Date
     * @throws Exception
     */
    public static Date addOrMinusYear(long ti, int i) throws Exception {
        Date rtn = null;
        GregorianCalendar cal = new GregorianCalendar();
        Date date = new Date(ti);
        cal.setTime(date);
        cal.add(GregorianCalendar.YEAR, i);
        rtn = cal.getTime();
        return rtn;
    }

    /**
     * 在一个时间上加上对应的月份数
     *
     * @param ti long
     * @param i  int
     * @return Date
     * @throws Exception
     */
    public static Date addOrMinusMonth(long ti, int i) throws Exception {
        Date rtn = null;
        GregorianCalendar cal = new GregorianCalendar();
        Date date = new Date(ti);
        cal.setTime(date);
        cal.add(GregorianCalendar.MONTH, i);
        rtn = cal.getTime();
        return rtn;
    }

    /**
     * 在一个时间上加上或减去周
     *
     * @param ti long
     * @param i  int
     * @return Date
     */
    public static Date addOrMinusWeek(long ti, int i) {
        Date rtn = null;
        GregorianCalendar cal = new GregorianCalendar();
        Date date = new Date(ti);
        cal.setTime(date);
        cal.add(GregorianCalendar.WEEK_OF_YEAR, i);
        rtn = cal.getTime();
        return rtn;
    }

    /**
     * 在一个时间上加上或减去天数
     *
     * @param ti long
     * @param i  int
     * @return Date
     */
    public static Date addOrMinusDays(long ti, int i) {
        Date rtn = null;
        GregorianCalendar cal = new GregorianCalendar();
        Date date = new Date(ti);
        cal.setTime(date);
        cal.add(GregorianCalendar.DAY_OF_MONTH, i);
        rtn = cal.getTime();
        return rtn;
    }

    /**
     * 获取当前到明天0点的秒数
     */

    public static long getTomorrowZeroSeconds() {
        long current = System.currentTimeMillis();// 当前时间毫秒数
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long tomorrowzero = calendar.getTimeInMillis();
        long tomorrowzeroSeconds = (tomorrowzero - current) / 1000;
        return tomorrowzeroSeconds;
    }

        /**
         * 在一个时间上加上或减去小时
         *
         * @param ti long
         * @param i  int
         * @return Date
         */
    public static Date addOrMinusHours(long ti, int i) {
        Date rtn = null;
        GregorianCalendar cal = new GregorianCalendar();
        Date date = new Date(ti);
        cal.setTime(date);
        cal.add(GregorianCalendar.HOUR, i);
        rtn = cal.getTime();
        return rtn;
    }

    /**
     * 在一个时间上加上或减去分钟
     *
     * @param ti long
     * @param i  int
     * @return Date
     */
    public static Date addOrMinusMinutes(long ti, int i) {
        Date rtn = null;
        GregorianCalendar cal = new GregorianCalendar();
        Date date = new Date(ti);
        cal.setTime(date);
        cal.add(GregorianCalendar.MINUTE, i);
        rtn = cal.getTime();
        return rtn;
    }

    /**
     * 在一个时间上加上或减去秒数
     *
     * @param ti long
     * @param i  int
     * @return Date
     */
    public static Date addOrMinusSecond(long ti, int i) {
        Date rtn = null;
        GregorianCalendar cal = new GregorianCalendar();
        Date date = new Date(ti);
        cal.setTime(date);
        cal.add(GregorianCalendar.SECOND, i);
        rtn = cal.getTime();
        return rtn;
    }

    /**
     * 两个日期比较大小
     *
     * @param date1
     * @param date2
     * @return Boolean
     */
    public static Boolean biggerDate(Timestamp date1, Timestamp date2) {
        if (date1.getTime() > date2.getTime()) {
            return Boolean.valueOf(true);
        } else {
            return Boolean.valueOf(false);
        }
    }


    /**
     * 根据指定的日期获取下个月的第一天的时间
     *
     * @param date
     * @return
     * @author shaosm
     */
    public static Timestamp getDateOfNextMonthFirstDay(Date date) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.set(Calendar.DAY_OF_MONTH, 1);
        rightNow.set(Calendar.HOUR_OF_DAY, 0);
        rightNow.set(Calendar.MILLISECOND, 0);
        rightNow.set(Calendar.SECOND, 0);
        rightNow.set(Calendar.MINUTE, 0);
        rightNow.set(Calendar.MONTH, rightNow.get(Calendar.MONTH) + 1);
        return new Timestamp(rightNow.getTimeInMillis());
    }

    /**
     * 根据指定的日期获取上个月的第一天的时间
     *
     * @param date
     * @return
     */
    public static Timestamp getDateOfPreMonthFirstDay(Date date) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.set(Calendar.DAY_OF_MONTH, 1);
        rightNow.set(Calendar.HOUR_OF_DAY, 0);
        rightNow.set(Calendar.MILLISECOND, 0);
        rightNow.set(Calendar.SECOND, 0);
        rightNow.set(Calendar.MINUTE, 0);
        rightNow.set(Calendar.MONTH, rightNow.get(Calendar.MONTH) - 1);
        return new Timestamp(rightNow.getTimeInMillis());
    }

    /**
     * 根据指定的日期获取上个月的最后一天的时间
     *
     * @param date
     * @return
     */
    public static Timestamp getDateOfPreMonthLastDay(Date date) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.set(Calendar.MONTH, rightNow.get(Calendar.MONTH) - 1);
        rightNow.set(Calendar.DAY_OF_MONTH, rightNow.getActualMaximum(Calendar.DAY_OF_MONTH));
        rightNow.set(Calendar.HOUR_OF_DAY, 23);
        rightNow.set(Calendar.MILLISECOND, 59);
        rightNow.set(Calendar.SECOND, 59);
        rightNow.set(Calendar.MINUTE, 59);
        return new Timestamp(rightNow.getTimeInMillis());
    }

    /**
     * 将带有时间类型的日期转换成不带时间的日期
     *
     * @param date
     * @return
     */
    public static Timestamp formatDateTimeToDate(Date date) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.set(Calendar.HOUR_OF_DAY, 0);
        rightNow.set(Calendar.MILLISECOND, 0);
        rightNow.set(Calendar.SECOND, 0);
        rightNow.set(Calendar.MINUTE, 0);
        return new Timestamp(rightNow.getTimeInMillis());
    }

    /**
     * @param date
     * @Author:zhanglei25
     * @Description:
     * @Date: 2017/6/30 10:27
     */

    public static Timestamp formDateToTimestamp(Date date) {
        String dateStr = getYYYYMMDDHHMMSS(date);
        if (null == dateStr) {
            return null;
        }
        return Timestamp.valueOf(getYYYYMMDDHHMMSS(date));
    }

    /**
     * 根据指定日期获取该月的最后一天的最后时间点
     *
     * @param date
     * @return
     */
    public static Timestamp getDateOfCurrentMonthEndDay(Date date) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.set(Calendar.DAY_OF_MONTH, rightNow.getActualMaximum(Calendar.DAY_OF_MONTH));
        rightNow.set(Calendar.HOUR_OF_DAY, 23);
        rightNow.set(Calendar.MILLISECOND, 59);
        rightNow.set(Calendar.SECOND, 59);
        rightNow.set(Calendar.MINUTE, 59);
        rightNow.set(Calendar.MONTH, rightNow.get(Calendar.MONTH));
        return new Timestamp(rightNow.getTimeInMillis());
    }

    /**
     * 根据指定日期获取当天的最后的时间点
     *
     * @param date
     * @return
     */
    public static Timestamp getLastDate(Date date) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.set(Calendar.HOUR_OF_DAY, 23);
        rightNow.set(Calendar.MILLISECOND, 59);
        rightNow.set(Calendar.SECOND, 59);
        rightNow.set(Calendar.MINUTE, 59);
        rightNow.set(Calendar.MONTH, rightNow.get(Calendar.MONTH));
        return new Timestamp(rightNow.getTimeInMillis());
    }

    /**
     * 根据指定日期获取前一天的最后的时间点
     *
     * @param date
     * @return
     */
    public static Timestamp getPreLastDate(Date date) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.set(Calendar.DAY_OF_MONTH, rightNow.get(Calendar.DAY_OF_MONTH) - 1);
        rightNow.set(Calendar.HOUR_OF_DAY, 23);
        rightNow.set(Calendar.MILLISECOND, 59);
        rightNow.set(Calendar.SECOND, 59);
        rightNow.set(Calendar.MINUTE, 59);
        rightNow.set(Calendar.MONTH, rightNow.get(Calendar.MONTH));
        return new Timestamp(rightNow.getTimeInMillis());
    }

    /**
     * 根据指定日期获取下一天的开始的时间点
     *
     * @param date
     * @return
     */
    public static Timestamp getNextDay(Date date) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.set(Calendar.DAY_OF_MONTH, rightNow.get(Calendar.DAY_OF_MONTH) + 1);
        rightNow.set(Calendar.HOUR_OF_DAY, 0);
        rightNow.set(Calendar.MILLISECOND, 0);
        rightNow.set(Calendar.SECOND, 0);
        rightNow.set(Calendar.MINUTE, 0);
        rightNow.set(Calendar.MONTH, rightNow.get(Calendar.MONTH));
        return new Timestamp(rightNow.getTimeInMillis());
    }

    /**
     * 将时间格式化为YYYY-MM-DD
     *
     * @param date
     * @return
     */
    public static String getYYYYMMDD(Date date) {
        if (date == null)
            return null;
        DateFormat dateformat = new SimpleDateFormat(YYYY_MM_DD);
        return dateformat.format(date);
    }

    /**
     * 将时间格式化为yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String getYYYYMMDDHHMMSS(Date date) {
        if (date == null)
            return null;
        DateFormat dateformat = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        return dateformat.format(date);
    }

    /**
     * 将时间格式化为YYYYMMDD
     *
     * @param date
     * @return
     */
    public static String getNoLineYYYYMMDD(Date date) {
        if (date == null)
            return null;
        DateFormat dateformat = new SimpleDateFormat(YYYYMMDD);
        return dateformat.format(date);
    }

    /**
     * 将时间格式化为YYYYMMDDHHMMSS
     *
     * @param date
     * @return
     */
    public static String getNoLineYYYYMMDDHHMISS(Date date) {
        if (date == null)
            return null;
        DateFormat dateformat = new SimpleDateFormat(YYYYMMDDHHMMSS);
        return dateformat.format(date);
    }

    /**
     * 将时间格式化为YYYYMMDDHHMMSS
     *
     * @param date
     * @return
     */
    public static String getNoLine(Date date, String type) {
        if (date == null)
            return null;
        DateFormat dateformat = new SimpleDateFormat(type);
        return dateformat.format(date);
    }

    /**
     * 将时间格式化为yyyyMMdd HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String getNoLineYYYYMMDDHHMMSS(Date date) {
        if (date == null)
            return null;
        DateFormat dateformat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        return dateformat.format(date);
    }

    /**
     * 处理计费月时间
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    public static Timestamp getBillMonthDate(Date beginDate, Date endDate) {
        if (null == beginDate) {
            return null;
        }
        // 获取结束时间的月底时间
        Timestamp monthEndDate = new Timestamp(TimeUtil.addOrMinusDays(TimeUtil.getDateOfNextMonthFirstDay(endDate).getTime(), -1).getTime());
        return new Timestamp(monthEndDate.getTime());
    }

    /**
     * 将指定的日期取整
     *
     * @param date
     * @return
     * @author shaosm
     */
    public static Timestamp getTruncDate(Date date) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.set(Calendar.HOUR_OF_DAY, 0);
        rightNow.set(Calendar.MILLISECOND, 0);
        rightNow.set(Calendar.SECOND, 0);
        rightNow.set(Calendar.MINUTE, 0);
        return new Timestamp(rightNow.getTimeInMillis());
    }

    /**
     * 根据指定的日期获取月的第一天的时间
     *
     * @param date
     * @return
     */
    public static Timestamp getDateOfMonthFirstDay(Date date) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.set(Calendar.DAY_OF_MONTH, 1);
        rightNow.set(Calendar.HOUR_OF_DAY, 0);
        rightNow.set(Calendar.MILLISECOND, 0);
        rightNow.set(Calendar.SECOND, 0);
        rightNow.set(Calendar.MINUTE, 0);
        rightNow.set(Calendar.MONTH, rightNow.get(Calendar.MONTH));
        return new Timestamp(rightNow.getTimeInMillis());
    }

    /**
     * 获取当前时间的最后时间点
     */
    /**
     * 根据指定日期获取最后时间点
     *
     * @param date
     * @return
     */
    public static Timestamp getDateOfCurrentEndDay(Date date) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.set(Calendar.HOUR_OF_DAY, 23);
        rightNow.set(Calendar.MILLISECOND, 59);
        rightNow.set(Calendar.SECOND, 59);
        rightNow.set(Calendar.MINUTE, 59);
        rightNow.set(Calendar.MONTH, rightNow.get(Calendar.MONTH));
        return new Timestamp(rightNow.getTimeInMillis());
    }


    /*
     * 得到某一日期的下个月的开始日期
     * param dateString 格式yyyyMMdd
     * return 8位日期值
     */
    public static String getBeginningDateOfLastMonth(String dateString) {
        String lastYear = getLastYear(dateString);// 该日期下年年份
        String lastMonth = getLastMonth(dateString);// 改日期下月月份
        if ("01".equals(lastMonth)) {
            return lastYear + "-" + lastMonth + "-01 00:00:00";
        } else {
            return dateString.substring(0, 5) + lastMonth + "-01 " + "00:00:00";
        }
    }


    // 得到某个日期下一年的年份
    public static String getLastYear(String dateString) {
        String currentYear = dateString.substring(0, 4);// 该日期的所在年份
        return String.valueOf(Integer.parseInt(currentYear) + 1);
    }

    // 得到某个日期下月的月份
    public static String getLastMonth(String dateString) {
        String currentMonth = dateString.substring(5, 7);// 改日期所在月份
        if ("12".equals(currentMonth)) {
            currentMonth = "01";
        } else if (Integer.parseInt(currentMonth) > 10) {
            currentMonth = String.valueOf(Integer.parseInt(currentMonth) + 1);
        } else {
            currentMonth = "0" + String.valueOf(Integer.parseInt(currentMonth) + 1);
        }
        return currentMonth;
    }

    /**
     * 获取两个时间之间的相差月份数
     *
     * @throws ParseException
     */
    public static int getMonthSpace(String date1, String date2) throws ParseException {
        int result = 0;

        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD);

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();

        c1.setTime(sdf.parse(date1));
        c2.setTime(sdf.parse(date2));

        if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)) {
            result = c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
        } else {
            result = 12 * (c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR)) + c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
        }
        return result == 0 ? 0 : Math.abs(result);
    }

    /**
     * 获取两个时间之间的相差月份数
     *
     * @throws ParseException
     */
    public static int getMonthSpace(Timestamp date1, Timestamp date2) throws ParseException {
        int result = 0;

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(date1);
        c2.setTime(date2);

        if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)) {
            result = c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
        } else {
            result = 12 * (c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR)) + c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
        }
        return result == 0 ? 0 : Math.abs(result);
    }

    /**
     * 月份相加
     *
     * @param time
     * @param month
     * @return
     * @Function: DateAddMonth
     * @version: v1.0.0
     * @author: wangxw3
     * @date: 2012-10-28 上午10:50:09
     */
    public static Timestamp timeAddMonth(Timestamp time, int month) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        cal.add(Calendar.MONTH, month);
        return new Timestamp(cal.getTimeInMillis());
    }

    /**
     * @param dateString 要转换的初始值
     * @param formate1   初始值格式
     * @param formate2   转换后的格式
     * @return
     * @throws ParseException
     * @Function changeDateFormat
     * @Description 时间格式转换
     * @version v1.0.0
     * @author lilong
     */
    public static String changeDateFormat(String dateString, String formate1, String formate2) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(formate1);
        Date date = null;
        if (dateString == null || dateString.trim().equals("")) {
            date = new Date();
        } else {
            date = sdf.parse(dateString);
        }
        SimpleDateFormat sdf2 = new SimpleDateFormat(formate2);
        return sdf2.format(date);

    }

    public static int getDayForWeek(String date, String formate) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(formate);
        Calendar c = Calendar.getInstance();
        c.setTime(sdf.parse(date));
        int dayForWeek = 0;
        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            dayForWeek = 7;
        } else {
            dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        }
        return dayForWeek;
    }

    public static Timestamp getTimstampByString(String strDate, String mask) throws Exception {
        if (strDate == null) {
            return null;
        } else {
            DateFormat dateformat = new SimpleDateFormat(mask);
            Timestamp date = null;
            try {
                date = new Timestamp(dateformat.parse(strDate).getTime());
            } catch (ParseException e) {
                throw new Exception(e.getMessage());
            }
            return date;
        }
    }

    public static Timestamp getPrimaryDataSourceSysDate() {
        return new Timestamp(System.currentTimeMillis());
    }


    public static String getYYYY_MM_DD(Timestamp date) {
        if (date == null) {
            return null;
        } else {
            DateFormat dateformat = new SimpleDateFormat(YYYY_MM_DD);
            return dateformat.format(date);
        }
    }

    public static String getYYYY_MM_DD_HH_MM_SS(Timestamp date) {
        if (date == null) {
            return null;
        } else {
            DateFormat dateformat = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
            return dateformat.format(date);
        }
    }

    public static String getYYYY_MM_DD(Date date) {
        if (date == null)
            return null;

        DateFormat dateformat = new SimpleDateFormat(YYYY_MM_DD);
        return dateformat.format(date);
    }

    public static String getFormattedDate(Timestamp dtDate, String strFormatTo) throws Exception {
        if (dtDate == null)
            return "";
        if (dtDate.equals(new Timestamp(0L)))
            return "";
        String strFormatToReplace = strFormatTo.replace('/', '-');
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        if (Integer.parseInt(formatter.format(dtDate)) < 1900)
            return "";
        try {
            formatter = new SimpleDateFormat(strFormatToReplace);
            return formatter.format(dtDate);
        } catch (Exception e) {
            throw new Exception(e.getMessage());

        }
    }

    public static Date parseDate(String str, String parsePatterns[]) throws ParseException {
        if (str == null || parsePatterns == null)
            throw new IllegalArgumentException("Date and Patterns must not be null");
        SimpleDateFormat parser = null;
        ParsePosition pos = new ParsePosition(0);
        for (int i = 0; i < parsePatterns.length; i++) {
            if (i == 0) {
                parser = new SimpleDateFormat(parsePatterns[0]);
            } else {
                if (parser != null) {
                    parser.applyPattern(parsePatterns[i]);
                }
            }
            pos.setIndex(0);
            Date date = null;
            if (parser != null) {
                date = parser.parse(str, pos);
            }
            if (date != null && pos.getIndex() == str.length())
                return date;
        }

        throw new ParseException("Unable to parse the date: " + str, -1);
    }

    public static String getDateString(Date dt) {
        if (dt == null) {
            return "";
        }
        DateFormat df = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        return df.format(dt);
    }

    /**
     * 获取系统时间
     *
     * @return
     */
    public static Timestamp getSysDate() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static Timestamp getNextMonthStartDate(Date date) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.set(5, 1);
        rightNow.set(11, 0);
        rightNow.set(14, 0);
        rightNow.set(13, 0);
        rightNow.set(12, 0);
        rightNow.set(2, rightNow.get(2) + 1);
        return new Timestamp(rightNow.getTimeInMillis());
    }

    public static Timestamp getBeforeMonthStartDate(Date date) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.set(5, 1);
        rightNow.set(11, 0);
        rightNow.set(14, 0);
        rightNow.set(13, 0);
        rightNow.set(12, 0);
        rightNow.set(2, rightNow.get(2) - 1);
        return new Timestamp(rightNow.getTimeInMillis());
    }

    public static Timestamp getCurrentMonthEndDate(Date date) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.set(5, rightNow.getActualMaximum(5));
        rightNow.set(11, 23);
        rightNow.set(14, 59);
        rightNow.set(13, 59);
        rightNow.set(12, 59);
        rightNow.set(2, rightNow.get(2));
        return new Timestamp(rightNow.getTimeInMillis());
    }

    public static Timestamp getCurrentMonthFirstDate(Date date) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.set(5, 1);
        rightNow.set(11, 0);
        rightNow.set(14, 0);
        rightNow.set(13, 0);
        rightNow.set(12, 0);
        rightNow.set(2, rightNow.get(2));
        return new Timestamp(rightNow.getTimeInMillis());
    }

    public static Timestamp getCurrentDayEndDate(Date date) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.set(11, 23);
        rightNow.set(14, 59);
        rightNow.set(13, 59);
        rightNow.set(12, 59);
        rightNow.set(2, rightNow.get(2));
        return new Timestamp(rightNow.getTimeInMillis());
    }

    public static Timestamp getCurrentDayStartDate(Date date) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.set(11, 0);
        rightNow.set(14, 0);
        rightNow.set(13, 0);
        rightNow.set(12, 0);
        rightNow.set(2, rightNow.get(2));
        return new Timestamp(rightNow.getTimeInMillis());
    }

    public static Timestamp getBeforeDayEndDate(Date date) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.set(5, rightNow.get(5) - 1);
        rightNow.set(11, 23);
        rightNow.set(14, 59);
        rightNow.set(13, 59);
        rightNow.set(12, 59);
        rightNow.set(2, rightNow.get(2));
        return new Timestamp(rightNow.getTimeInMillis());
    }

    public static Timestamp getNextDayStartDay(Date date) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.set(5, rightNow.get(5) + 1);
        rightNow.set(11, 0);
        rightNow.set(14, 0);
        rightNow.set(13, 0);
        rightNow.set(12, 0);
        rightNow.set(2, rightNow.get(2));
        return new Timestamp(rightNow.getTimeInMillis());
    }

    public static String getDateStringFromTimestamp(Timestamp time) {
        Date date = new Date(time.getTime());
        DateFormat df = new SimpleDateFormat(YYYYMM);
        return df.format(date);
    }

    public static String getYearMonthString(String dateValue) throws Exception {
        DateFormat parseDf = new SimpleDateFormat(YYYYMM);
        Date date = parseDf.parse(dateValue);
        DateFormat df = new SimpleDateFormat(YYYY_MM);
        return df.format(date);
    }

    public static String getYearSplitMonthString(String dateValue) throws Exception {
        DateFormat parseDf = new SimpleDateFormat(YYYY_MM);
        Date date = parseDf.parse(dateValue);
        DateFormat df = new SimpleDateFormat(YYYYMM);
        return df.format(date);
    }

    /**
     * 获得时间的字符串
     *
     * @param ts      Timestamp
     * @param pattern String
     * @return String
     * @throws Exception
     */
    public static String getYYYYMMDDHHMMSS(Timestamp ts, String pattern)
            throws Exception {
        if (ts == null) {
            return null;
        }
        DateFormat dateformat = new SimpleDateFormat(pattern);
        return dateformat.format(ts);
    }

    /**
     * 将时间格式化为YYYY-MM-DD
     *
     * @param date
     * @return
     */
    public static String getYYYYMM(Date date) {
        if (date == null)
            return null;
        DateFormat dateformat = new SimpleDateFormat(YYYYMM);
        return dateformat.format(date);
    }

    public static String getYYYY_MM(Date date) {
        if (date == null)
            return null;
        DateFormat dateformat = new SimpleDateFormat(YYYY_MM);
        return dateformat.format(date);
    }

    public static Timestamp getMaxExpire() {
        Calendar cal = Calendar.getInstance();
        cal.set(2099, 11, 31, 0, 0, 0);
        return new Timestamp(cal.getTimeInMillis());
    }

    public static Timestamp getDefaultSysDate() throws Exception {
        return new Timestamp(System.currentTimeMillis());
    }

    public static String generateBusiDate(Date date) {
        SimpleDateFormat df2 = new SimpleDateFormat(YYYYMMDD);
        return df2.format(date);
    }

    public static Date generateYesterday(Date date) {
        return new Date(date.getTime() - 1000 * 60 * 60 * 24);
    }

    public static Date generateBusiDateByStr(String dateStr) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat(YYYYMMDD);
        return df.parse(dateStr);
    }

    public static String generateCompareDate(Date date) {
        SimpleDateFormat df2 = new SimpleDateFormat(YYYY_MM_DD);
        Date yesterday = new Date(date.getTime());
        return df2.format(yesterday) + " 00:00:00";
    }

    public static Date generateNextDate(Date date) {
        return new Date(date.getTime() + 1000 * 60 * 60 * 24);
    }

    public static String getYyyyMmDdHhMmSsSss() {
        SimpleDateFormat formatter = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS_SSS);

        return formatter.format(new Date());
    }

    public static String getYyyyMmDdHhMmSs() {
        SimpleDateFormat formatter = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);

        return formatter.format(new Date());
    }

    public static String getMonthFirstDay(String type) {
        SimpleDateFormat formatter = new SimpleDateFormat(type);
        Calendar cal_1 = Calendar.getInstance();//获取当前日期
        cal_1.set(Calendar.DAY_OF_MONTH, 1);
        cal_1.set(Calendar.HOUR_OF_DAY, 0);
        cal_1.set(Calendar.MILLISECOND, 0);
        cal_1.set(Calendar.SECOND, 0);
        cal_1.set(Calendar.MINUTE, 0);
        cal_1.set(Calendar.MONTH, cal_1.get(Calendar.MONTH));
        String firstDay = formatter.format(cal_1.getTime());
        return firstDay;
    }

    public static String getMonthLastDay(Date date, String type) {
        SimpleDateFormat formatter = new SimpleDateFormat(type);
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.set(Calendar.DAY_OF_MONTH, rightNow.getActualMaximum(Calendar.DAY_OF_MONTH));
        rightNow.set(Calendar.HOUR_OF_DAY, 23);
        rightNow.set(Calendar.MILLISECOND, 59);
        rightNow.set(Calendar.SECOND, 59);
        rightNow.set(Calendar.MINUTE, 59);
        rightNow.set(Calendar.MONTH, rightNow.get(Calendar.MONTH));
        String lastDay = formatter.format(rightNow.getTime());
        return lastDay;
    }

    /**
     * 获取每天的开始时间 00:00:00:00
     *
     * @param date
     * @return
     */
    public static Timestamp getStartTime(Date date) {
        Calendar dateStart = Calendar.getInstance();
        dateStart.setTime(date);
        dateStart.set(Calendar.HOUR_OF_DAY, 0);
        dateStart.set(Calendar.MINUTE, 0);
        dateStart.set(Calendar.SECOND, 0);
        return new Timestamp(dateStart.getTimeInMillis());
    }

    /**
     * 获取每天的开始时间 23:59:59:999
     *
     * @param date
     * @return
     */
    public static Timestamp getEndTime(Date date) {
        Calendar dateEnd = Calendar.getInstance();
        dateEnd.setTime(date);
        dateEnd.set(Calendar.HOUR_OF_DAY, 23);
        dateEnd.set(Calendar.MINUTE, 59);
        dateEnd.set(Calendar.SECOND, 59);
        return new Timestamp(dateEnd.getTimeInMillis());
    }

    /**
     * String to TimeStamp
     *
     * @param str
     * @return
     */
    public static Timestamp stringParseTimeStamp(String str) {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        try {
            ts = Timestamp.valueOf(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ts;
    }

    /**
     * 获取当月所有天
     *
     * @return
     */
    public static List<String> getDayListOfMonth() {
        List<String> list = new ArrayList<String>();
        Calendar aCalendar = Calendar.getInstance(Locale.CHINA);
        aCalendar.set(Calendar.DAY_OF_MONTH, 1); //设置日期
        int year = aCalendar.get(Calendar.YEAR);//年份
        int month = aCalendar.get(Calendar.MONTH) + 1;//月份
        int day = aCalendar.getActualMaximum(Calendar.DATE);
        for (int i = 1; i <= day; i++) {
            String aDate = String.valueOf(year) + "-" + month + "-" + i;
            list.add(aDate);
        }
        return list;
    }

    /**
     * 获取指定月所有天
     *
     * @return
     */
    public static List<String> getDayListOfMonth(Integer year, Integer month) {
        List<String> list = new ArrayList<String>();
        Calendar aCalendar = Calendar.getInstance(Locale.CHINA);
        aCalendar.set(Calendar.YEAR, year);
        aCalendar.set(Calendar.MONTH, month - 1);
        aCalendar.set(Calendar.DAY_OF_MONTH, 1); //设置日期
        int day = aCalendar.getActualMaximum(Calendar.DATE);
        for (int i = 1; i <= day; i++) {
            String aDate = String.valueOf(year) + "-" + month + "-" + i;
            list.add(aDate);
        }
        return list;
    }
}
