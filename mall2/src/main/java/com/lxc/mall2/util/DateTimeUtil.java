package com.lxc.mall2.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * Created by 82138 on 2018/8/19.
 */
public class DateTimeUtil {

    static final String  DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static DateTime strToDate(String dateTimeStr) {
        return strToDate(dateTimeStr,DEFAULT_FORMAT);
    }

    public static String dateToStr(Date date) {
       return dateToStr(date,DEFAULT_FORMAT);
    }
    public static DateTime strToDate(String dateTimeStr,String FormatStr) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(FormatStr);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);

        return dateTime;

    }

    public static String dateToStr(Date date,String FormatStr) {
        if(date == null) {
            return StringUtils.EMPTY;
        }
        DateTime datetime = new DateTime(date);//这里转换成datetime是因为joda的datetime类的tostring方法有格式转换功能
        //return date.toString();//这样格式就不是想要的了
        return datetime.toString(FormatStr);//参数为想要的格式，如下注掉的测试
    }

/*    public static void main(String[] args) {
        //System.out.println(strToDate("2018/09/19 11:11:11","yyyy-MM-dd HH:mm:ss"));
        System.out.println(dateToStr(new Date(),"yyyy-MM-dd HH:mm:ss"));

    }*/
}
