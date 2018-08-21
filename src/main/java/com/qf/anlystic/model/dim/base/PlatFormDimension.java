/**
 * @author Administrator
 * @create 2018/8/18
 * @since 1.0.0
 */
package com.qf.anlystic.model.dim.base;

import com.qf.common.GlobalConstant;
import org.apache.commons.lang.StringUtils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlatFormDimension extends BaseDimension {
    private int id;
    private String platformName;

    public PlatFormDimension() {
    }

    public PlatFormDimension(String platformName) {
        this.platformName = platformName;
    }

    public PlatFormDimension(int id, String platformName) {
        this.id = id;
        this.platformName = platformName;
    }
    public static List<PlatFormDimension> buildList(String platformName){
        if(StringUtils.isEmpty(platformName)){
            platformName = GlobalConstant.DEFAULT_VALUE;
        }
        List<PlatFormDimension> li = new ArrayList<PlatFormDimension>();
        li.add(new PlatFormDimension(platformName));
        li.add(new PlatFormDimension(GlobalConstant.ALL_OF_VALUE));
        return li;

    }
    @Override
    public int compareTo(BaseDimension o) {
        if(this == o){
            return  0;
        }
        PlatFormDimension other = (PlatFormDimension) o;
        int tmp = this.id - other.id;
        if(tmp != 0){
            return tmp;
        }
        tmp = this.platformName.compareTo(other.platformName);
        return tmp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlatFormDimension)) return false;
        PlatFormDimension that = (PlatFormDimension) o;
        return getId() == that.getId() &&
                Objects.equals(getPlatformName(), that.getPlatformName());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getPlatformName());
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(this.id);
        out.writeUTF(this.platformName);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.id= in.readInt();
        this.platformName = in.readUTF();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }
}
