/**
 * 将队列中的url发送出去
 *
 * @author Administrator
 * @create 2018/8/15
 * @since 1.0.0
 */
package com.qf.jSdk;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SendUrl {
    //获取日志的打印对象
    private final static Logger logger = Logger.getGlobal();
    //获取一个线程安全的队列  用于存储url
    private static final BlockingQueue<String> queue =
            new LinkedBlockingQueue<>();

    //创建和获取一个单例对象
    //私有对象和构造器
    private static SendUrl sendUrl = null;

    private SendUrl() {

    }

    public static SendUrl getSendUrl() {
        if (sendUrl == null) {
            //添加同步代码块防止两个线程同时获取对象
            synchronized (SendUrl.class) {
                if (sendUrl == null) {
                    sendUrl = new SendUrl();
                    //TODO 创建独立线程 发送Url
                    Thread th = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SendUrl.getSendUrl().consumeUrl();
                        }
                    });
                    //启动线程// 一般哦用于在服务器中
                    //th.setDaemon(true);//是否过载在后台启动守护线程
                    th.start();
                }
            }
        }
        return sendUrl;
    }

    /**
     * 添加url到队列中
     *
     * @param url
     */
    public static void addurlToQueue(String url) {
        try {
            //若队列没有空间  将一直等待
            getSendUrl().queue.put(url);
            //若队列没有空间或非法空间   会抛异常
            //getSendUrl().queue.add(url);
        } catch (InterruptedException e) {
            logger.log(Level.WARNING, "添加url到队列异常");
        }
    }

    /**
     * 消费url
     * 获取并发送
     */
    public static void consumeUrl() {
        while (true) {
            //getSendUrl().queue.poll();
            try {
                String url = getSendUrl().queue.take();
                HttpRequestUtil.requesUrl(url);
            } catch (InterruptedException e) {
                logger.log(Level.WARNING, "队列获取url异常");
            }
        }
    }


    /**
     * 真正发送url的类
     */
    private static class HttpRequestUtil {

        /**
         * 真正发送的方法
         *
         * @param Url
         */
        public static void requesUrl(String Url) {
            HttpURLConnection conn = null;
            InputStream is = null;
            try {
                //获取url
                URL u = new URL(Url);
                conn = (HttpURLConnection) u.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);

                //conn qingq
                is = conn.getInputStream();


            } catch (Exception e) {
                logger.log(Level.WARNING, "真正发送url异常");
            } finally {
                conn.disconnect();
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

}
