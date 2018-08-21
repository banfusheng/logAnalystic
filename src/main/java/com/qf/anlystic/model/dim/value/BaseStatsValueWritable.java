/**
 * map或者是reduce阶段输出value类型的顶级父类
 *
 * @author Administrator
 * @create 2018/8/20
 * @since 1.0.0
 */
package com.qf.anlystic.model.dim.value;

import com.qf.common.KpiType;
import org.apache.hadoop.io.Writable;

public abstract class BaseStatsValueWritable implements Writable {
    /**
     * 获取kpi  获取一个kpi的抽象方法
     * @return
     */
    public abstract KpiType getKpi();  //获取kpi的类型
}
