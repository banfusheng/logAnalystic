/**
 * 〈一句话功能简述〉 <br>
 *
 * @author Administrator
 * @create 2018/8/20
 * @since 1.0.0
 */
package com.qf.anlystic.model.dim.value.map;

import com.qf.anlystic.model.dim.value.BaseStatsValueWritable;
import com.qf.common.KpiType;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class TimeOutputValue extends BaseStatsValueWritable {
    //泛指id:uuid memnerId  sessionId  ...
    private String id;
    private long time;
    @Override
    public KpiType getKpi() {
        return null;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(this.id);
        out.writeLong(this.time);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.id = in.readUTF();
        this.time = in.readLong();
    }

    public TimeOutputValue() {
    }

    public TimeOutputValue(String id, long time) {
        this.id = id;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
