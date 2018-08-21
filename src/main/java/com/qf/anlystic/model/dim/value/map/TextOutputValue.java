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

public class TextOutputValue extends BaseStatsValueWritable {
    private String uuid;
    private String item; // sessionId,event

    @Override
    public KpiType getKpi() {
        return null;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(this.uuid);
        out.writeUTF(this.item);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.uuid = in.readUTF();
        this.item = in.readUTF();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String itemId) {
        this.item = itemId;
    }
}
