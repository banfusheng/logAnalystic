/**
 * 新增会员的工具类
 *
 * @author Administrator
 * @create 2018/8/22
 * @since 1.0.0
 */
package com.qf.util;

import com.qf.common.GlobalConstant;
import com.qf.common.KpiType;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class MemberUtil {
    private static Map<String, Boolean> cache = new LinkedHashMap() {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return this.size() > 2000;
        }
    };

    /**
     * 正则判断是否和法
     *
     * @param memberId
     * @return
     */
    public static boolean checkMemberId(String memberId) {
        String regex = "^[0-9a-zA-Z].*$";
        if (StringUtils.isNotBlank(memberId)) {
            return memberId.trim().matches(regex);
        }
        return false;

    }

    /**
     * 判断是否为新增会员
     *
     * @param memberId
     * @param conn
     * @param conf
     * @return
     */
    public static boolean isNewMember(String memberId, Connection conn, Configuration conf) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Boolean res = false;
        /**
         * 1、先查询缓存，缓存有则直接返回，没有就查询数据库
         * 数据库有为不是新会员，没有则是新会员
         */
        if (StringUtils.isNotBlank(memberId)) {
            if (!cache.containsKey(memberId)) {
                //查询数据库
                //select `member_id` from `member_info` where `member_id` = ?
                String sql = conf.get(GlobalConstant.PREFIX_TOTAL +
                        KpiType.EMPTY_MEMBER.kpiName);
                try {
                    ps = conn.prepareStatement(sql);
                    ps.setString(1, memberId);
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        //数据库中有该数据
                        res = false;
                    } else {
                        //数据库中么有该数据
                        res = true;
                    }
                    cache.put(memberId, res);
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    JdbcUtil.close(null, ps, rs);
                }
            }

        }
        //如果缓存中有则不是新会员
        return res == null ? false : res.booleanValue();

    }
}
