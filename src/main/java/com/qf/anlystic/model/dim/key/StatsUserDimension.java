/**
 * 用于用户模块 浏览器没看map输出的key的类型
 *
 * @author Administrator
 * @create 2018/8/20
 * @since 1.0.0
 */
package com.qf.anlystic.model.dim.key;

import com.qf.anlystic.model.dim.base.BaseDimension;
import com.qf.anlystic.model.dim.base.BrowserDimension;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

public class StatsUserDimension extends BashStatsDimension{
    private StatsCommonDimension statsCommonDimension = new StatsCommonDimension();
    private BrowserDimension browserDimension = new BrowserDimension();

    /**
     * 克隆一个当前对象
     * @param dimension
     * @return
     */
    public static StatsUserDimension clone(StatsUserDimension dimension){
        BrowserDimension browserDimension = new BrowserDimension(dimension.browserDimension.getId(),
                dimension.browserDimension.getBrowserName(),
                dimension.browserDimension.getBrowserVersion());
        StatsCommonDimension statsCommonDimension = StatsCommonDimension.clone(
                dimension.statsCommonDimension
        );
        return new StatsUserDimension(statsCommonDimension,browserDimension);
    }


    @Override
    public int compareTo(BaseDimension o) {
        if (this == o){
            return 0;
        }
        StatsUserDimension other = (StatsUserDimension) o;
        int tmp = this.browserDimension.compareTo(other.browserDimension);
        if (tmp !=0 ){
            return tmp;
        }
        return this.statsCommonDimension.compareTo(other.statsCommonDimension);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        this.browserDimension.write(out);
        this.statsCommonDimension.write(out);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.browserDimension.readFields(in);
        this.statsCommonDimension.readFields(in);
    }

    public StatsUserDimension() {
    }

    public StatsUserDimension(StatsCommonDimension statsCommonDimension,
                              BrowserDimension browserDimension) {
        this.statsCommonDimension = statsCommonDimension;
        this.browserDimension = browserDimension;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StatsUserDimension)) return false;
        StatsUserDimension that = (StatsUserDimension) o;
        return Objects.equals(getStatsCommonDimension(), that.getStatsCommonDimension()) &&
                Objects.equals(getBrowserDimension(), that.getBrowserDimension());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getStatsCommonDimension(), getBrowserDimension());
    }

    public StatsCommonDimension getStatsCommonDimension() {
        return statsCommonDimension;
    }

    public void setStatsCommonDimension(StatsCommonDimension statsCommonDimension) {
        this.statsCommonDimension = statsCommonDimension;
    }

    public BrowserDimension getBrowserDimension() {
        return browserDimension;
    }

    public void setBrowserDimension(BrowserDimension browserDimension) {
        this.browserDimension = browserDimension;
    }
}
