import java.util.Calendar;

/**
 * 〈一句话功能简述〉 <br>
 *
 * @author Administrator
 * @create 2018/8/18
 * @since 1.0.0
 */

public class calendarTest {
    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        System.out.println(calendar.get(Calendar.DAY_OF_MONTH));
        System.out.println(calendar.get(Calendar.MONTH));
        calendar.set(Calendar.WEEK_OF_YEAR,1);
        System.out.println(calendar.get(Calendar.DAY_OF_MONTH));
        System.out.println(calendar.get(Calendar.MONTH));
    }
}
