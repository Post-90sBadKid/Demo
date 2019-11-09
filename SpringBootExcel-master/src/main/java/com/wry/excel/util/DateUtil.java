package com.wry.excel.util;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    /**
     * 把excel表格日期转Java日期
     * @param dateNumber excel 数据日期
     * @param dateFormat Java 日期
     * @return
     */
    public static Date getDateFromExcel(Double dateNumber,String dateFormat){
        Date date = HSSFDateUtil.getJavaDate(dateNumber);
        return date;
    }
    /**
     * 把excel表格日期转String类型的Java日期
     * @param dateNumber excel 数据日期
     * @param dateFormat Java 日期
     * @return
     */
    public static String getDateFromExcelToString(Double dateNumber, String dateFormat){
        SimpleDateFormat format=new SimpleDateFormat(dateFormat);
        Date date = HSSFDateUtil.getJavaDate(dateNumber);
        return format.format(date);
    }
    /**
     * 按照特定的格式转换时间
     * @param date 时间
     * @param dateFormat 时间格式
     * @return String的时间
     */
    public static String getDatetoString(Date date,String dateFormat){
        SimpleDateFormat format=new SimpleDateFormat(dateFormat);
       return format.format(date);
    }

    /**
     * 按照特定的格式转换时间("yyyy-MM-dd hh:mm:ss")
     * @param date 时间
     * @return String的时间
     */
    public static String getDatetoString(Date date){
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return format.format(date);
    }

    /**
     * 把字符串转换时间("yyyy-MM-dd hh:mm:ss")
     * @param strDate
     * @return
     */
    public static Date getStringtoDate(String strDate){
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date=null;
        try {
            date=format.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    return date;
    }


    /**
     * 把字符串转换时间
     * @param strDate
     * @param dateFormat
     * @return
     */
    public static Date getStringtoDate(String strDate ,String dateFormat){
        SimpleDateFormat format=new SimpleDateFormat(dateFormat);
        Date date=null;
        try {
            date=format.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

}
