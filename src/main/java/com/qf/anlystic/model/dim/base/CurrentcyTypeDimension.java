/**
 *
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

public class CurrentcyTypeDimension  extends BaseDimension{
    private  int id;
    private String currencyName;


    @Override
    public int compareTo(BaseDimension o) {
        if (this == o){
            return 0;
        }
        CurrentcyTypeDimension other = (CurrentcyTypeDimension) o;

        int tmp = this.id -other.id;
        if (tmp != 0){
            return tmp;
        }
        return  this.currencyName.compareTo(other.currencyName);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(this.id);
        out.writeUTF(this.currencyName);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.id =in.readInt();
        this.currencyName = in.readUTF();
    }

    public CurrentcyTypeDimension() {
    }

    public CurrentcyTypeDimension(String currencyName) {
        this.currencyName = currencyName;
    }

    public CurrentcyTypeDimension(int id, String currencyName) {
        this.id = id;
        this.currencyName = currencyName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CurrentcyTypeDimension)) return false;
        CurrentcyTypeDimension that = (CurrentcyTypeDimension) o;
        return getId() == that.getId() &&
                Objects.equals(getCurrencyName(), that.getCurrencyName());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getCurrencyName());
    }
}
