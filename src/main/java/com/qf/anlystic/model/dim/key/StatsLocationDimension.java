/**
 * 用于map阶段的输出key
 *
 * @author Administrator
 * @create 2018/8/20
 * @since 1.0.0
 */
package com.qf.anlystic.model.dim.key;

import com.qf.anlystic.model.dim.base.BaseDimension;
import com.qf.anlystic.model.dim.base.LocationDimension;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

public class StatsLocationDimension extends BashStatsDimension{
    private StatsCommonDimension statsCommonDimension = new StatsCommonDimension();
    private LocationDimension locationDimension = new LocationDimension();

    /**
     * 克隆一个当前对象
     * @param dimension
     * @return
     */
    public static StatsLocationDimension clone(StatsLocationDimension dimension){
        LocationDimension locationDimension = LocationDimension.newInstance(
                dimension.locationDimension.getCountry(),
                dimension.locationDimension.getProvince(),
                dimension.locationDimension.getCity()
        );
        StatsCommonDimension statsCommonDimension = StatsCommonDimension.clone(dimension.statsCommonDimension);
        return new StatsLocationDimension(statsCommonDimension,locationDimension);
    }


    @Override
    public int compareTo(BaseDimension o) {
        if(this == o){
            return  0;
        }
        StatsLocationDimension other = (StatsLocationDimension) o;
        int tmp = this.statsCommonDimension.compareTo(other.statsCommonDimension);
        if(tmp != 0){
            return tmp;
        }
        tmp = this.locationDimension.compareTo(other.locationDimension);
        return tmp;

    }

    @Override
    public void write(DataOutput out) throws IOException {
        this.locationDimension.write(out);
        this.statsCommonDimension.write(out);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.locationDimension.readFields(in);
        this.statsCommonDimension.readFields(in);
    }

    public StatsLocationDimension() {
    }

    public StatsLocationDimension(StatsCommonDimension statsCommonDimension,
                                  LocationDimension locationDimension) {
        this.statsCommonDimension = statsCommonDimension;
        this.locationDimension = locationDimension;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StatsLocationDimension)) return false;
        StatsLocationDimension that = (StatsLocationDimension) o;
        return Objects.equals(getStatsCommonDimension(), that.getStatsCommonDimension()) &&
                Objects.equals(getLocationDimension(), that.getLocationDimension());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getStatsCommonDimension(), getLocationDimension());
    }

    public StatsCommonDimension getStatsCommonDimension() {
        return statsCommonDimension;
    }

    public void setStatsCommonDimension(StatsCommonDimension statsCommonDimension) {
        this.statsCommonDimension = statsCommonDimension;
    }

    public LocationDimension getLocationDimension() {
        return locationDimension;
    }

    public void setLocationDimension(LocationDimension locationDimension) {
        this.locationDimension = locationDimension;
    }
}
