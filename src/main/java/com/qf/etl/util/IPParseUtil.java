/**
 *
 *ip解析工具
 * @author Administrator
 * @create 2018/8/15
 * @since 1.0.0
 */
package com.qf.etl.util;

import com.qf.etl.util.ip.IPSeeker;


import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;


public class IPParseUtil extends IPSeeker {

    private  static  final Logger logger = Logger.getLogger(IPParseUtil.class);

    RegionInfo info = new RegionInfo();
    /**
     * 用于解析ip
     * @param ip
     * @return
     */
    public  RegionInfo parseIp(String ip){
        if (StringUtils.isEmpty(ip)){
            logger.warn("解析的Ip为空");
            return info;
        }

        //通过ipseeker来获取ip所对应的信息
        String country = IPSeeker.getInstance().getCountry(ip);
        try {
            if (country.equals("局域网")){
                info.setCountry("中国");
                info.setProvince("北京");
                info.setCity("昌平区");
            }else if(country != null && !country.trim().isEmpty()){
                //查找返回的字符串中是否有省
                info.setCountry("中国");
                int index = country.indexOf("省");

                if (index > 0){
                    //证明有省
                    info.setProvince(country.substring(0,index+1));
                    int index2 = country.indexOf("市");
                    if (index2 > 0){
                        info.setCity(country.substring(index+1,index2+1));
                    }
                }else {
                    String flag = country.substring(0,2);
                    switch (flag){
                        case "内蒙":
                            info.setProvince(flag+"古");
                            String country1 = country.substring(3);
                             index = country1.indexOf("市");
                            if (index > 0){
                            info.setCity(country1.substring(0,index +1));
                            }
                            break;

                        case "西藏":
                            info.setProvince(flag);
                            country1 = country.substring(2);
                            index = country1.indexOf("市");
                            if (index > 0){
                                info.setCity(country1.substring(0,index +1));
                            }
                            break;
                        case "北京":
                            break;
                        case "上海":break;
                        case "重庆":break;
                        case "天津":break;
                        case "台湾":break;
                        case "澳门":break;
                        case "香港":break;
                    }
                }
            }
        } catch (Exception e) {

            logger.warn("解析ip数据异常");
        }

        return info;
    }


    /**
     * 用于封装ip解析出来的国家省份市 信息
     */
    private static class  RegionInfo{
        private static  final  String DEFAULT_VALUE = null;
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