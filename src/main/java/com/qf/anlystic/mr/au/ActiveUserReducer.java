/**
 * 活跃用户和活跃的总用户统计的Reducer类
 *
 * @author Administrator
 * @create 2018/8/20
 * @since 1.0.0
 */
package com.qf.anlystic.mr.au;

import com.qf.anlystic.model.dim.key.StatsUserDimension;
import com.qf.anlystic.model.dim.value.map.TimeOutputValue;
import com.qf.anlystic.model.dim.value.reduce.MapWritableValue;
import com.qf.common.DateEnum;
import com.qf.common.KpiType;
import com.qf.util.Timeutil;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ActiveUserReducer extends Reducer<StatsUserDimension,
        TimeOutputValue, StatsUserDimension, MapWritableValue> {
    //用set集合来实现去重的效果  按天统计
    private Set<String> unique = new HashSet<>();
    private MapWritableValue v = new MapWritableValue();
    //按小时统计的属性  小时:该小时段内存储id的集合
    private Map<Integer, Set<String>> hourlyUnique =
            new HashMap<Integer, Set<String>>();
    private MapWritable houlyMap = new MapWritable();  //时间数:个数


    @Override
    protected void reduce(StatsUserDimension key, Iterable<TimeOutputValue> values,
                          Context context) throws IOException, InterruptedException {
        String kn = key.getStatsCommonDimension().getKpiDimension().getKpiName();
        try {
            if (KpiType.HOURLY_ACTIVE_USER.kpiName.equals(kn)) {
                //处理按小时统计
                for (TimeOutputValue tv : values) {
                    //获取该时间戳的对应的小时
                    int hour = Timeutil.getDateInfo(tv.getTime(), DateEnum.HOUR);
                    //获取hourlyUnique中的value
                    this.hourlyUnique.get(hour).add(tv.getId());

                }
                this.v.setKpi(KpiType.HOURLY_ACTIVE_USER);
                //为hourlyMap赋值
                for (Map.Entry<Integer, Set<String>> en : hourlyUnique.entrySet()) {
                    this.houlyMap.put(new IntWritable(en.getKey()),
                            new IntWritable(en.getValue().size()));
                }
                this.v.setValue(this.houlyMap);
                //输出
                context.write(key, this.v);

            } else {
                //每次运行需要把set集合清空
                this.unique.clear();
                //循环map阶段传过来的value
                for (TimeOutputValue tv : values) {
                    //存储的是uuid 用户的唯一id
                    this.unique.add(tv.getId());
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭按小时的统计
            this.houlyMap.clear();
            this.hourlyUnique.clear();
            for (int i = 0; i < 24; i++) {
                this.hourlyUnique.put(i, new HashSet<String>());
                this.houlyMap.put(new IntWritable(i), new IntWritable(0));
            }

        }
    }
}
