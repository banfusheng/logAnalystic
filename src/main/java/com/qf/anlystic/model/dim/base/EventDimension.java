/**事件维度
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

public class EventDimension extends BaseDimension {
    private  int id;
    private String category;
    private String action;
    @Override
    public int compareTo(BaseDimension o) {
        if (this == o){
            return  0;
        }
        EventDimension other = (EventDimension) o;
        int tmp = this.id - other.id;
        if(tmp != 0){
            return tmp;
        }
        tmp = this.category.compareTo(other.category);
        if(tmp != 0){
            return tmp;
        }
        tmp = this.action.compareTo(other.action);
        return tmp;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(this.id);
        out.writeUTF(this.category);
        out.writeUTF(this.action);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.id = in.readInt();
        this.category = in.readUTF();
        this.action = in.readUTF();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventDimension)) return false;
        EventDimension that = (EventDimension) o;
        return getId() == that.getId() &&
                Objects.equals(getCategory(), that.getCategory()) &&
                Objects.equals(getAction(), that.getAction());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getCategory(), getAction());
    }

    public EventDimension() {
    }

    public EventDimension(String category, String action) {
        this.category = category;
        this.action = action;
    }

    public EventDimension(int id, String category, String action) {
        this.id = id;
        this.category = category;
        this.action = action;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
