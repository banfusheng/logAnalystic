/**
 * 浏览器活跃会员的ps赋值类
 *
 * @author Administrator
 * @create 2018/8/20
 * @since 1.0.0
 */
package com.qf.anlystic.mr.am;

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

public class BrowserActiveMemberrWritter implements IOutputWritter {
    @Override
    public void write(Configuration conf, BaseDimension key,
                      BaseStatsValueWritable value, PreparedStatement ps,
                      IDimensionConvert convert)
            throws SQLException, IOException {
        StatsUserDimension statsUserDimension = (StatsUserDimension) key;
        MapWritableValue mapWritableValue = (MapWritableValue) value;
        int browserActvieMemberNums = ((IntWritable) mapWritableValue.getValue().get(new IntWritable(-1))).get();

        //为ps设置值
        /*
         browser_active_user--------
           insert into `stats_device_browser` (
            `date_dimension_id`,
            `platform_dimension_id`,
            `browser_dimension_id`,
            `active_members`,
            `created`)
            values(?,?,?,?,?) on duplicate key update `active_members` = ?
         */
        int i = 0;
        ps.setInt(++i, convert.getDimensionIDByDimension(
                statsUserDimension.getStatsCommonDimension().getDateDimension()
        ));
        ps.setInt(++i, convert.getDimensionIDByDimension(
                statsUserDimension.getStatsCommonDimension().getPlatFormDimension()
        ));
        ps.setInt(++i, convert.getDimensionIDByDimension(
                statsUserDimension.getBrowserDimension()
        ));
        ps.setInt(++i, browserActvieMemberNums);
        ps.setString(++i, conf.get(GlobalConstant.RUNNING_DATE));
        ps.setInt(++i, browserActvieMemberNums);
        //将ps添加到batch
        ps.addBatch();
    }
}
