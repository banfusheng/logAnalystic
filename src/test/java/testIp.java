import com.qf.etl.util.ip.IPSeeker;

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

        String country = IPSeeker.getInstance().getCountry("221.11.112.255");
        System.out.println(country);
       /* List<String> allIp = IPSeeker.getInstance().getAllIp();
        for (String ip : allIp) {
            System.out.println(ip +"--------------"+new IPParseUtil()
                    .parseIp(ip));

        }*/



    }
}
