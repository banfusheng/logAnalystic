import com.qf.etl.util.LogUtil;

import java.util.Map;

/**
 * 〈一句话功能简述〉 <br>
 *
 * @author Administrator
 * @create 2018/8/17
 * @since 1.0.0
 */

public class testlogUtil {
    public static void main(String[] args) {


        Map<String, String> map = LogUtil.hangleLog("120.197.87.216^A1496208168.276" +
                "^Ahh^A/BCImg.gif?en=e_pv&p_url=http%3A%2F%2Fwww.mbeicai.com%2F%236d&" +
                "p_ref=https%3A%2F%2Fwww.baidu.com%2Fbaidu.php%3Fwd%3Dbeifengwang%2" +
                "6issp%3D1%26f%3D8%26ie%3Dutf-8%26tn%3D95230157_hao_pg%26inputT%3D2" +
                "412&tt=%E5%8C%97%E9%A3%8E%E7%BD%91-IT%E5%9C%A8%E7%BA%BF%E6%95%99%E8" +
                "%82%B2java%E5%9F%B9%E8%AE%AD%2Casp.net%E5%9F%B9%E8%AE%AD%2Cphp%E5%9F" +
                "%B9%E8%AE%AD%2Candroid%E5%9F%B9%E8%AE%AD%2CC%2FC%2B%2B%E5%9F%B9%E8%AE%A" +
                "D-%E4%B8%AD%E5%9B%BDIT%E7%BD%91%E7%BB%9C%E6%95%99%E8%82%B2%E7%AC%AC%E4" +
                "%B8%80%E5%93%81%E7%89%8C%E3%80%82&ver=1&pl=website&sdk=js&u_ud=D4289356" +
                "-5BC9-47C4-8F7D-A16022833E7E&u_sd=FA47F1DG-2C1B-4F41-8C38-344040ABCCA1&c" +
                "_time=145013766375&l=zh-CN&b_iev=Mozilla%2F5.0%20(Windows%20NT%206.1%3B%" +
                "20WOW64)%20AppleWebKit%2F537.36%20(KHTML%2C%20like%20Gecko)%20Chrome%2F4" +
                "6.0.2490.71%20Safari%2F537.36&b_rst=1280*768&u_mid=beicainet");
        for (Map.Entry<String,String> entry:map.entrySet()) {

            System.out.println(entry.getKey()+ "==="+entry.getValue());
        }

    }
}
