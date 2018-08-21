/**
 * ip解析工具
 *
 * @author Administrator
 * @create 2018/8/15
 * @since 1.0.0
 */
package com.qf.etl.util;

import com.alibaba.fastjson.JSONObject;
import com.qf.etl.util.ip.IPSeeker;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


public class IPParseUtil extends IPSeeker {

    private static final Logger logger = Logger.getLogger(IPParseUtil.class);
    private final static String urlStr = "http://ip.taobao.com/service/getIpInfo.php?ip=";

    RegionInfo info = new RegionInfo();

    /**
     * 用于解析ip
     *
     * @param ip
     * @return
     */
    public RegionInfo parseIp(String ip) {
        if (StringUtils.isEmpty(ip)) {
            logger.warn("解析的Ip为空");
            return info;
        }

        try {
            //通过ipSeekeer来获取ip所对应的信息  贵州省铜仁地区| 局域网
            //通过ipseeker来获取ip所对应的信息
            String country = IPSeeker.getInstance().getCountry(ip);
            if (country.equals("局域网")) {
                info.setCountry("中国");
                info.setProvince("北京");
                info.setCity("昌平区");
            } else if (country != null && !country.trim().isEmpty()) {
                //查找返回的字符串中是否有省
                info.setCountry("中国");
                int index = country.indexOf("省");

                if (index > 0) {
                    //证明有省
                    info.setProvince(country.substring(0, index + 1));
                    int index2 = country.indexOf("市");
                    if (index2 > 0) {
                        info.setCity(country.substring(index + 1, index2 + 1));
                    }
                } else {
                    String flag = country.substring(0, 2);
                    String country1 = null;
                    switch (flag) {
                        case "内蒙":
                            info.setProvince(flag + "古");
                            country1 = country.substring(3);
                            index = country1.indexOf("市");
                            if (index > 0) {
                                info.setCity(country1.substring(0, index + 1));
                            }
                            break;
                        case "广西":
                        case "宁夏":
                        case "新疆":
                        case "西藏":
                            info.setProvince(flag);
                            country1 = country.substring(2);
                            index = country1.indexOf("市");
                            if (index > 0) {
                                info.setCity(country1.substring(0, index + 1));
                            }
                            break;
                        case "北京":
                        case "上海":
                        case "重庆":
                        case "天津":
                            info.setProvince(flag + "市");
                            country1 = country.substring(3);
                            index = country1.indexOf("区");
                            if (index > 0) {
                                char ch = country1.charAt(index - 1);
                                if (ch != '小' || ch != '校' || ch != '军') {
                                    info.setCity(country1.substring(0, index + 1));
                                }
                            }
                            index = country.indexOf("县");
                            if (index > 0) {
                                info.setCity(country1.substring(0, index + 1));
                            }
                            break;
                        case "台湾":
                        case "澳门":
                        case "香港":
                            info.setProvince(flag + "特别行政区");
                            break;
                        default:
                            break;
                    }
                }
            }
        } catch (Exception e) {

            logger.warn("解析ip数据异常");
        }

        return info;
    }

    /**
     * 使用淘宝的ip解析库解析ip
     *
     * @param ip
     * @param charset
     * @return
     * @throws Exception
     */
    public RegionInfo parserIp1(String ip, String charset) {
        HttpClient httpClient = new HttpClient();
        GetMethod method = new GetMethod(urlStr + ip);
        //PostMethod postMethod = new PostMethod(url);

        try {
            if (StringUtils.isBlank(ip)) {
                throw new Exception("ip为空");
            }


            //设置请求的编码'
            if (null != charset) {
                method.addRequestHeader(
                        "Content-Type",
                        "application/x-www-form-urlencoded;charset=" + charset);
            } else {
                method.addRequestHeader(
                        "Content-Type",
                        "application/x-www-form-urlencoded; charset=" + "utf-8");
            }
            int statusCode = httpClient.executeMethod(method);
            if (statusCode != HttpStatus.SC_OK) {
                System.out.println("Method failed: " + method.getStatusLine());
            }
            //返回响应信息
            byte[] responseBody =
                    method.getResponseBodyAsString()
                            .getBytes(method.getResponseCharSet());
            // 在返回响应消息使用编码(utf-8或gb2312)
            String response = new String(responseBody, "utf-8");

            //将reponse的字符串转换成json对象
            JSONObject jo = JSONObject.parseObject(response);
            JSONObject jo1 = JSONObject.parseObject(jo.getString("data"));

            //赋值
            info.setCountry(jo1.getString("country"));
            info.setProvince(jo1.getString("region"));
            info.setCity(jo1.getString("city"));

            return info;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }


    /**
     * 用于封装ip解析出来的国家省份市 信息
     */
    public static class RegionInfo {
        private static final String DEFAULT_VALUE = "null";
        private String country = DEFAULT_VALUE;
        private String province = DEFAULT_VALUE;
        private String city = DEFAULT_VALUE;

        public RegionInfo() {
        }

        public RegionInfo(String country, String province, String city) {
            this.country = country;
            this.province = province;
            this.city = city;
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

        @Override
        public String toString() {
            return "RegionInfo{" +
                    "country='" + country + '\'' +
                    ", province='" + province + '\'' +
                    ", city='" + city + '\'' +
                    '}';
        }
    }


}
