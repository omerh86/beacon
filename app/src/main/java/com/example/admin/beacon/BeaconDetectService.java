package com.example.admin.beacon;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.SmsManager;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

public class BeaconDetectService extends Service implements BeaconConsumer {

    static BeaconConsumer beaconConsumer;
    public static BeaconManager beaconManager;
    protected static final String TAG = "RangingActivity";
    Data data;
    public static double dbm = 0;
    int counter = 0;
    LocationManager mlocManager;

    public BeaconDetectService() {
        data = Data.getinstance();
        Log.d("context", "" + this);
        beaconManager = BeaconManager.getInstanceForApplication(MyApp.getAppContext());
        // beaconManager = MainActivity.beaconManager;
        beaconManager.getBeaconParsers().add(new BeaconParser().

                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconConsumer = this;
        //conncting to gps

//        MylocationListener mlocListener;
//        mlocManager = (LocationManager) MyApp.getAppContext().getSystemService(Context.LOCATION_SERVICE);
//        mlocListener = new MylocationListener();
//        // Log.d("gps", "gps1");
//        Toast.makeText(MyApp.getAppContext(), "connecting gps", Toast.LENGTH_LONG).show();
//        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, mlocListener);


    }

    public static void bindBeacon() {
        beaconManager.bind(beaconConsumer);
        Log.d("bind", "");
    }

    public static void unBindBeacon() {
        beaconManager.unbind(beaconConsumer);
        Log.d("unbind", "");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

        SharedPreferences sharedpreferences = getSharedPreferences(Data.PREFFOLDER, Context.MODE_PRIVATE);

        boolean isbind = sharedpreferences.getBoolean(Data.SHOULDBIND, false);
        if (isbind) {
            bindBeacon();
        }


        return START_STICKY;

    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBeaconServiceConnect() {
//
        beaconManager.setBackgroundMode(true);
        beaconManager.setBackgroundScanPeriod(1000L);
        beaconManager.setBackgroundBetweenScanPeriod(1200l);
        try {
            beaconManager.updateScanPeriods();
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.d("sdsadsada", "sds: " + e.toString());
        }
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
               Log.d("scanning","scanning right now");
                if (beacons.size() > 0) {

                    Log.i(TAG, "The  beacon " + beacons.iterator().next().getBluetoothAddress() + " is " + beacons.iterator().next().getRssi() + " db");
                    // Log.d("alert", "The db is " + data.rangeOfAlert);

//                    Log.i(TAG, "adress " + beacons.iterator().next().getBluetoothAddress() + " RSSI");
//                    Log.i(TAG, "id1 " + beacons.iterator().next().getId1());
//                    Log.i(TAG, "id2 " + beacons.iterator().next().getId2());
//                    Log.i(TAG, "id3 " + beacons.iterator().next().getId3());
                    // Log.i(TAG, "adress " + beacons.iterator().next().getIdentifiers());
                    // dbm = (double) beacons.iterator().next().getDistance();
                    dbm = (double) beacons.iterator().next().getRssi();


//                    if (dbm > data.rangeOfAlert / 2) {
                    if (dbm < -70) {
                        counter++;

                        if (counter > 3) {
                            counter = 0;

                            if (!AlertActivity.active) {
                                Intent i = new Intent(MyApp.getAppContext(), AlertActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                MyApp.getAppContext().startActivity(i);

                            } else {
                                Log.d("alert", "need to");
                                //  Toast.makeText(MyApp.getAppContext(), "need to start alert", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }


                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
        }
    }




    public class OnStatusChangeReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("onrecieve", "");
            if (intent.getStringExtra("mission").equals("start")) {
                bindBeacon();
            } else {
                unBindBeacon();
            }
        }
    }

    protected void sendSMSMessage() {
        Log.d("Send SMS", "");
        if (data.lat == 0) {
            Location loc = mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            data.lat = (int) loc.getLatitude();
            data.lon = (int) loc.getLongitude();
        }

        Log.d("location SMS", "Location: lat- " + data.lat + " lon- " + data.lon);
        SmsManager smsManager = SmsManager.getDefault();
        for (Person person : data.contactArray) {
            if (person.getPhoneNumber().length() > 1) {
                try {
                    smsManager.sendTextMessage(person.getPhoneNumber(), null, "Location: lat- " + data.lat + " lon- " + data.lon + ", " + person.getMassege(), null, null);
                    Log.d("SMS sent", "");
                    Log.d("phone number", "" + person.getPhoneNumber());
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        }


    }


}
