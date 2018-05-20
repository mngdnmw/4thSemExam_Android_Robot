package mafioso.so.so.android_robot.gui.helper;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;


public class GpsLocation {

    private Context mContext;
    private LocationManager locationManager;
    private static final String TAG = "GPS";


    public GpsLocation(Context context) {
        mContext = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public Location lastKnownLocation() {

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Has permission, getting location");
            return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        }
        Log.d(TAG, "No permission last known");
        return null;
    }
}
