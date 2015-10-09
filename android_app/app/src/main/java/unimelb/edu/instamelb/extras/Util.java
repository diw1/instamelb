package unimelb.edu.instamelb.extras;

import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Windows on 05-02-2015.
 */
public class Util {
    public static boolean isLollipopOrGreater() {
        return Build.VERSION.SDK_INT >= 21 ? true : false;
    }
    public static boolean isJellyBeanOrGreater(){
        return Build.VERSION.SDK_INT>=16?true:false;
    }

    /*
* Given 1234567890, return "1,234,567,890"
*/
    public static String getPrettyCount(int count) {
        String regex = "(\\d)(?=(\\d{3})+$)";
        return Integer.toString(count).replaceAll(regex, "$1,");
    }

    public static String getDateTime(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String localTime = sdf.format(new Date(time) );

        return localTime;
    }
}
