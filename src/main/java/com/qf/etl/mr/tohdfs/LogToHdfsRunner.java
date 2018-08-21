/**
 * 〈一句话功能简述〉 <br>
 *
 * @author Administrator
 * @create 2018/8/19
 * @since 1.0.0
 */
package com.qf.etl.mr.tohdfs;

import com.qf.common.GlobalConstant;
import com.qf.util.Timeutil;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

import java.io.IOException;

public class LogToHdfsRunner implements Tool {
    private static final Logger logger = Logger.
            getLogger(LogToHdfsRunner.class);
    Configuration conf = null;

    public static void main(String[] args) {
        try {
            ToolRunner.run(new Configuration(), new LogToHdfsRunner(), args);
        } catch (Exception e) {
            logger.warn("运行etl to hdfs 异常", e);
        }
    }

    /**
     * yarn jar *.jar className -d 2018-08-17
     *
     * @param args /logs/07/26
     * @return
     * @throws Exception
     */
    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = this.getConf();
        this.setArgs(conf, args);
        Job job = Job.getInstance(conf, "LogTohdfs");
        job.setJarByClass(LogToHdfsRunner.class);

        job.setMapperClass(LogToHdfsMapper.class);
        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(LogDataWritable.class);

        job.setNumReduceTasks(0);

        //设置map阶段的输入路禁
        this.setPath(job);
        return job.waitForCompletion(true) ? 0 : 1;
    }

    /**
     * 设置路径
     *
     * @param job
     */
    private void setPath(Job job) {
        FileSystem fs = null;
        Configuration conf = job.getConfiguration();
        String date = conf.get(GlobalConstant.RUNNING_DATE);
        String[] fields = date.split("-");
        Path inpath = new Path("/logs/" + fields[1] + "/"
                + fields[2]);

        Path outpath = new Path("/ods/month=" + fields[1] +
                "/day=" + fields[2]);

        try {
            fs = FileSystem.get(conf);
            //判断输入路禁是否存在
            if (fs.exists(inpath)){
                FileInputFormat.setInputPaths(job,inpath);

            }else {
                throw new RuntimeException("输入路禁异常:"+inpath.toString());
            }
            //设置输出路径 先判断是否存在若存在就删除
            if (fs.exists(outpath)){
                fs.delete(outpath,true);
            }
            FileOutputFormat.setOutputPath(job,outpath);


        } catch (IOException e) {
            logger.warn("获取hdfs的FileSystem异常" +e);
        }finally {
            if (fs != null){
                try {
                    fs.close();
                } catch (IOException e) {
                    //do nothing
                }
            }
        }
    }

    /**
     * 参数处理
     * 将接收到的日期存储在conf中,以供后续使用
     * 如果没有传递日期,则默认使用昨天的日期
     * 可以抽成工具类方法
     *
     * @param conf
     * @param args
     */
    private void setArgs(Configuration conf, String[] args) {
        String date = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-d")) {
                if (i + 1 < args.length) {
                    date = args[i + 1];
                    break;
                }
            }
        }
        //再次判断时间的规范性和是否为空
        if (StringUtils.isBlank(date) || !Timeutil.isValidateDate(date)) {
            date = Timeutil.getYesterday();
        }
        conf.set(GlobalConstant.RUNNING_DATE, date);

    }

    /*这个方法什么时候调用的*/
    @Override
    public void setConf(Configuration conf) {
        //此处为何要用HbaseConfiguration
        //在写入HDFS中时也可以用,因为都是Configuration对象
        this.conf = HBaseConfiguration.create();
    }

    @Override
    public Configuration getConf() {
        return this.conf;
    }
}
