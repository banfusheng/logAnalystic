/**
 * 全局的时间工具类
 *
 * @author Administrator
 * @create 2018/8/17
 * @since 1.0.0
 */
package com.qf.util;

import com.qf.common.DateEnum;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Timeutil {
    //默认时间格式
    private static final String DEFAULT_FORMAT = "yyyy-MM-dd";

    /**
     * 判断时间是否有效
     *
     * @param date 用正则表达式判断
     * @return
     */
    public static boolean isValidateDate(String date) {
        Matcher matcher = null;
        boolean res = false;
        String reg = "[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}";
        if (date != null || !date.isEmpty()) {
            Pattern pattern = Pattern.compile(reg);
            matcher = pattern.matcher(date);
        }
        if (matcher != null) {
            res = matcher.matches();
        }
        return res;
    }

    /**
     * 获取昨天的默认格式的时间
     *
     * @return
     */
    public static String getYesterday() {

        return getYesterday(DEFAULT_FORMAT);
    }

    /**
     * 获取昨天的指定格式的时间
     *
     * @param pattern
     * @return
     */
    public static String getYesterday(String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return sdf.format(calendar.getTime());
    }

    /**
     * 将指定的时间戳。转换成字符串的日期
     *
     * @param time 15289878934549
     * @return 2018-07-05
     */
    public static String parseLong2String(long time) {
        return parseLong2String(time, DEFAULT_FORMAT);
    }

    /**
     * 将时间戳转换成指定格式的日期
     *
     * @param time
     * @param pattern
     * @return
     */
    public static String parseLong2String(long time, String pattern) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return new SimpleDateFormat(pattern).format(calendar.getTime());
    }

    /**
     * 日期转成时间戳
     *
     * @param intput
     * @return
     */
    public static long parseString2Long(String intput) {
        return parseString2Long(intput, DEFAULT_FORMAT);
    }

    /**
     * 指定格式的日期转成时间戳
     *
     * @param intput
     * @param pattern
     * @return
     */
    public static long parseString2Long(String intput, String pattern) {
        Date date = null;
        try {
            date = new SimpleDateFormat(pattern).parse(intput);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date == null ? 0 : date.getTime();

    }

    /**
     * 根据时间戳和type来获取对应值
     *
     * @param timetamp
     * @param type
     * @return
     */
    public static int getDateInfo(Long timetamp, DateEnum type) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timetamp);
        if (DateEnum.YEAR.equals(type)) {
            return calendar.get(Calendar.YEAR);
        }
        if (DateEnum.SEASON.equals(type)) {
            int month = calendar.get(Calendar.MONTH) + 1;
            if (month % 3 == 0) {
                return month / 3;
            } else {
                return month / 3 + 1;
            }
        }
        if (DateEnum.MONTH.equals(type)){
            return calendar.get(Calendar.MONTH) + 1;
        }
        if(DateEnum.WEEK.equals(type)){
            return calendar.get(Calendar.WEEK_OF_YEAR);
        }
        if (DateEnum.DAY.equals(type)){
            return calendar.get(Calendar.DAY_OF_MONTH);
        }
        if(DateEnum.HOUR.equals(type)){
            return calendar.get(Calendar.HOUR_OF_DAY);
        }
        throw new RuntimeException("该类型暂不支持时间信息获取.type:"+type.typeName);


    }

    /**
     * 根据时间戳获取当前周的第一天
     * @param timetamp
     * @return
     */
    public static long getFirstDayOfWeek(Long timetamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timetamp);
        calendar.set(Calendar.DAY_OF_WEEK,1);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTimeInMillis();
    }


    public static void main(String[] args) {
        System.out.println(getYesterday("yyyy/MM/dd"));
        System.out.println(isValidateDate("2018-06-32"));
        System.out.println(parseString2Long("2018-07-05"));
        System.out.println(parseLong2String(1530720000000l));
        System.out.println(getDateInfo(1530720000000l,DateEnum.DAY));
        System.out.println(getDateInfo(1530720000000l,DateEnum.SEASON));
        System.out.println(getDateInfo(1530720000000l,DateEnum.MONTH));
        System.out.println(getFirstDayOfWeek(1530720000000l));
        System.out.println(parseString2Long("2018-07-01"));
        /**
         *   calendar.add(Calendar.DAY_OF_MONTH, 2);//表示在默认的当前日期上加两天
         *   calendar.set(Calendar.DAY_OF_MONTH,2);//表示直接将日期设置为本月2号
         *   当为0是上个月的最后一天
         *   当为-n时是上月的倒数第n+1天
         */
        /*Calendar calendar = Calendar.getInstance();
        //calendar.add(Calendar.DAY_OF_MONTH ,-1);//Calendar.DAY_OF_YEAR相同
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println(sdf.format(calendar.getTime()));*/


    }




}
