/**
 * 运行tohbaseMapper方法
 *
 * @author Administrator
 * @create 2018/8/17
 * @since 1.0.0
 */
package com.qf.etl.mr.tohbase;

import com.qf.common.EventLogConstants;
import com.qf.util.Timeutil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

import java.io.IOException;

public class TohbaseRunner implements Tool {
    private static final Logger logger = Logger.getLogger(TohbaseRunner.class);
    private Configuration conf = null;

    public static void main(String[] args) {
        try {
            ToolRunner.run(new Configuration(), new TohbaseRunner(), args);
        } catch (Exception e) {
            logger.warn("执行job方法失败 清洗异常", e);
        }
    }

    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = getConf();
        Job job = Job.getInstance(conf, "TOhbase");
        //????
        job.setJarByClass(TohbaseRunner.class);

        job.setMapperClass(TohbaseMapper.class);
        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(Put.class);

        //TODO 输入参数处理 和设置job的输入路径处理
        this.handleArgs(args, conf, job);
        //TODO 判断hbase中的表是否存在
        this.isExistsHbaseTable(conf);

        //初始化reduce addDependencyJars:false 本地提交本地运行，反之等于true是集群运行
        TableMapReduceUtil.initTableReducerJob(EventLogConstants.EVENT_LOG_HBASE_NAME,
                null, job, null, null, null,
                null, true);

        job.setNumReduceTasks(0);
        //将不能识别的资源文件添加到分布式缓存文件

        return job.waitForCompletion(true) ? 0 : 1;


    }

    @Override
    public void setConf(Configuration configuration) {
        this.conf = HBaseConfiguration.create();

    }

    @Override
    public Configuration getConf() {
        return this.conf;
    }

    /**
     * 判断hbase表是否存在
     * @param conf
     */
    private void isExistsHbaseTable(Configuration conf) {
        Admin admin = null;
        //HBaseAdmin ha = null;
        try {
            //ha = new HBaseAdmin(conf);
            Connection conn = ConnectionFactory.createConnection(conf);
            admin = conn.getAdmin();
            //hbase的表
            TableName tn = TableName.valueOf(EventLogConstants.EVENT_LOG_HBASE_NAME);
            //判断是否存在，存在则不管，否则创建
            if (!admin.tableExists(tn)) {
                HTableDescriptor hdc = new HTableDescriptor(tn);
                HColumnDescriptor hcd = new HColumnDescriptor(EventLogConstants.EVENT_LOG_FAMILY_NAME);
                hdc.addFamily(hcd);
                //提交创建
                admin.createTable(hdc);
                //ha.createTable(hdc);
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
/*            if(ha != null) {  //关闭对象
                try {
                    ha.close();
                } catch (IOException e) {
                    //do nothing
                }
            }*/
            if (admin != null) {  //关闭对象
                try {
                    admin.close();
                } catch (IOException e) {
                    //do nothing
                }
            }
        }
    }

    /**
     * 设置清洗数据的输入路径
     * 运行：yarn jar *.jar .class -d 2018-08-15
     * @param args
     * @param conf
     */
    private void handleArgs(String[] args, Configuration conf, Job job) {
        FileSystem fs = null;
        try {
            String date = null;
            //循环参数列表
            for (int i=0;i < args.length;i++){
                if("-d".equals(args[i])){
                    if(i+1 < args.length){
                        date = args[i+1];
                        break;
                    }
                }
            }
            //如果date为空或者无效，则默认使用昨天的date。将date设置到conf中
            if(date == null || !Timeutil.isValidateDate(date)){
                date = Timeutil.getYesterday();
            }
            //把时间存到conf中
            //conf.set

            //设置输入的路径   /logs/07/05/**.log
            fs = FileSystem.get(conf);
            String dates [] = date.split("-");
            Path inputPath = new Path("/logs/"+dates[1]+"/"+dates[2]);
            if(fs.exists(inputPath)){
                FileInputFormat.addInputPath(job,inputPath);
            } else {
                throw new RuntimeException("job的运行数据目录不存在.");
            }
        } catch (IOException e) {
            logger.warn("设置作业的输入路径异常.",e);
        } finally {
            if (fs != null){
                try {
                    fs.close();
                } catch (IOException e) {
                    //do nothing
                }
            }
        }


    }


}
