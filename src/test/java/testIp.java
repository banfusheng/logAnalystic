/**
 * 〈一句话功能简述〉 <br>
 *
 * @author Administrator
 * @create 2018/8/15
 * @since 1.0.0
 */

import com.qf.etl.util.IPParseUtil;
import com.qf.etl.util.ip.IPSeeker;

import java.util.List;

public class testIp {
    public static void main(String[] args) {

        System.out.println(new IPParseUtil()
                .parseIp("221.11.112.123"));


        List<String> allIp = IPSeeker.getInstance().getAllIp();
        for (String ip : allIp) {
            System.out.println(ip +"       "+new IPParseUtil()
                    .parseIp(ip));

        }

    }
}
