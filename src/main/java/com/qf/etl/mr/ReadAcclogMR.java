/**
 * 〈一句话功能简述〉 <br>
 *
 * @author Administrator
 * @create 2018/8/17
 * @since 1.0.0
 */
package com.qf.etl.mr;

import com.qf.etl.util.acctestlogUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.Map;

public class ReadAcclogMR {
    public static  class myMapper extends Mapper<LongWritable,Text,Text,NullWritable>{
        private static Text k = new Text();
        private static NullWritable v = NullWritable.get();

        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            String line = value.toString();
            Map<String, String> splitlog = acctestlogUtil.splitlog(line);

            k.set(splitlog.toString());
            context.write(k,v);
        }
    }
    /*public static class myReduce extends Reducer<>{

    }*/
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "accesslog");
        //设置jar的位置
        job.setJarByClass(ReadAcclogMR.class);
        //设置Map类的业务实现
        job.setMapperClass(myMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(NullWritable.class);

        //设置输入输出路径
        FileInputFormat.setInputPaths(job,new Path("E://test/testdata/access.log"));
        FileOutputFormat.setOutputPath(job,new Path("E://test/out/004"));
        //提交job
        boolean b = job.waitForCompletion(true);
        //退出
        System.exit(b ? 0: 1);


    }
}
