/**
 * JDBC工具类, 获取MySQL的连接和关闭
 *
 * @author Administrator
 * @create 2018/8/21
 * @since 1.0.0
 */
package com.qf.util;

import com.qf.common.GlobalConstant;

import java.sql.*;

public class JdbcUtil {
    static {
        try {
            Class.forName(GlobalConstant.DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("找不到Mysql的驱动类");

        }
    }
    /**
     * 获取Mysql的链接
     */
    public static Connection getConn(){
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(GlobalConstant.URL,
                    GlobalConstant.USERNAME,GlobalConstant.PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("加载数据库链接未成功");
        }
        return conn;
    }

    /**
     * 关闭相关对象
     */
    public static  void close(Connection conn, PreparedStatement ps,ResultSet rs){
        if (rs != null){
            try {
                rs.close();
            } catch (SQLException e) {
                //do nothing
            }
        }
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                // do nothing
            }
        }
        if (conn  !=null){
            try {
                conn.close();
            } catch (SQLException e) {
                //do nothing

            }
        }
    }

    public static void main(String[] args) {

        System.out.println(JdbcUtil.getConn());


    }




}
