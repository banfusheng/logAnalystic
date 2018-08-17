import com.qf.etl.util.ip.IPSeeker;
import com.qf.mytest.taobaoIpparse;

import java.util.List;

/**
 * 〈一句话功能简述〉 <br>
 *
 * @author Administrator
 * @create 2018/8/16
 * @since 1.0.0
 */

public class Testtaobao {
    public static void main(String[] args) {
//219.136.134.157  220.194.218.22
  /*      tobaoIpParseUtil tobaoIpParseUtil = new tobaoIpParseUtil();

        System.out.println(tobaoIpParseUtil.getInfo("149.244.251.66"));*/

     /*   List<String> allIp = IPSeeker.getInstance().getAllIp();
        for (String ip : allIp) {
            System.out.println(ip + "--------------" +
                    tobaoIpParseUtil.getInfo(ip));

            System.out.println();
        }*/


        System.out.println("---------------------------------------");
        taobaoIpparse taobaoIpparse = new taobaoIpparse();
        System.out.println(taobaoIpparse.parseInfo(
                "149.244.251.66"));
        List<String> allIp = IPSeeker.getInstance().getAllIp();
        for (String ip : allIp) {
            System.out.println(ip + "--------------" +
                    taobaoIpparse.parseInfo(ip));

            System.out.println();
        }

    }
}
