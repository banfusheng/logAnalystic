/**
 * 〈一句话功能简述〉 <br>
 *
 * @author Administrator
 * @create 2018/8/15
 * @since 1.0.0
 */
package com.qf.mytest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class tobaoIpParseUtil {

    private static final Logger logger = Logger.getLogger(tobaoIpParseUtil.class);
    private final static String urlStr = "http://ip.taobao.com/service/getIpInfo.php?ip=";
    RegionInfo info2 = new RegionInfo();

    /**
     * 根据ip信息进行封装
     */
    public RegionInfo getInfo(String ip) {

        String strResult = getUrlStrResult(urlStr, ip);
        if (strResult != null) {
            System.out.println("1   " + strResult);
            JSONObject jsonObject = JSON.parseObject(strResult);
            if (jsonObject.getString("code").equals("0")) {
                String data = jsonObject.getString("data");
                JSONObject dataJsonObject = JSONObject.parseObject(data);
                info2.setCountry(dataJsonObject.getString("country"));
                info2.setProvince(dataJsonObject.getString("region"));
                info2.setCity(dataJsonObject.getString("city"));
            }
        }

        return info2;
    }


    /**
     * @param urlStr 请求的地址
     * @param ip     请求的参数ip
     * @return 返回淘宝网上的json string模式
     */
    public String getUrlStrResult(String urlStr, String ip) {
        URL url = null;
        HttpURLConnection conn = null;
        try {
            //得先判断ip的情况
            url = new URL(urlStr + ip);
            //新建链接的实例
            conn = (HttpURLConnection) url.openConnection();
            //设置链接超时时间
            conn.setConnectTimeout(5000);
            //设置读取数据的时间
            conn.setReadTimeout(10000);
            //设置是否打开输出流
            // conn.setDoOutput(true);
            //设置是否打开输入流
            conn.setDoInput(true);
            //设置提交的方法
            conn.setRequestMethod("POST");
            //是否缓存
            conn.setUseCaches(false);
            //打开链接的端口
            conn.connect();
          /*  //打开输出流对服务器写数据
            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            //写入数据
            out.writeBytes(ip);
            out.flush();
            out.close();*/
            //往对端写完数据对服务器返回数据  以bufferreader流俩读取
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            conn.getInputStream(), "utf-8"));
            StringBuffer buf = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                buf.append(line);
            }
            br.close();
            return buf.toString();


        } catch (Exception e) {
            logger.warn("读取ip的json数据失败");
        } finally {
            if (conn != null) {
                conn.disconnect();//关闭链接
            }
        }


        return null;
    }


    /**
     * 对地域信息的内部类
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

    /**
     * unicode 转换成 中文
     *
     * @param theString
     * @return
     */
   /* private static String decodeUnicode(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len; ) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed      encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't') {
                        aChar = '\t';
                    } else if (aChar == 'r') {
                        aChar = '\r';
                    } else if (aChar == 'n') {
                        aChar = '\n';
                    } else if (aChar == 'f') {
                        aChar = '\f';
                    }
                    outBuffer.append(aChar);
                }
            } else {
                outBuffer.append(aChar);
            }
        }
        return outBuffer.toString();
    }*/


}
