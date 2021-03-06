/**
 * 从hbase中筛选,
 * 统计新增会员和新增的总会员的mapper类。
 * 需要Launch时间中的uuid为一个数
 *
 * @author Administrator
 * @create 2018/8/20
 * @since 1.0.0
 */
package com.qf.anlystic.mr.nm;

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
import com.qf.util.JdbcUtil;
import com.qf.util.MemberUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

public class NewMemberMapper extends TableMapper<StatsUserDimension, TimeOutputValue> {
    private static final Logger logger = Logger.getLogger(NewMemberMapper.class);

    private static StatsUserDimension k = new StatsUserDimension();
    private static TimeOutputValue v = new TimeOutputValue();
    //列簇的byte数组
    private static byte[] family = Bytes.toBytes(EventLogConstants.EVENT_LOG_FAMILY_NAME);
    //new_Member的kpi维度
    private static KpiDimension newMember = new KpiDimension(KpiType.NEW_MEMBER.kpiName);
    // browser_new_Member的kpi维度
    private static KpiDimension browserNewMember = new KpiDimension(
            KpiType.BROWSER_NEW_MEMBER.kpiName);
    private Connection conn = null;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        conn = JdbcUtil.getConn();

    }

    @Override
    protected void map(ImmutableBytesWritable key, Result value, Context context)
            throws IOException, InterruptedException {
        String memberId = Bytes.toString(value.getValue(family,
                Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_MEMBER_ID)));
        String serverTime = Bytes.toString(value.getValue(family,
                Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_SERVER_TIME)));
        String platform = Bytes.toString(value.getValue(family,
                Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_PLATFORM)));

        String browerName = Bytes.toString(value.getValue(family,
                Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_BROWSER_NAME)));
        String browerVersion = Bytes.toString(value.getValue(family,
                Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_BROWSER_VERSION)));

        //由于这三个字段要作为主键   进行空判断
        if (StringUtils.isEmpty(memberId ) || StringUtils.isEmpty(serverTime)
                || StringUtils.isEmpty(platform)) {
            logger.warn("uuid,serverTime,platform中有空值" + "memberId  = " +memberId
                    + "serverTime" + serverTime + "platform" + platform);
            return;
        }
        //判断memberId是否合法
        if (!MemberUtil.checkMemberId(memberId)){
            logger.warn("该uuid不和法" + memberId);
            return;
        }
        //判断该memberId是否是新增的即在缓存和数据库中查询是否有该memberID
        if (!MemberUtil.isNewMember(memberId, conn, context.getConfiguration())) { // 不是新增
            logger.warn("该会员不是新增会员:" + memberId);
            return;
        }
        logger.warn("该会员是新增会员:" + memberId);


        //构建输出的value
        long serverTimeOfLong = Long.valueOf(serverTime);
        this.v.setId(memberId);
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
        //循环平台维度的平台
        for (PlatFormDimension pl : platFormDimensions) {
            statsCommonDimension.setKpiDimension(newMember);
            statsCommonDimension.setPlatFormDimension(pl);

            this.k.setStatsCommonDimension(statsCommonDimension);
            //
            this.k.setBrowserDimension(defaultBrowserDimension);

            context.write(this.k, this.v);
            //循环浏览器维度的集合
            for (BrowserDimension bd : browserDimensions) {
                statsCommonDimension.setKpiDimension(browserNewMember);
                this.k.setStatsCommonDimension(statsCommonDimension);
                this.k.setBrowserDimension(bd);
                //输出  用于浏览器模块的的新增用户上网计算
                context.write(this.k, this.v);
            }

        }


    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        JdbcUtil.close(conn, null, null);
    }
}
