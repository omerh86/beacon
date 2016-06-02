package com.example.admin.beacon;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Admin on 1/31/2016.
 */
public class MylocationListener implements LocationListener {
    Data data;

    public MylocationListener() {
        data = Data.getinstance();
    }

    @Override
    public void onLocationChanged(Location loc) {

        data.lat = (int) loc.getLatitude();
        data.lon = (int) loc.getLongitude();

        Log.d("gps", "lat : " + data.lat + "lon :" + data.lon);
        Toast.makeText(MyApp.getAppContext(), "lat : " + data.lat + "lon :" + data.lon, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("gps", "disabled");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("gps", "enabled");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("gps", "statuschange");

    }
}
