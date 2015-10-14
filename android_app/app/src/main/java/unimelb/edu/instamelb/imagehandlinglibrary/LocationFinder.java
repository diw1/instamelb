package unimelb.edu.instamelb.imagehandlinglibrary;

import android.location.Location;
import android.os.Bundle;

import static unimelb.edu.instamelb.extras.Util.getLocation;

/**
 * Created by bboyce on 12/09/15.
 */
public class LocationFinder implements android.location.LocationListener {

    static double latitude = 0;
    static double longitude = 0;

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    public static double getLatitude() {
        return latitude;
    }

    public static double getLongitude() {
        return longitude;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

