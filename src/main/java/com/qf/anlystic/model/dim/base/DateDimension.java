/**
 * 时间维度类
 *
 * @author Administrator
 * @create 2018/8/18
 * @since 1.0.0
 */
package com.qf.anlystic.model.dim.base;

import com.qf.common.DateEnum;
import com.qf.util.Timeutil;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class DateDimension extends BaseDimension {
    private int id;
    private int year;
    private int season;
    private int month;//1-12
    private int week;
    private int day;
    private Date date= new Date();
    private String type;

    public DateDimension() {
    }

    public DateDimension(int year, int season, int month, int week, int day) {
        this.year = year;
        this.season = season;
        this.month = month;
        this.week = week;
        this.day = day;
    }

    public DateDimension(int year, int season, int month, int week, int day, Date date) {
        this(year, season, month, week, day);
        this.date = date;
    }

    public DateDimension(int year, int season, int month, int week, int day, Date date, String type) {
        this(year, season, month, week, day, date);
        this.type = type;
    }

    public DateDimension(int id, int year, int season, int month, int week, int day, Date date, String type) {
        this(year, season, month, week, day, date, type);
        this.id = id;

    }

    /**
     * 根据时间戳和type类型获取时间的维度
     *
     * @param timetamp
     * @param type
     * @return
     */
    public static DateDimension buildDate(Long timetamp, DateEnum type) {

        Calendar calendar = Calendar.getInstance();
        calendar.clear();//先清除日历对象
        int year = Timeutil.getDateInfo(timetamp, DateEnum.YEAR);
        //判断type的类型

        /*
          获取的维度是年    截止日期返回当年的1月1号的
         */
        if (DateEnum.YEAR.equals(type)) {

            calendar.set(year, 0, 1);
            return new DateDimension(year, 1, 1,
                    0, 1, calendar.getTime(), type.typeName);
        }

        int season = Timeutil.getDateInfo(timetamp, DateEnum.SEASON);
        /*
        获取的维度指标是季度 返回该季度的第一个月第一天

         */
        if (DateEnum.SEASON.equals(type)) {
            int month = season * 3 - 2;
            calendar.set(year, month - 1, 1);
            return new DateDimension(year, season, month,
                    0, 1, calendar.getTime(), type.typeName);
        }

        int month = Timeutil.getDateInfo(timetamp, DateEnum.MONTH);
        /*
        获取的维度指标是月份  返回这个月的第一天
         */
        if (DateEnum.MONTH.equals(type)) { // 当月1号这一天
            calendar.set(year, month - 1, 1);
            return new DateDimension(year, season, month, 0, 1, calendar.getTime(), type.typeName);
        }


        int week = Timeutil.getDateInfo(timetamp, DateEnum.WEEK);
        /*
        获取的维度指标是周 该周的第一天0:00:00
         */
        if (DateEnum.WEEK.equals(type)) {
            //获取指定日期的周的第一天，返回当前周的第一天的时间戳
            long firstDayOfWeek = Timeutil.getFirstDayOfWeek(timetamp);
            //此时的年月周都有可能发生变化需要重新获取
            year = Timeutil.getDateInfo(firstDayOfWeek, DateEnum.YEAR);
            season = Timeutil.getDateInfo(firstDayOfWeek, DateEnum.SEASON);
            month = Timeutil.getDateInfo(firstDayOfWeek, DateEnum.MONTH);
            week = Timeutil.getDateInfo(firstDayOfWeek, DateEnum.WEEK);
            int day = Timeutil.getDateInfo(firstDayOfWeek, DateEnum.DAY);

            return new DateDimension(year, season, month,
                    week, day, new Date(firstDayOfWeek), type.typeName);
        }

        int day = Timeutil.getDateInfo(timetamp, DateEnum.DAY);
        /*
        返回改天的0:00:00
         */
        if (DateEnum.DAY.equals(type)) {
            calendar.set(year, month - 1, day);
            return new DateDimension(year, season, month, week,
                    day, calendar.getTime(), type.typeName);
        }
        throw new RuntimeException("该类型暂时不支持获取时间维度." + type.typeName);


    }

    @Override
    public int compareTo(BaseDimension o) {
        if (o == this) {
            return 0;
        }
        DateDimension other = (DateDimension) o;
        int tmp = this.id - other.id;
        if (tmp != 0) {
            return tmp;
        }
        tmp = this.year - other.year;
        if (tmp != 0) {
            return tmp;
        }
        tmp = this.season - other.season;
        if (tmp != 0) {
            return tmp;
        }
        tmp = this.month - other.month;
        if (tmp != 0) {
            return tmp;
        }
        tmp = this.week - other.week;
        if (tmp != 0) {
            return tmp;
        }
        tmp = this.day - other.day;
        if (tmp != 0) {
            return tmp;
        }
        tmp = this.date.compareTo(other.date);
        if (tmp != 0) {
            return tmp;
        }
        tmp = this.type.compareTo(other.type);
        return tmp;


    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(id);
        out.writeInt(year);
        out.writeInt(season);
        out.writeInt(month);
        out.writeInt(week);
        out.writeInt(day);
        //Date类型写成时间戳??
        out.writeLong(date.getTime());
        out.writeUTF(type);

    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.id = in.readInt();
        this.year = in.readInt();
        this.season = in.readInt();
        this.month = in.readInt();
        this.week = in.readInt();
        this.day = in.readInt();
        this.date.setTime(in.readLong()); //date类型的读
        this.type = in.readUTF();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DateDimension)) return false;
        DateDimension that = (DateDimension) o;
        return getId() == that.getId() &&
                getYear() == that.getYear() &&
                getSeason() == that.getSeason() &&
                getMonth() == that.getMonth() &&
                getWeek() == that.getWeek() &&
                getDay() == that.getDay() &&
                Objects.equals(getDate(), that.getDate()) &&
                Objects.equals(getType(), that.getType());
    }

    @Override
    public int hashCode() {

        /*return Objects.hash(getId(), getYear(),
                getSeason(), getMonth(), getWeek(),
                getDay(), getDate(), getType());*/
        int result = id;
        result = 31 * result + year;
        result = 31 * result + season;
        result = 31 * result + month;
        result = 31 * result + week;
        result = 31 * result + day;
        result = 31 * result + date.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "DateDimension{" +
                "id=" + id +
                ", year=" + year +
                ", season=" + season +
                ", month=" + month +
                ", week=" + week +
                ", day=" + day +
                ", date=" + date +
                ", type='" + type + '\'' +
                '}';
    }
}
