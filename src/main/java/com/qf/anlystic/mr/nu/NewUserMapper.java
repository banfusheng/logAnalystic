/**
 *  从hbase中筛选,
 *  统计新增用户和新增的总用户的mapper类。
 *  需要Launch时间中的uuid为一个数
 *
 * @author Administrator
 * @create 2018/8/20
 * @since 1.0.0
 */
package com.qf.anlystic.mr.nu;

import com.qf.anlystic.model.dim.base.BrowserDimension;
import com.qf.anlystic.model.dim.base.DateDimension;
import com.qf.anlystic.model.dim.base.KpiDimension;
import com.qf.anlystic.model.dim.base.PlatFormDimension;
import com.qf.anlystic.model.dim.key.StatsCommonDimension;
import com.qf.anlystic.model.dim.key.StatsUserDimension;
import com.qf.anlystic.model.dim.value.map.TimeOutputValue;
import com.qf.common.DateEnum;
import com.qf.common.EventLogConstants;
import com.qf.common.KpiType;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;

public class NewUserMapper extends TableMapper<StatsUserDimension,TimeOutputValue> {
private static final Logger logger = Logger.getLogger(NewUserMapper.class);

private static  StatsUserDimension k = new StatsUserDimension();
private static TimeOutputValue v = new TimeOutputValue();
//列簇的byte数组
private static byte[] family = Bytes.toBytes(EventLogConstants.EVENT_LOG_FAMILY_NAME);
//new_install_user的kpi维度
private static KpiDimension newUserKpi = new KpiDimension(KpiType.NEW_USER.kpiName);
// browser_new_install_user的kpi维度
private static KpiDimension browserNewUserKpi = new KpiDimension(
        KpiType.BROWSER_NEW_INSTALL_USER.kpiName);
    @Override
    protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {
        String uuid = Bytes.toString(value.getValue(family,
                Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_UUID)));
        String serverTime = Bytes.toString(value.getValue(family,
                Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_SERVER_TIME)));
        String platform = Bytes.toString(value.getValue(family,
                Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_PLATFORM)));
        String browerName = Bytes.toString(value.getValue(family,
                Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_BROWSER_NAME)));
        String browerVersion = Bytes.toString(value.getValue(family,
                Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_BROWSER_VERSION)));

        //由于这三个字段要作为主键   进行空判断
        if(StringUtils.isEmpty(uuid) || StringUtils.isEmpty(serverTime)
                ||StringUtils.isEmpty(platform)){
            logger.warn("uuid,serverTime,platform中有空值" + "uuid = " + uuid
                    + "serverTime" + serverTime + "platform" + platform);
            return;
        }

        //构建输出的value
        long serverTimeOfLong = Long.valueOf(serverTime);
        this.v.setId(uuid);
        this.v.setTime(serverTimeOfLong);

        //构建输出的key
        //构建平台的两个维度 本身  和 all
        List<PlatFormDimension> platFormDimensions =
                PlatFormDimension.buildList(platform);
        DateDimension dateDimension = DateDimension.buildDate(
                serverTimeOfLong, DateEnum.DAY);
        //浏览器的维度  本身的本版本和 all(全部版本)
        List<BrowserDimension> browserDimensions =
                BrowserDimension.buildList(browerName, browerVersion);
        StatsCommonDimension statsCommonDimension = this.k.getStatsCommonDimension();
        //为statsCommonDimension赋值
        statsCommonDimension.setDateDimension(dateDimension);

        BrowserDimension defaultBrowserDimension =
                new BrowserDimension("", "");
        /**
         * 这样会产生6条数据
         */
        for (PlatFormDimension pl : platFormDimensions){
            statsCommonDimension.setKpiDimension(newUserKpi);
            statsCommonDimension.setPlatFormDimension(pl);

            this.k.setStatsCommonDimension(statsCommonDimension);
            //为啥用默认的浏览器
            this.k.setBrowserDimension(defaultBrowserDimension);

            context.write(this.k,this.v);

            for (BrowserDimension bd : browserDimensions){
                statsCommonDimension.setKpiDimension(browserNewUserKpi);
                this.k.setStatsCommonDimension(statsCommonDimension);
                this.k.setBrowserDimension(bd);

                context.write(this.k,this.v);
            }

        }


    }
}
