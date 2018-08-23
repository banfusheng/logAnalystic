/**
 * 新增的用户和新增的总用户统计的Reducer类
 *
 * @author Administrator
 * @create 2018/8/20
 * @since 1.0.0
 */
package com.qf.anlystic.mr.nm;

import com.qf.anlystic.model.dim.key.StatsUserDimension;
import com.qf.anlystic.model.dim.value.map.TimeOutputValue;
import com.qf.anlystic.model.dim.value.reduce.MapWritableValue;
import com.qf.common.KpiType;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class NewMemberReducer extends Reducer<StatsUserDimension,
        TimeOutputValue, StatsUserDimension, MapWritableValue> {
    //用set集合来实现去重的效果
    private Set<String> unique = new HashSet<>();
    private MapWritableValue v = new MapWritableValue();


    @Override
    protected void reduce(StatsUserDimension key, Iterable<TimeOutputValue> values,
                          Context context) throws IOException, InterruptedException {
        //构造reduce、阶段输出的value
        MapWritable map = new MapWritable();
        //每次运行需要把set集合清空
        this.unique.clear();
        //循环map阶段传过来的value
        for (TimeOutputValue tv : values) {
            //存储的是uuid 用户的唯一id
            this.unique.add(tv.getId());
        }
        //需要将排重过后的memebrId进行存储memberInfo表
        for(String memberId:this.unique){
            //将每一个memberid添加到memberInfo表中
            this.v.setKpi(KpiType.MEMBER_INFO);
            map.put(new IntWritable(-1),new Text(memberId));
            this.v.setValue(map);
            //输出
            context.write(key,this.v);
        }


        //构建输出的value
        MapWritable mapWritable = new MapWritable();
        mapWritable.put(new IntWritable(-1), new IntWritable(this.unique.size()));
        v.setValue(mapWritable);
        //两个kpi的地方
        v.setKpi(KpiType.valueOfName(key.getStatsCommonDimension()
                .getKpiDimension().getKpiName()));


        context.write(key, v);
    }
}
