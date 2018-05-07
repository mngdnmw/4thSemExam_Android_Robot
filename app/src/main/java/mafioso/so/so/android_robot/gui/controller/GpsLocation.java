package mafioso.so.so.android_robot.gui.controller;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;


public class GpsLocation implements IViewCallBack {


    Context context;

    LocationListener locListener;

    LocationManager locationManager;

    Location homeLoc;
    Location currentLoc;

    private NumberFormat formatter;

    public GpsLocation(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        locListener = null;
        formatter = new DecimalFormat("#.##");
    }

    protected void showLastKnownLocation() {
        Location location = lastKnownLocation();
        if (location == null) {
            Toast.makeText(context, "Last known location is null",
                    Toast.LENGTH_LONG).show();
            return;
        }
        this.homeLoc = location;
        String latitude = formatter.format(location.getLatitude());
        String longitude = formatter.format(location.getLongitude());
        String msg = "Home loc = Latitude: " + latitude + "\n" + "Longitude: "
                + longitude;


    }

    public Location lastKnownLocation() {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d("GPSGPS", "Has permission, getting location");
            return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        }
        Log.d("GPSGPS", "No permission last known");
        return null;
    }

    public void setCurrentLocation(Location loc)
    {
        this.currentLoc = loc;
        String latitude = formatter.format(loc.getLatitude());
        String longitude = formatter.format(loc.getLongitude());
        //txtCurrentLoc.setText("Current Loc = Latitude: " + latitude + " Longitude: " + longitude);
        if (homeLoc != null && currentLoc != null) {
            //setTxtDistance(Float.parseFloat(formatter.format(homeLoc.distanceTo(currentLoc))));
        }
    }
}
