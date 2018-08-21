/**
 * 将hdfs中的数据清洗之后存储到hdfs，
 * 以便后续hive映射做数据仓库的分析
 *
 * @author Administrator
 * @create 2018/8/19
 * @since 1.0.0
 */

package com.qf.etl.mr.tohdfs;

import com.qf.common.EventLogConstants;
import com.qf.etl.util.LogUtil;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Map;

/*
create external table if not exists event_logs(
s_time string,
en string,
ver string,
u_ud string,
u_mid string,
u_sid string,
c_time string,
language  string,
b_iev string,
b_rst string,
p_url string,
p_ref string,
tt string,
pl string,
o_id string,
`on` string,
cut string,
cua string,
pt string,
ca string,
ac string,
kv_ string,
du string,
os string,
os_v string,
browser string,
browser_v string,
country string,
province string,
city string
)
partitioned by(month String,day string)
row format delimited fields terminated by '\001'
;
在hdfs上的数据是移动  在本地是copy
load data inpath '/ods/month=08/day=17' into event_logs logs partition(month=08,day=17);
 */
public class LogToHdfsMapper extends Mapper<Object, Text, NullWritable, LogDataWritable> {
    private static final Logger logger = Logger.getLogger(LogToHdfsMapper.class);
    private byte[] family = Bytes.toBytes(EventLogConstants.EVENT_LOG_FAMILY_NAME);
    //输入输出和过滤行记录
    private int inputRecords, outputRecords, filterRecords = 0;

    @Override
    protected void map(Object key, Text value, Context context)
            throws IOException, InterruptedException {
        this.inputRecords++;
        logger.info("输入的日志为:" + value.toString());
        Map<String, String> info = LogUtil.hangleLog(value.toString());
        if (info.isEmpty()) {
            this.filterRecords++;
            return;
        }
        //获取事件根据事件分别存储
        //这里统一存到 一张表中
        String eventName = info.get(EventLogConstants.LOG_COLUMN_NAME_EVENT_NAME);
        EventLogConstants.EventEnum eventEnum = EventLogConstants.EventEnum.valueOfAlias(eventName);
        switch (eventEnum) {
            case LANUCH:
            case EVENT:
            case PAGEVIEW:
            case CHARGEREQUEST:
            case CHARGESUCCESS:
            case CHARGEREFUND:
                handleInfo(info, eventName, context);
                break;
            default:
                filterRecords++;
                logger.warn("该事件暂时不支持数据的清洗.event:" + eventName);
                break;
        }


    }

    /**
     * 写数据到hdfs中
     *
     * @param info
     * @param eventName
     * @param context
     */
    private void handleInfo(Map<String, String> info, String eventName, Context context) {
        try {
            if (!info.isEmpty()) {
                LogDataWritable ld = new LogDataWritable();
                ld.s_time = info.get(EventLogConstants.LOG_COLUMN_NAME_SERVER_TIME);
                ld.en = info.get(EventLogConstants.LOG_COLUMN_NAME_EVENT_NAME);
                ld.ver = info.get(EventLogConstants.LOG_COLUMN_NAME_VERSION);
                ld.u_ud = info.get(EventLogConstants.LOG_COLUMN_NAME_UUID);
                ld.u_mid = info.get(EventLogConstants.LOG_COLUMN_NAME_MEMBER_ID);
                ld.u_sid = info.get(EventLogConstants.LOG_COLUMN_NAME_SESSION_ID);
                ld.c_time= info.get(EventLogConstants.LOG_COLUMN_NAME_CLIENT_TIME);
                ld.language = info.get(EventLogConstants.LOG_COLUMN_NAME_LANGUAGE);
                ld.b_iev = info.get(EventLogConstants.LOG_COLUMN_NAME_USERAGENT);
                ld.b_rst = info.get(EventLogConstants.LOG_COLUMN_NAME_RESOLUTION);
                ld.p_url = info.get(EventLogConstants.LOG_COLUMN_NAME_CURRENT_URL);
                ld.p_ref = info.get(EventLogConstants.LOG_COLUMN_NAME_PREFER_URL);
                ld.tt = info.get(EventLogConstants.LOG_COLUMN_NAME_TITLE);
                ld.pl = info.get(EventLogConstants.LOG_COLUMN_NAME_PLATFORM);
                ld.oid = info.get(EventLogConstants.LOG_COLUMN_NAME_ORDER_ID);
                ld.on = info.get(EventLogConstants.LOG_COLUMN_NAME_ORDER_NAME);
                ld.cut = info.get(EventLogConstants.LOG_COLUMN_NAME_ORDER_CURRENCY_TYPE);
                ld.cua = info.get(EventLogConstants.LOG_COLUMN_NAME_ORDER_CURRENCY_AMOUT);
                ld.pt = info.get(EventLogConstants.LOG_COLUMN_NAME_ORDER_PAYMENT_TYPE);
                ld.ca = info.get(EventLogConstants.LOG_COLUMN_NAME_EVENT_CATEGORY);
                ld.ac = info.get(EventLogConstants.LOG_COLUMN_NAME_EVENT_ACTION);
                ld.kv_ = info.get(EventLogConstants.LOG_COLUMN_NAME_EVENT_START);
                ld.du = info.get(EventLogConstants.LOG_COLUMN_NAME_EVENT_DURATION);
                ld.os = info.get(EventLogConstants.LOG_COLUMN_NAME_OS_NAME);
                ld.os_v = info.get(EventLogConstants.LOG_COLUMN_NAME_OS_VERSION);
                ld.browser = info.get(EventLogConstants.LOG_COLUMN_NAME_BROWSER_NAME);
                ld.browser_v = info.get(EventLogConstants.LOG_COLUMN_NAME_BROWSER_VERSION);
                ld.country = info.get(EventLogConstants.LOG_COLUMN_NAME_COUNTRY);
                ld.province = info.get(EventLogConstants.LOG_COLUMN_NAME_PROVINCE);
                ld.city = info.get(EventLogConstants.LOG_COLUMN_NAME_CITY);

                //输出

                context.write(NullWritable.get(), ld);

                this.outputRecords++;
            }
        } catch (Exception e) {
            this.filterRecords ++;
            logger.warn("写出到hbase异常.",e);
        }

    }
    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        logger.info("输入、输出和过滤的记录数.inputRecords"+this.inputRecords
                +"  outputRecords:"+this.outputRecords+"  filterRecords:"+this.filterRecords);
    }
}
