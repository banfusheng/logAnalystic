import com.qf.mytest.acctestlogUtil;

import java.util.Map;

/**
 * 〈一句话功能简述〉 <br>
 *
 * @author Administrator
 * @create 2018/8/16
 * @since 1.0.0
 */

public class Testacclog {
    public static void main(String[] args) {
        Map<String, String> splitlog = acctestlogUtil.splitlog(
                "192.168.216.1^A1534332320.571^A192.168.216.51^A" +
                        "/index.html?ver=1.0&u_mid=aa-dd&en=e_cs&oid=123456&s" +
                        "dk=java_sdk&pl=java_server");
        System.out.println(splitlog);


    }
}
