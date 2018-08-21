/**
 * map阶段和reduce阶段的key公共维度类型的封装
 *
 * @author Administrator
 * @create 2018/8/20
 * @since 1.0.0
 */
package com.qf.anlystic.model.dim.key;

import com.qf.anlystic.model.dim.base.BaseDimension;
import com.qf.anlystic.model.dim.base.DateDimension;
import com.qf.anlystic.model.dim.base.KpiDimension;
import com.qf.anlystic.model.dim.base.PlatFormDimension;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

public class StatsCommonDimension extends BashStatsDimension {
    private DateDimension dateDimension = new DateDimension();
    private PlatFormDimension platFormDimension = new PlatFormDimension();
    private KpiDimension kpiDimension = new KpiDimension();

    /**
     * 根据当前对象克隆一个对象
     *
     * @param dimension
     * @return
     */
    public static StatsCommonDimension clone(StatsCommonDimension dimension) {
        PlatFormDimension platFormDimension = new PlatFormDimension(dimension.platFormDimension.getId(),
                dimension.platFormDimension.getPlatformName());
        KpiDimension kpiDimension = new KpiDimension(dimension.kpiDimension.getId(),
                dimension.kpiDimension.getKpiName());
        DateDimension dateDimension = new DateDimension(dimension.dateDimension.getId(),
                dimension.dateDimension.getYear(),
                dimension.dateDimension.getSeason(),
                dimension.dateDimension.getMonth(),
                dimension.dateDimension.getWeek(),
                dimension.dateDimension.getDay(),
                dimension.dateDimension.getDate(),
                dimension.dateDimension.getType());
        return new StatsCommonDimension(dateDimension,
                platFormDimension, kpiDimension);

    }

    @Override
    public int compareTo(BaseDimension o) {
        if(this == o){
            return  0;
        }
        StatsCommonDimension other = (StatsCommonDimension) o;
        int tmp = this.dateDimension.compareTo(other.dateDimension);
        if(tmp != 0){
            return tmp;
        }
        tmp = this.platFormDimension.compareTo(other.platFormDimension);
        if(tmp != 0){
            return tmp;
        }
        tmp = this.kpiDimension.compareTo(other.kpiDimension);
        return tmp;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        //持久化对象
        this.dateDimension.write(out);
        this.platFormDimension.write(out);
        this.kpiDimension.write(out);

    }

    @Override
    public void readFields(DataInput in) throws IOException {
        //读对象
        this.dateDimension.readFields(in);
        this.platFormDimension.readFields(in);
        this.kpiDimension.readFields(in);
    }

    public StatsCommonDimension() {
    }

    public StatsCommonDimension(DateDimension dateDimension,
                                PlatFormDimension platFormDimension,
                                KpiDimension kpiDimension) {
        this.dateDimension = dateDimension;
        this.platFormDimension = platFormDimension;
        this.kpiDimension = kpiDimension;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StatsCommonDimension)) return false;
        StatsCommonDimension that = (StatsCommonDimension) o;
        return Objects.equals(getDateDimension(), that.getDateDimension()) &&
                Objects.equals(getPlatFormDimension(), that.getPlatFormDimension()) &&
                Objects.equals(getKpiDimension(), that.getKpiDimension());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getDateDimension(),
                getPlatFormDimension(), getKpiDimension());
    }

    public DateDimension getDateDimension() {
        return dateDimension;
    }

    public void setDateDimension(DateDimension dateDimension) {
        this.dateDimension = dateDimension;
    }

    public PlatFormDimension getPlatFormDimension() {
        return platFormDimension;
    }

    public void setPlatFormDimension(PlatFormDimension platFormDimension) {
        this.platFormDimension = platFormDimension;
    }

    public KpiDimension getKpiDimension() {
        return kpiDimension;
    }

    public void setKpiDimension(KpiDimension kpiDimension) {
        this.kpiDimension = kpiDimension;
    }
}
