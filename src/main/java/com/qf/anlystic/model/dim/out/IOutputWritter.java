/**
 * 每一个指标的sql语句赋值的接口
 *自定义reduce阶段输出的格式
 * 操作最终结果表的接口
 *
 * @author Administrator
 * @create 2018/8/20
 * @since 1.0.0
 */
package com.qf.anlystic.model.dim.out;


import com.qf.anlystic.model.dim.base.BaseDimension;
import com.qf.anlystic.model.dim.value.BaseStatsValueWritable;
import com.qf.anlystic.service.IDimensionConvert;
import org.apache.hadoop.conf.Configuration;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface IOutputWritter {
    /**
     * 将最终的结果存储到mysql中
     * @param conf   用于传递kpi
     * @param key  存储维度
     * @param value 存储统计值
     * @param ps   对应kpi的sql的ps
     * @param convert 获取对应的维度的id值
     * @throws SQLException
     * @throws IOException
     */
    void write(Configuration conf, BaseDimension key,
               BaseStatsValueWritable value,
               PreparedStatement ps, IDimensionConvert convert)
            throws SQLException,IOException;


}
