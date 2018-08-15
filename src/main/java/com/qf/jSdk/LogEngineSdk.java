/**
 * 〈一句话功能简述〉 <br>
 *
 * @author Administrator
 * @create 2018/8/15
 * @since 1.0.0
 */
package com.qf.jSdk;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 后台收集日志的sdk
 */
public class LogEngineSdk {
    //获取日志的打印对象
    private final static Logger logger = Logger.getGlobal();

    private final static String access_url = "http://192.168.216.51/index.html";

    private final static String ver = "1.0";
    private final static String platforName = "java_server";
    private final static String sdkName = "java_sdk";

    /**
     * 支付成功事件
     *
     * @param oid
     * @param mid
     * @param flag cs :支付成  cr  退款成功
     * @return支付成功返回true
     */
    public static boolean chargSuccess(String oid, String mid, String flag) {

        try {
            if (isEmpty(oid) || isEmpty(mid)) {
                logger.log(Level.WARNING,
                        "oid &mid may be null  oid" + oid + "  mid" + mid);
            }
            //正常支付  构建url :http : //192.168.216.51/index.html
            //?en=p_cs&ver=1.0&pl=
            HashMap<String, String> info = new HashMap<>();
            //添加参数
            info.put("u_mid", mid);
            info.put("oid", oid);
            info.put("ver", ver);
            if (flag.equals("cr")){
                info.put("en", "e_cr");
            }else{
                info.put("en", "e_cs");
            }
            info.put("pl", platforName);
            info.put("sdk", sdkName);

            //构建
            String url = buildUrl(info);

            //TODO  将构建好的url添加到发送队列
            SendUrl.getSendUrl().addurlToQueue(url);


            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


    }

    /**
     * 构建url
     *
     * @param info
     * @return
     */
    private static String buildUrl(HashMap<String, String> info) {
        StringBuffer sb = new StringBuffer();
        sb.append(access_url).append("?");
        if (!info.isEmpty()) {
            for (Map.Entry<String, String> entry : info.entrySet()) {
                if (isNotEmpty(entry.getKey())) {
                    try {
                        String value = URLEncoder.encode(entry.getValue(), "utf-8");
                        sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                    } catch (UnsupportedEncodingException e) {
                        logger.log(Level.WARNING, "编码异常");
                    }
                }

            }


        }
        return sb.toString().substring(0,sb.length()-1);

    }

    /**
     * 判断 oid是否为空
     *
     * @param input
     * @return返回值为true时为空
     */
    private static boolean isEmpty(String input) {
        return input == null || input.trim().isEmpty();
    }

    private static boolean isNotEmpty(String input) {
        return !isEmpty(input);
    }

}
