package com.huizi.easydinner.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 * @PROJECT_NAME: personal
 * @DESCRIPTION:Java 8 新日期时间工具类
 * @AUTHOR: 12615
 * @DATE: 2023/8/3 10:10
 */
public class DateTimeUtils {
    /**
     * 获取当前时间的时间戳(10位:不带毫秒)
     */
    public static Long getNowTimeStamp() {
        LocalDateTime now = LocalDateTime.now();
        return now.toEpochSecond(OffsetDateTime.now().getOffset());
    }

    /**
     * 获取当前时间的时间戳(13位:带毫秒)
     */
    public static Long getNowTimeStampWithMs() {
        LocalDateTime now = LocalDateTime.now();
        return now.toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
    }

    /**
     * 时间戳转格式化字符串
     *
     * @param time
     * @param pattern
     * @return
     */
    public static String longToString(Long time, String pattern) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        return dtf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()));
    }

    /**
     * 格式化字符串转时间戳
     *
     * @param time
     * @param pattern
     * @return
     */
    public static Long stringToLong(String time, String pattern) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime parse = LocalDateTime.parse(time, dtf);
        return LocalDateTime.from(parse).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * localDate日期转字符串
     *
     * @param localDate
     * @param pattern
     * @return
     */
    public static String localDateToString(LocalDate localDate, String pattern) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        String format = localDate.format(dateTimeFormatter);
        return format;
    }


    /**
     * LocalDateTime日期转字符串
     *
     * @param localDateTime
     * @param pattern
     * @return
     */
    public static String localDateTimeToString(LocalDateTime localDateTime, String pattern) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        String format = localDateTime.format(dateTimeFormatter);
        return format;
    }

    /**
     * 字符串转LocalDate
     *
     * @param localDate
     * @param pattern
     * @return
     */
    public static LocalDate stringToLocalDate(String localDate, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDate date = LocalDate.parse(localDate, formatter);
        return date;
    }

    /**
     * 字符串转LocalDateTime
     *
     * @param localDate
     * @param pattern
     * @return
     */
    public static LocalDateTime stringToLocalDateTime(String localDate, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime date = LocalDateTime.parse(localDate, formatter);
        return date;
    }

    /**
     * 字符串转日期Date
     *
     * @param dateString
     * @param pattern
     * @return
     */
    public static Date stringToDate(String dateString, String pattern) {
        LocalDate localDate = stringToLocalDate(dateString, pattern);
        Date date = Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        return date;
    }

    /**
     * 获取本月第一天
     */
    public static LocalDate firstDayOfThisMonth() {
        LocalDate today = LocalDate.now();
        return today.with(TemporalAdjusters.firstDayOfMonth());
    }

    /**
     * 获取本月第N天
     */
    public static LocalDate dayOfThisMonth(int n) {
        LocalDate today = LocalDate.now();
        return today.withDayOfMonth(n);
    }

    /**
     * 获取本月最后一天
     */
    public static LocalDate lastDayOfThisMonth() {
        LocalDate today = LocalDate.now();
        return today.with(TemporalAdjusters.lastDayOfMonth());
    }

    /**
     * 获取指定月的最后一天
     *
     * @param month 1-12
     */
    public static LocalDate lastDayOfMonth(int year, int month) {
        LocalDate ofDate = LocalDate.of(year, month, 1);
        return ofDate.with(TemporalAdjusters.lastDayOfMonth());
    }

    /**
     * 获取本月第一天的开始时间
     */
    public static String startOfThisMonth(String pattern) {
        LocalDateTime ofDateTime = LocalDateTime.of(firstDayOfThisMonth(), LocalTime.MIN);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        return ofDateTime.format(dtf);
    }

    /**
     * 取本月最后一天的结束时间
     */
    public static String endOfThisMonth(String pattern) {
        LocalDateTime ofDateTime = LocalDateTime.of(lastDayOfThisMonth(), LocalTime.MAX);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        return ofDateTime.format(dtf);
    }

    /**
     * 获取两个日期相差的月数
     */
    public static int getMonthDiff() {
        String text1 = "2020-08-02";
        Temporal temporal1 = LocalDate.parse(text1);
        String text2 = "2020-09-01";
        Temporal temporal2 = LocalDate.parse(text2);
        // 方法返回为相差月份
        Long l = ChronoUnit.MONTHS.between(temporal1, temporal2);
        System.out.println(l);

        return l.intValue();
    }
}
