/**
 * 〈一句话功能简述〉 <br>
 *
 * @author Administrator
 * @create 2018/8/20
 * @since 1.0.0
 */
package com.qf.anlystic.model.dim.value.reduce;

import com.qf.anlystic.model.dim.value.BaseStatsValueWritable;
import com.qf.common.KpiType;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.WritableUtils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class MapWritableValue extends BaseStatsValueWritable {
    private MapWritable value = new MapWritable();
    private  KpiType kpi;
    public MapWritableValue(){

    }
    public MapWritableValue(MapWritable value, KpiType kpi) {
        this.value = value;
        this.kpi = kpi;
    }


    public MapWritable getValue() {
        return value;
    }

    public void setValue(MapWritable value) {
        this.value = value;
    }

    public void setKpi(KpiType kpi) {
        this.kpi = kpi;
    }

    @Override
    public KpiType getKpi() {
        return this.kpi;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        this.value.write(out); //自带mapwritable类型
        WritableUtils.writeEnum(out,kpi); //持久化枚举类型
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.value.readFields(in);
        WritableUtils.readEnum(in,KpiType.class);
    }
}
