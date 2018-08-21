import com.qf.etl.util.IPParseUtil;
import com.qf.etl.util.ip.IPSeeker;

import java.util.List;

/**
 * 〈一句话功能简述〉 <br>
 *
 * @author Administrator
 * @create 2018/8/15
 * @since 1.0.0
 */

public class testIp {
    public static void main(String[] args) {


      /*  System.out.println(new IPParseUtil()
                .parseIp("221.11.112.123"));*/

        String country = IPSeeker.getInstance().getCountry("1.2.4.7");
        System.out.println(country);
        List<String> allIp = IPSeeker.getInstance().getAllIp();
        for (String ip : allIp) {
            System.out.println(ip +"--------------"+new IPParseUtil()
                    .parseIp(ip));

        }
        /*try {
            IPParseUtil.RegionInfo regionInfo = new IPParseUtil().parserIp1("http://ip.taobao.com/service/getIpInfo.php?ip=221.11.112.255"
                    , "utf-8");
            System.out.println(regionInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }*/


    }
}
