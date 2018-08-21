/**
 * 〈一句话功能简述〉 <br>
 *
 * @author Administrator
 * @create 2018/8/18
 * @since 1.0.0
 */
package com.qf.common;

public enum DateEnum {
    YEAR("year"),
    SEASON("season"),
    MONTH("month"),
    WEEK("week"),
    DAY("day"),
    HOUR("hour");
    public final String typeName;

    DateEnum(String typeName) {
        this.typeName = typeName;
    }

    /**
     * 根据typeName来放回时间枚举
     *
     * @param name
     * @return
     */
    public static DateEnum valueofName(String name) {
        for (DateEnum dateEnum : values()) {
            if (dateEnum.typeName.equals(name)) {
                return dateEnum;
            }
        }
        return null;
    }


}
