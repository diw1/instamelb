package unimelb.edu.instamelb.extras;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
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

    public static boolean isJellyBeanOrGreater() {
        return Build.VERSION.SDK_INT >= 16 ? true : false;
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
        String localTime = sdf.format(new Date(time));

        return localTime;
    }


    public static Locations getLocation(Context context) {
        double latitude = 0.0;
        double longitude = 0.0;
        SimpleLocation location=new SimpleLocation(context);
        if (!location.hasLocationEnabled()) {
            // ask the user to enable location access
            SimpleLocation.openSettings(context);
        }
        return new Locations(longitude, latitude);
    }

//
//        LocationManager locationManager;
//        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//        Location location;
//        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            return new Locations(location.getLongitude(),location.getLatitude());
//        } else {
//            toggleGPS(context, locationManager);
//            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            return new Locations(location.getLongitude(),location.getLatitude());
//        }


    private static void toggleGPS(Context context,LocationManager lm) {
        Intent gpsIntent = new Intent();
        gpsIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
        gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
        gpsIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, gpsIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    public static final class Locations {
        private final double longitude;
        private final double latitude;

        public Locations(double longitude, double latitude) {
            this.longitude = longitude;
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public double getLatitude() {
            return latitude;
        }
    }

}
