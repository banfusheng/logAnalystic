/**获取各个维度对应的id
 * 操作维度表的接口实现
 *
 * @author Administrator
 * @create 2018/8/21
 * @since 1.0.0
 */
package com.qf.anlystic.service.impl;

import com.qf.anlystic.model.dim.base.*;
import com.qf.anlystic.model.dim.value.BaseStatsValueWritable;
import com.qf.anlystic.service.IDimensionConvert;
import com.qf.util.JdbcUtil;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class IDimensionConvertImpl implements IDimensionConvert {
    private static final Logger logger = Logger.getLogger(
            IDimensionConvertImpl.class);

    //用于存储维度  维度累计的sql个数
    public Map<String, Integer> cache = new HashMap<>();
    //各种维度对应的id的缓存map
    private Map<String, Integer> cacheDimension =
            new LinkedHashMap<String, Integer>() {
                @Override
                protected boolean removeEldestEntry(Map.Entry eldest) {
                    return this.size() > 2000;
                }
            };
    //
    private Map<String, Integer> cacheWritable =
            new LinkedHashMap<String, Integer>() {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, Integer> eldest) {
                    return this.size() > 2000;
                }
            };

    /**
     * 获取维度id
     * 0.先查询缓存中是否存在对应维度,如果有直接取出返回
     * 1.先用根据维度属性去查询数据库,如果有就返回维度对应的ID
     * 2.如果没有先插入先返回
     */
    @Override
    public int getDimensionIDByDimension(BaseDimension baseDimension){
        Connection conn = null;
        try {
            //判断缓存中是否有该维度的id
            String cacheKey = this.buildCache(baseDimension);
            if (this.cache.containsKey(cacheKey)) {
                //若存在直接就返回
                return this.cache.get(cacheKey).intValue();
            }
            //缓存中没有该维度的eid需要去数据库中查询
            conn = JdbcUtil.getConn();
            //构建查询或插入维度表的sql语句
        /*'
        1、先查询维度表，是否有维度所对应的维度Id，有查询返回；没有先插入再返回
         */
            String[] sqls = null;
            if (baseDimension instanceof BrowserDimension) {
                sqls = this.buildBrowserSqls(baseDimension);
            } else if (baseDimension instanceof DateDimension) {
                sqls = this.buildDateSqls(baseDimension);
            } else if (baseDimension instanceof PlatFormDimension) {
                sqls = this.buildPlatformSqls(baseDimension);
            } else if (baseDimension instanceof KpiDimension) {
                sqls = this.buildKpiSqls(baseDimension);
            } else if (baseDimension instanceof LocationDimension) {
                sqls = this.buildLocalSqls(baseDimension);
            } else if (baseDimension instanceof EventDimension) {
                sqls = this.buildEventSqls(baseDimension);
            } else if (baseDimension instanceof CurrentcyTypeDimension) {
                sqls = this.buildCurrencySqls(baseDimension);
            } else if (baseDimension instanceof PaymentTypeDimension) {
                sqls = this.buildPaymentSqls(baseDimension);
            } else {
                throw new RuntimeException("dimension维度暂不支持.");
            }

            int id = -1;
            //执行sql
            synchronized (this) {
                id = this.executSql(conn, cacheKey, sqls, baseDimension);
            }
            //将获取出来的维度id的值存储到缓存中
            this.cache.put(cacheKey, id);
            return id;
        } catch (Exception e) {
            throw new RuntimeException("获取维度Id方法失败", e);
        } finally {
            JdbcUtil.close(conn, null, null);
        }

    }

    /**
     * 执行sql
     *
     * @param conn
     * @param cacheKey
     * @param sqls
     * @param baseDimension
     * @return
     */
    private int executSql(Connection conn, String cacheKey, String[] sqls, BaseDimension baseDimension) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            //获取ps
            //先查询是否有该对象对应的id,没有的话则插入
            ps = conn.prepareStatement(sqls[0]);
            this.setArgs(baseDimension, ps);
            rs = ps.executeQuery();
            //查询出来则返回该对象对应的id
            if (rs.next()) {
                return rs.getInt(1);
            }
            //如果没有查询到 则执行插入方法
            ps = conn.prepareStatement(sqls[1], Statement.RETURN_GENERATED_KEYS);
            this.setArgs(baseDimension, ps);
            //返回影响的函数
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            logger.warn("执行查询或插入sql语句异常", e);
        } finally {
            JdbcUtil.close(null, ps, rs);
        }

        throw new RuntimeException("该diemnsion不能获取到维度Id.dimension:"
                + baseDimension.getClass());
    }

    /**
     * 设置sql执行语句的参数
     *
     * @param baseDimension
     * @param ps
     */
    private void setArgs(BaseDimension baseDimension, PreparedStatement ps) {
        try {
            int i = 0;
            if (baseDimension instanceof BrowserDimension) {
                BrowserDimension browserDimension = (BrowserDimension) baseDimension;
                ps.setString(++i, browserDimension.getBrowserName());
                ps.setString(++i, browserDimension.getBrowserVersion());
            } else if (baseDimension instanceof PlatFormDimension) {
                PlatFormDimension platFormDimension = (PlatFormDimension) baseDimension;
                ps.setString(++i, platFormDimension.getPlatformName());
            } else if (baseDimension instanceof DateDimension) {
                DateDimension date = (DateDimension) baseDimension;
                ps.setInt(++i, date.getYear());
                ps.setInt(++i, date.getSeason());
                ps.setInt(++i, date.getMonth());
                ps.setInt(++i, date.getWeek());
                ps.setInt(++i, date.getDay());
                //把java.uitl中的date转成java.sql的date
                ps.setDate(++i, new Date(date.getDate().getTime())); //设置时间
                ps.setString(++i, date.getType());
            } else if (baseDimension instanceof KpiDimension) {
                KpiDimension kpi = (KpiDimension) baseDimension;
                ps.setString(++i, kpi.getKpiName());
            } else if (baseDimension instanceof LocationDimension) {
                LocationDimension local = (LocationDimension) baseDimension;
                ps.setString(++i, local.getCountry());
                ps.setString(++i, local.getProvince());
                ps.setString(++i, local.getCity());
            } else if (baseDimension instanceof EventDimension) {
                EventDimension event = (EventDimension) baseDimension;
                ps.setString(++i, event.getCategory());
                ps.setString(++i, event.getAction());
            } else if (baseDimension instanceof PaymentTypeDimension) {
                PaymentTypeDimension payment = (PaymentTypeDimension) baseDimension;
                ps.setString(++i, payment.getPaymentType());
            } else if (baseDimension instanceof CurrentcyTypeDimension) {
                CurrentcyTypeDimension currency = (CurrentcyTypeDimension) baseDimension;
                ps.setString(++i, currency.getCurrencyName());
            } else {
                throw new RuntimeException("dimension维度暂不支持.");
            }
        } catch (Exception e) {
            logger.warn("设置参数异常 ",e);

        }

    }

    /**
     * 构建kpi的sql
     *
     * @param dimension
     * @return
     */
    private String[] buildKpiSqls(BaseDimension dimension) {
        String query = "select id from `dimension_kpi` where `kpi_name` = ?";
        String insert = "insert into `dimension_kpi`(`kpi_name`) values(?)";
        return new String[]{query, insert};
    }

    private String[] buildPlatformSqls(BaseDimension dimension) {
        String query = "select id from `dimension_platform` where `platform_name` = ?";
        String insert = "insert into `dimension_platform`(`platform_name`) values(?)";
        return new String[]{query, insert};
    }

    private String[] buildDateSqls(BaseDimension dimension) {
        String query = "select id from `dimension_date` where `year` = ? and `season` = ? and `month` = ?" +
                " and `week` = ? and `day` = ? and `calendar` = ? and `type` = ?";
        String insert = "insert into `dimension_date`(`year` ,`season` ,`month` ,`week` ,`day` ,`calendar` ,`type`) " +
                "values(?,?,?,?,?,?,?)";
        return new String[]{query, insert};
    }

    private String[] buildBrowserSqls(BaseDimension dimension) {
        String query = "select id from `dimension_browser` where `browser_name` = ? and `browser_version` = ?";
        String insert = "insert into `dimension_browser`(`browser_name`,`browser_version`) values(?,?)";
        return new String[]{query, insert};
    }

    private String[] buildLocalSqls(BaseDimension dimension) {
        String query = "select id from `dimension_location` where `country` = ? and `province` = ? and `city` = ?";
        String insert = "insert into `dimension_location`(`country`,`province`,`city`) values(?,?,?)";
        return new String[]{query, insert};
    }

    private String[] buildEventSqls(BaseDimension dimension) {
        String query = "select id from `dimension_event` where `category` = ? and  `action` = ?";
        String insert = "insert into `dimension_event`(`category`, `action`) values(?,?)";
        return new String[]{query, insert};
    }

    private String[] buildPaymentSqls(BaseDimension dimension) {
        String query = "select id from `dimension_payment_type` where `payment_type` = ?";
        String insert = "insert into `dimension_payment_type`(`payment_type`) values(?)";
        return new String[]{query, insert};
    }

    private String[] buildCurrencySqls(BaseDimension dimension) {
        String query = "select id from `dimension_currency_type` where `currency_name` = ?";
        String insert = "insert into `dimension_currency_type`(`currency_name`) values(?)";
        return new String[]{query, insert};
    }


    @Override
    public int getDimensionIDByDimension(BaseStatsValueWritable baseStatsValueWritable) {
        return 0;
    }

    /**
     * 构建每一个Dimension的key
     *
     * @param baseDimension
     * @return
     */
    private String buildCache(BaseDimension baseDimension) {
        StringBuffer sb = new StringBuffer();
        if (baseDimension instanceof BrowserDimension) {
            sb.append("browser_");
            BrowserDimension browser = (BrowserDimension) baseDimension;
            sb.append(browser.getBrowserName()).append(browser.getBrowserVersion());

        } else if (baseDimension instanceof DateDimension) {
            sb.append("date_");
            DateDimension date = (DateDimension) baseDimension;
            sb.append(date.getYear()).append(date.getSeason())
                    .append(date.getMonth()).append(date.getWeek())
                    .append(date.getDay()).append(date.getType());

        } else if (baseDimension instanceof PlatFormDimension) {
            sb.append("platform");
            PlatFormDimension platform = (PlatFormDimension) baseDimension;
            sb.append(platform.getPlatformName());
        } else if (baseDimension instanceof KpiDimension) {
            sb.append("kpi_");
            KpiDimension kpi = (KpiDimension) baseDimension;
            sb.append(kpi.getKpiName());
        } else if (baseDimension instanceof LocationDimension) {
            sb.append("local_");
            LocationDimension local = (LocationDimension) baseDimension;
            sb.append(local.getCountry());
            sb.append(local.getProvince());
            sb.append(local.getCity());
        } else if (baseDimension instanceof EventDimension) {
            sb.append("event_");
            EventDimension event = (EventDimension) baseDimension;
            sb.append(event.getCategory());
            sb.append(event.getAction());
        } else if (baseDimension instanceof PaymentTypeDimension) {
            sb.append("payment_");
            PaymentTypeDimension payment = (PaymentTypeDimension) baseDimension;
            sb.append(payment.getPaymentType());
        } else if (baseDimension instanceof CurrentcyTypeDimension) {
            sb.append("currency_");
            CurrentcyTypeDimension currency = (CurrentcyTypeDimension) baseDimension;
            sb.append(currency.getCurrencyName());
        } else {
            throw new RuntimeException("dimension维度暂不支持." + baseDimension.getClass());
        }


        return sb == null ? null : sb.toString();
    }


}
