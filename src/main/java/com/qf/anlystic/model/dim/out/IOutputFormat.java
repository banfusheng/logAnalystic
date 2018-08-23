/**
 * 自定义输出到MySQL的输出格式类
 *
 * @author Administrator
 * @create 2018/8/20
 * @since 1.0.0
 */
package com.qf.anlystic.model.dim.out;

import com.qf.anlystic.model.dim.base.BaseDimension;
import com.qf.anlystic.model.dim.value.BaseStatsValueWritable;
import com.qf.anlystic.service.IDimensionConvert;
import com.qf.anlystic.service.impl.IDimensionConvertImpl;
import com.qf.common.GlobalConstant;
import com.qf.common.KpiType;
import com.qf.util.JdbcUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class IOutputFormat extends OutputFormat<BaseDimension, BaseStatsValueWritable> {
    private static final Logger logger = Logger.getLogger(IOutputFormat.class);

    /**
     * @param context
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public RecordWriter<BaseDimension, BaseStatsValueWritable> getRecordWriter(
            TaskAttemptContext context) throws IOException, InterruptedException {
        Configuration conf = context.getConfiguration();
        Connection conn = JdbcUtil.getConn();
        IDimensionConvert convert = new IDimensionConvertImpl();

        return new IOutputRecordWritter(conn, conf, convert);
    }

    //检测输出的空间
    @Override
    public void checkOutputSpecs(JobContext jobContext) throws IOException, InterruptedException {
        //do nothing
    }

    /**设置输出的路径
     * @param context
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public OutputCommitter getOutputCommitter(TaskAttemptContext context) throws IOException, InterruptedException {
        return new FileOutputCommitter(null, context);
        ////FileOutputFormat.getOutputPath(context)
    }


    /**
     * 自定义内部类，用于封装输出的记录
     */
    public class IOutputRecordWritter extends
            RecordWriter<BaseDimension, BaseStatsValueWritable> {
        private Connection conn = null;
        private Configuration conf = null;
        private IDimensionConvert convert = null;
        //存储kpi  kpi对应的ps
        private Map<KpiType, PreparedStatement> map = new HashMap<>();
        //存储kpi:count对应的累计数
        private Map<KpiType, Integer> batch = new HashMap<>();

        public IOutputRecordWritter(Connection conn, Configuration conf,
                                    IDimensionConvert convert) {
            this.conn = conn;
            this.conf = conf;
            this.convert = convert;
        }

        /**
         * 将结果输出的核心方法
         *
         * @param key
         * @param value
         * @throws IOException
         * @throws InterruptedException
         */
        @Override
        public void write(BaseDimension key,
                          BaseStatsValueWritable value)
                throws IOException, InterruptedException {
            if (key == null || value == null) {
                return;//不用写出
            }
            PreparedStatement ps = null;
            try {
                //获取kpi 根据kpi来获取对应的sql
                KpiType kpi = value.getKpi();
                int count = 1;
                //从map中获取对应的ps
                if (map.get(kpi) == null) {
                    //从conf中获取对应的kpi，因为conf中会存储sql
                    // 从 output-mapping中对应关系获取
                    ps = this.conn.prepareStatement(conf.get(kpi.kpiName));
                    map.put(kpi, ps);
                } else {
                    ps = map.get(kpi);
                    count = batch.get(kpi);
                    count++;
                }
                batch.put(kpi, count);

                //为ps赋值  writer_kpiName ==赋值类的类名
                String writerName = conf.get(GlobalConstant.WRITTER_PREFIX + kpi.kpiName);
                //转换成类
                Class<?> classz = Class.forName(writerName);
                //获取clssz对应的对象 将类转换成接口
                IOutputWritter iw = (IOutputWritter) classz.newInstance();
                //该方法需要对应对的类去实现  为ps赋值
                iw.write(conf, key, value, ps, convert);

                //当达到一定的batch就可以触发执行
                if (count % GlobalConstant.BATCH_VALUE == 0) {
                    //批量提交
                    ps.executeBatch();
                    //提交
                    conn.commit();
                    batch.remove(kpi);
                }

            } catch (Exception e) {
                logger.warn("执行getRecordWritter方法失败。", e);
            }

        }

        /**
         * 关闭该关闭的对象
         *
         * @param context
         * @throws IOException
         * @throws InterruptedException
         */
        @Override
        public void close(TaskAttemptContext context)
                throws IOException, InterruptedException {
            try {
                //循环将剩下的map中的ps进行执行
                for (Map.Entry<KpiType, PreparedStatement> en : map.entrySet()) {
                    en.getValue().executeBatch();
                }
            } catch (SQLException e) {
                logger.warn("批量执行剩余的ps异常.", e);
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        //关闭连接抛异常
                    } finally {
                        //循环将剩下的map中的ps进行关闭
                        for (Map.Entry<KpiType, PreparedStatement> en : map.entrySet()) {
                            try {
                                en.getValue().close();
                            } catch (SQLException e) {
                                //循环关闭ps异常
                            }
                        }
                    }
                }
            }
        }
    }
}

