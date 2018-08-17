/**
 * 整行日志的解析工具
 *
 * @author Administrator
 * @create 2018/8/17
 * @since 1.0.0
 */
package com.qf.etl.util;

import com.qf.common.EventLogConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LogUtil {

    private static final Logger logger = Logger.getLogger(LogUtil.class);

    /**
     * 单行日志解析
     *
     * @param logText
     * @return ConcurrentHashMap
     */
    public static Map<String, String> hangleLog(String logText) {
        //线程更安全
        Map<String, String> info = new ConcurrentHashMap<String, String>();
        //判断logText是否为空
        if (StringUtils.isNotEmpty(logText.trim())) {
            //拆分单行日志
            String[] fields = logText.split(EventLogConstants.LOG_SEPARTOR);
            if (fields.length == 4) {
                //将字段存储到info中
                /*
                192.168.216.1^A1256798789.123^A192.168.216.111^A/1.png?
                en=e_l&ver=1&u_ud=679f-dfsa-u789-dfaa
                 */
                //来源ip
                info.put(EventLogConstants.LOG_COLUMN_NAME_IP, fields[0]);
                //时间戳解析
                info.put(EventLogConstants.LOG_COLUMN_NAME_SERVER_TIME,
                        fields[1].replace(".", ""));

                //将参数列表中的k-v解析存储到info
                //处理参数
                handleParams(fields[3], info);
                //解析ip
                handleIp(info);
                //机械userAgent
                handleUserAgent(info);
            }
        }

        return info;
    }

    /**
     * 解析ip
     *
     * @param info
     */
    private static void handleIp(Map<String, String> info) {
        String ip = info.get(EventLogConstants.LOG_COLUMN_NAME_IP);
        if (StringUtils.isNotEmpty(ip)) {
            IPParseUtil.RegionInfo regionInfo = new IPParseUtil().parseIp(ip);
            if (regionInfo != null) {
                info.put(EventLogConstants.LOG_COLUMN_NAME_COUNTRY, regionInfo.getCountry());
                info.put(EventLogConstants.LOG_COLUMN_NAME_PROVINCE, regionInfo.getProvince());
                info.put(EventLogConstants.LOG_COLUMN_NAME_CITY, regionInfo.getCity());
            }

        }
    }

    private static void handleParams(String field, Map<String, String> info) {
        //isNotEmpty将空格也作为参数，isNotBlank则排除空格参数
        if (StringUtils.isNotBlank(field) && !info.isEmpty()) {
            int index = field.indexOf("?");
            if (index > 0) {
                String fields = field.substring(index + 1);
                String[] params = fields.split("&");
                for (String param : params) {
                    int index1 = param.indexOf("=");
                    if (index1 > 0) {
                        //拆分param
                        String[] kvs = param.split("=");
                        String k = kvs[0];
                        try {
                            String v = URLDecoder.decode(kvs[1], "utf-8");
                            if (StringUtils.isNotBlank(k)) {
                                info.put(k, v);
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }

    }


    /**
     * 解析userAgent
     *
     * @param info
     */
    private static void handleUserAgent(Map<String, String> info) {
        //获取userAgent的字段
        String userAgent = info.get(EventLogConstants.LOG_COLUMN_NAME_USERAGENT);

        if (StringUtils.isNotEmpty(userAgent)) {
            UserAgentUtil.UserAgentInfo userAgentInfo =
                    UserAgentUtil.parseUserAgent(userAgent);
            if (userAgentInfo != null) {
                info.put(EventLogConstants.LOG_COLUMN_NAME_BROWSER_NAME, userAgentInfo.getBrowserName());
                info.put(EventLogConstants.LOG_COLUMN_NAME_BROWSER_VERSION, userAgentInfo.getBrowserVersion());
                info.put(EventLogConstants.LOG_COLUMN_NAME_OS_NAME, userAgentInfo.getOsName());
                info.put(EventLogConstants.LOG_COLUMN_NAME_OS_VERSION, userAgentInfo.getOsVersion());
            }


        }


    }
}
