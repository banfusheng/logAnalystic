/**
 * 〈一句话功能简述〉 <br>
 *
 * @author Administrator
 * @create 2018/8/18
 * @since 1.0.0
 */
package com.qf.anlystic.model.dim.base;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

public class LocationDimension extends BaseDimension {
    private int id;
    private String country;
    private String province;
    private String city;
    public static LocationDimension newInstance(String country, String province, String city){
        LocationDimension ld = new LocationDimension();
        ld.country = country;
        ld.province = province;
        ld.city = city;
        return ld;
    }
    @Override
    public int compareTo(BaseDimension o) {
        return 0;
    }

    @Override
    public void write(DataOutput out) throws IOException {

    }

    @Override
    public void readFields(DataInput in) throws IOException {

    }

    public LocationDimension() {
    }

    public LocationDimension(String country, String province, String city) {
        this.country = country;
        this.province = province;
        this.city = city;
    }

    public LocationDimension(int id, String country,
                             String province, String city) {
        this.id = id;
        this.country = country;
        this.province = province;
        this.city = city;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LocationDimension)) return false;
        LocationDimension that = (LocationDimension) o;
        return getId() == that.getId() &&
                Objects.equals(getCountry(), that.getCountry()) &&
                Objects.equals(getProvince(), that.getProvince()) &&
                Objects.equals(getCity(), that.getCity());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getCountry(), getProvince(), getCity());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
