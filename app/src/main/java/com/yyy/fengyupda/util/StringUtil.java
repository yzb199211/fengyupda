package com.yyy.fengyupda.util;

import android.graphics.Color;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class StringUtil {
    public final static int DATETYPE = 1;
    final static String colorFormat = "^#([0-9a-fA-F]{6}|[0-9a-fA-F]{3})$";

    /**
     * 判断颜色是否正确
     *
     * @param color
     * @return
     */
    public static boolean isColor(String color) {
        return color.matches(colorFormat);
    }

    /**
     * 判断是否粗体，0否，1是
     *
     * @param i
     * @return
     */
    public static boolean isBold(int i) {
        if (i == 0)
            return false;
        else
            return true;
    }

    /**
     * 转化单行占比为float型数据，当超过一行时只取一行长度
     *
     * @param i
     * @return
     */
    public static float isPercent(int i) {

        if (i > 100)
            i = 100;
        return (i / 100f);
    }

    /**
     * 获取时间格式，type=1时间格式为yyyy-MM-dd HH:mm
     *
     * @param date
     * @param type
     * @return
     */
    public static String getDate(String date, int type) throws Exception {
        if (type == 1) {
            date = date.substring(0, 16);
            date = date.replace("T", " ");
            return date;
        } else
            return date;
    }

    /**
     * 判断字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str) {
        return !TextUtils.isEmpty(str);
    }

    /**
     * 替换字符串中的null值
     *
     * @param str
     * @return
     */
    public static String replaceNull(String str) {
        return str.replace(":null", ":\"\"");
    }

    public static int randomColor() {
        Random random = new Random();
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        return Color.rgb(r, g, b);
    }

    public static String getTime(Date date) {//可根据需要自行截取数据显示
//        Log.d("getTime()", "choice date millis: " + date.getTime());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    public static String getTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss
//获取当前时间
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

    public static String getDate(String string) {
        return string.substring(0, 10);
    }
}
