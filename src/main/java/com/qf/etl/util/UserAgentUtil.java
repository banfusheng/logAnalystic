/**
 * 解析useragent代理对象
 *
 * @author Administrator
 * @create 2018/8/15
 * @since 1.0.0
 */
package com.qf.etl.util;

import cz.mallat.uasparser.OnlineUpdater;
import cz.mallat.uasparser.UASparser;
import org.apache.directory.api.util.Strings;
import org.apache.log4j.Logger;

import java.io.IOException;

public class UserAgentUtil {

    private static final Logger logger = Logger.getLogger(UserAgentUtil.class);

    private static UASparser uaSparser =null;
    static {
        try {
            uaSparser = new UASparser(OnlineUpdater.
                    getVendoredInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static  UserAgentInfo parseUserAgent(String agent){
        UserAgentInfo uainfo = new UserAgentInfo();
        if (Strings.isEmpty(agent)){
            logger.warn("agent is null.but we need not null.");
            return null;
        }
        //正常解析
        try {
            cz.mallat.uasparser.UserAgentInfo info = uaSparser.parse(agent);
            //设置属性
            uainfo.setBrowserName(info.getUaFamily());
            uainfo.setBrowserVersion(info.getBrowserVersionInfo());
            uainfo.setOsName(info.getOsFamily());
            uainfo.setOsVersion(info.getOsName());


        } catch (Exception e) {
            logger.warn("useragent parse 异常.",e);
        }
        return uainfo;
    }

    /**
     * 用于封装解析出来的字段，浏览器名、版本、操作系统名、版本
     */
    public  static  class  UserAgentInfo{
        private String browserName;
        private String browserVersion;
        private String osName;
        private String osVersion;

        public UserAgentInfo() {
        }

        public UserAgentInfo(String browserName,
                             String browserVersion, String osName, String osVersion) {
            this.browserName = browserName;
            this.browserVersion = browserVersion;
            this.osName = osName;
            this.osVersion = osVersion;
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

        public String getOsName() {
            return osName;
        }

        public void setOsName(String osName) {
            this.osName = osName;
        }

        public String getOsVersion() {
            return osVersion;
        }

        public void setOsVersion(String osVersion) {
            this.osVersion = osVersion;
        }

        @Override
        public String toString() {
            return "UserAgentInfo{" +
                    "browserName='" + browserName + '\'' +
                    ", browserVersion='" + browserVersion + '\'' +
                    ", osName='" + osName + '\'' +
                    ", osVersion='" + osVersion + '\'' +
                    '}';
        }
    }
}
