/**
 * 为新增用户的ps赋值
 *
 * @author Administrator
 * @create 2018/8/20
 * @since 1.0.0
 */
package com.qf.anlystic.mr.au;

import com.qf.anlystic.model.dim.base.BaseDimension;
import com.qf.anlystic.model.dim.key.StatsUserDimension;
import com.qf.anlystic.model.dim.out.IOutputWritter;
import com.qf.anlystic.model.dim.value.BaseStatsValueWritable;
import com.qf.anlystic.model.dim.value.reduce.MapWritableValue;
import com.qf.anlystic.service.IDimensionConvert;
import com.qf.common.GlobalConstant;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ActiveUserWritter implements IOutputWritter {
    @Override
    public void write(Configuration conf, BaseDimension key,
                      BaseStatsValueWritable value, PreparedStatement ps,
                      IDimensionConvert convert) throws SQLException, IOException {

        StatsUserDimension statsUserDimension = (StatsUserDimension) key;
        MapWritableValue mapWritableValue = (MapWritableValue) value;
        //获得活跃用户的数量
        int activeuser = ((IntWritable) mapWritableValue.getValue().get(
                new IntWritable(-1))).get();

        //为ps赋值
        int i = 0;
        /*
        insert into `stats_user` (
            `date_dimension_id`,
            `platform_dimension_id`,
            `active_users`,
            `created`)
            values(?,?,?,?) on duplicate key update `active_users` = ?
         */
        //获取date的id
        ps.setInt(++i, convert.getDimensionIDByDimension(
                statsUserDimension.getStatsCommonDimension().getDateDimension()
        ));
        //获取平台的id
        ps.setInt(++i, convert.getDimensionIDByDimension(
                statsUserDimension.getStatsCommonDimension().getPlatFormDimension()
        ));
        ps.setInt(++i,activeuser);
        ps.setString(++i,conf.get(GlobalConstant.RUNNING_DATE));
        ps.setInt(++i, activeuser);
        //添加到批处理中
        ps.addBatch();

    }
}
