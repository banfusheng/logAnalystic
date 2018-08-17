/**
 * 〈一句话功能简述〉 <br>
 *
 * @author Administrator
 * @create 2018/8/16
 * @since 1.0.0
 */
package com.qf.etl.util;

import org.apache.directory.api.util.Strings;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class acctestlogUtil {
    private static final Logger logger = Logger.getLogger(acctestlogUtil.class);

    private static Map<String, String> map = null;

    public static Map<String, String> splitlog(String log) {
        map = new HashMap<>();
        if (Strings.isEmpty(log)) {
            logger.warn("log为空");
            return map;
        }
        String[] split = log.split("\\?");
        frontlog(split[0]);
        if (split.length > 1) {
            laterlog(split[1]);
        }


        return map;
    }

    private static void laterlog(String log) {
        if (Strings.isEmpty(log)) {
            logger.warn("log前半部分为空");

        }
        String[] split = log.split("\\&");
        for (String kv : split) {
            String[] arr = kv.split("=");
            map.put(arr[0], arr[1]);
        }
    }

    private static void frontlog(String log) {
        if (Strings.isEmpty(log)) {
            logger.warn("log后半部分为空");

        }
        String[] split = log.split("\\^A");
        map.put("srcIp", split[0]);
        map.put("time", split[1]);
        map.put("descIp", split[2]);
        map.put("page", split[3]);
    }


}
