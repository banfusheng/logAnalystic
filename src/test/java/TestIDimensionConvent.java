import com.qf.anlystic.model.dim.base.PlatFormDimension;
import com.qf.anlystic.service.impl.IDimensionConvertImpl;

/**
 * 〈一句话功能简述〉 <br>
 *
 * @author Administrator
 * @create 2018/8/21
 * @since 1.0.0
 */

public class TestIDimensionConvent {
    public static void main(String[] args) {
        PlatFormDimension platFormDimension = new PlatFormDimension("all");
        IDimensionConvertImpl iDimensionConvert = new IDimensionConvertImpl();

        System.out.println(iDimensionConvert.getDimensionIDByDimension(platFormDimension));


    }
}
