/**
 * 浏览器维度
 *
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

public class BrowserDimension extends BaseDimension {
    private int id;
    private String browserName;
    private String browserVersion;

    public BrowserDimension() {
    }

    public BrowserDimension(String browserName, String browserVersion) {
        this.browserName = browserName;
        this.browserVersion = browserVersion;
    }

    public BrowserDimension(int id, String browserName, String browserVersion) {
        this.id = id;
        this.browserName = browserName;
        this.browserVersion = browserVersion;
    }

    /**
     * 获取当前对象的一个静态方法
     *
     * @param browserName
     * @param browserVersion
     * @return
     */
    public static BrowserDimension getInstance(String browserName, String browserVersion) {
        BrowserDimension browserDimension = new BrowserDimension();
        browserDimension.browserName = browserName;
        browserDimension.browserVersion = browserVersion;
        return browserDimension;
    }

    /**
     * 构建维度集合对象
     *
     * @param browserName
     * @param browserVersion
     * @return
     */
    public static List<BrowserDimension> buildList(
            String browserName, String browserVersion) {
        List<BrowserDimension> li = new ArrayList<>();
        if (StringUtils.isBlank(browserName)) {
            browserName = GlobalConstant.DEFAULT_VALUE;
            browserVersion = GlobalConstant.DEFAULT_VALUE;

        }
        if (StringUtils.isBlank(browserVersion)) {
            browserVersion = GlobalConstant.DEFAULT_VALUE;
        }

        li.add(BrowserDimension.getInstance(browserName, browserVersion));
        li.add(BrowserDimension.getInstance(browserName, GlobalConstant.ALL_OF_VALUE));
        return li;

    }


    /**
     * 采用升序
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(BaseDimension o) {
        if (o == this) {
            return 0;
        }
        BrowserDimension other = (BrowserDimension) o;
        int tmp = this.id - other.id;
        if (tmp != 0) {
            return tmp;
        }
        tmp = this.browserName.compareTo(other.browserName);
        if (tmp != 0) {
            return tmp;
        }
        return this.browserVersion.compareTo(other.browserVersion);
    }

    //序列化，将对象的属性值转换成序列化的值
    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(this.id);
        out.writeUTF(this.browserName);
        out.writeUTF(this.browserVersion);

    }

    //反序列化，将序列化后的值转换成普通数据类型并存入属性
    @Override
    public void readFields(DataInput in) throws IOException {
        this.id = in.readInt();
        this.browserName = in.readUTF();
        this.browserVersion = in.readUTF();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BrowserDimension that = (BrowserDimension) o;
        return id == that.id &&
                Objects.equals(browserName, that.browserName) &&
                Objects.equals(browserVersion, that.browserVersion);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (browserName != null ? browserName.hashCode() : 0);
        result = 31 * result + (browserVersion != null ? browserVersion.hashCode() : 0);
        return result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBrowserName() {
        return browserName;
    }

    public void setBrowserName(String browserName) {
        this.browserName = browserName;
    }

    public String getBrowserVersion() {
        return browserVersion;
    }

    public void setBrowserVersion(String browserVersion) {
        this.browserVersion = browserVersion;
    }

    @Override
    public String toString() {
        return "BrowserDimension{" +
                "id=" + id +
                ", browserName='" + browserName + '\'' +
                ", browserVersion='" + browserVersion + '\'' +
                '}';
    }
}
