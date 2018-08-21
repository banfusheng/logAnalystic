import com.qf.anlystic.model.dim.base.DateDimension;
import com.qf.common.DateEnum;

import java.util.Calendar;

/**
 * 〈一句话功能简述〉 <br>
 *
 * @author Administrator
 * @create 2018/8/21
 * @since 1.0.0
 */

public class TestDateDimension {
    public static void main(String[] args) {
        //1534506930123
        //Date date = new Date(1534506930123l);
        long time = 1534506930123l;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(1534506930123l);
        System.out.println(calendar.get(Calendar.YEAR));
        System.out.println(calendar.get(Calendar.MONTH)+1);
        System.out.println(calendar.get(Calendar.DAY_OF_MONTH));
        DateDimension dateDimension = DateDimension.buildDate(time, DateEnum.DAY);
        System.out.println(dateDimension);

    }
}
