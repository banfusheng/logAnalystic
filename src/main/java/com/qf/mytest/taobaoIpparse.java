/**
 * 〈一句话功能简述〉 <br>
 *
 * @author Administrator
 * @create 2018/8/16
 * @since 1.0.0
 */
package com.qf.mytest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import java.io.IOException;

public class taobaoIpparse {

    private static HttpClient client = null;
    private static PostMethod method = null;

    private static final Logger logger = Logger.getLogger(taobaoIpparse.class);
    private final static String urlStr = "http://ip.taobao.com/service/getIpInfo.php?ip=";
    private static RegionInfo info3 = new RegionInfo();
    public String parseInfo(String ip){
        if (ip ==null || ip.trim() == ""){
            return  null;
        }
        String strResult = getJson(urlStr, ip);
        if (strResult != null) {
            System.out.println("1   " + strResult);
            JSONObject jsonObject = JSON.parseObject(strResult);
            if (jsonObject.getString("code").equals("0")) {
                String data = jsonObject.getString("data");
                JSONObject dataJsonObject = JSONObject.parseObject(data);
                info3.setCountry(dataJsonObject.getString("country"));
                info3.setProvince(dataJsonObject.getString("region"));
                info3.setCity(dataJsonObject.getString("city"));
            }
        }

        return info3.toString();
    }

    //先获取json字符串
    public String getJson(String urlStr, String ip) {
        client = new HttpClient();

        method = new PostMethod(urlStr+ip);
        //method = new PostMethod("http://ip.taobao.com/service/getIpInfo.php?ip=192.168.20.11");
        try {
            client.executeMethod(method);
            String response = method.getResponseBodyAsString();
            return response;
        } catch (IOException e) {
            logger.warn("获取json数据失败");
        }
        return null;

    }


    /**
     * 用于封装ip解析出来的国家省份市 信息
     */
    private static class RegionInfo {
        private static final String DEFAULT_VALUE = null;
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
