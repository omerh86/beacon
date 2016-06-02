package com.example.admin.beacon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class AlertActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mLocationClient;


    com.michaldrabik.tapbarmenulib.TapBarMenu t;
    tr.xip.markview.MarkView circle;
    Context context;
    Data data;
    ToneGenerator toneG;

    int maxNumberForRing = 300;

    private TextView timerValue, alertTv;
    private long startTime = 0L;
    private Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    int counterForCircle = 0;
    int counterforCounter = 0;
    boolean stopCounters = false;
    LinearLayout maplayout;
    LinearLayout ll;
    Button mapBtn;
    TextView distanceTv;
    static boolean active = false;
    String distanceHelper = " / ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
       // PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP, "My Tag");
       // wl.acquire();
        active = true;
        context = this;
        data = Data.getinstance();
//        Window window = this.getWindow();
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        long color = Long.parseLong("0B7792", 16);
//        window.setStatusBarColor(Color.GRAY);

        toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        // toneG = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        setContentView(R.layout.activity_alert);


        setUpMapIfNeeded();


        ll = (LinearLayout) findViewById(R.id.ll);

        distanceTv = (TextView) findViewById(R.id.distancetv);
        distanceTv.setText(0 + distanceHelper + 0);
        alertTv = (TextView) findViewById(R.id.alertmassege);
        //  String s = data.alertMassege.substring(0, 15);

        alertTv.setText(data.alertMassege + "...");
        timerValue = (TextView) findViewById(R.id.timerValue);
        startTime = SystemClock.uptimeMillis();

        customHandler.postDelayed(updateTimerThread, 100);
        maplayout = (LinearLayout) findViewById(R.id.maplayout);
        maplayout.setVisibility(View.GONE);

        mapBtn = (Button) findViewById(R.id.mapbtn);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, MapsActivity.class);
//                startActivity(intent);

                if (maplayout.getVisibility() == View.VISIBLE) {
                    maplayout.setVisibility(View.GONE);
                    ll.setAlpha(1);
                } else {
                    maplayout.setVisibility(View.VISIBLE);
                    ll.setAlpha((float) 0.5);
                }
            }
        });

//        t = (com.michaldrabik.tapbarmenulib.TapBarMenu) findViewById(R.id.tapBarMenu);
//        t.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent intent = new Intent(context, MapsActivity.class);
////                startActivity(intent);
//                t.toggle();
//                if (maplayout.getVisibility() == View.VISIBLE) {
//                    maplayout.setVisibility(View.GONE);
//                    ll.setAlpha(1);
//                } else {
//                    maplayout.setVisibility(View.VISIBLE);
//                    ll.setAlpha((float) 0.5);
//                }
//            }
//        });
        circle = (tr.xip.markview.MarkView) findViewById(R.id.circle);
        long color2 = Long.parseLong("CBCBCB", 16);
        circle.setMark(0);
        circle.setMax(maxNumberForRing);
        circle.setRingColor((int) color2);
        circle.setStrokeColors(Color.GRAY);
        circle.setRingWidth(20);
        circle.setStrokeWidth(20);
        circle.setTextSize(0);

        circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                customHandler.removeCallbacks(updateTimerThread);
                circle.setMark(maxNumberForRing);
                counterForCircle = 0;

            }
        });
    }

    public void makeSound() {


        //toneG.startTone(data.alertSound, 1000);

    }

    public void stopSound() {

        toneG.stopTone();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    protected void onStop() {
        super.onStop();
        active = false;
        counterForCircle = 0;
        customHandler.removeCallbacks(updateTimerThread);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alert, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);

            int mins = secs / 60;

            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);

            timerValue.setText("" + mins + ":"
                    + String.format("%02d", secs));
            distanceTv.setText(String.format("%.2f", BeaconDetectService.dbm) + "m" + distanceHelper + String.format("%.2f", BeaconDetectService.dbm / 3) + "f");

            if (!stopCounters) {
                counterforCounter++;
                if (counterforCounter % 4 == 0) {
                    counterForCircle++;
                    circle.setMark(counterForCircle);
                    makeSound();
                    if (counterForCircle > maxNumberForRing) {
                        counterForCircle = 0;
                        //send massege
                        sendSMSMessage();
                        // customHandler.removeCallbacks(updateTimerThread);
                        stopCounters = true;
                        stopSound();
                        customHandler.removeCallbacks(updateTimerThread);
                        return;

                    }
                }
            }
            customHandler.postDelayed(this, 100);
        }


    };

    private void setUpMapIfNeeded() {
        mLocationClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        mLocationClient.connect();
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }

    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

    }

    public void getCurrentLocation() {
        Location currentL = LocationServices.FusedLocationApi.getLastLocation(mLocationClient);
        if (currentL == null) {
            Toast.makeText(MyApp.getAppContext(), "Cant get location", Toast.LENGTH_SHORT).show();

        }
        if (currentL != null) {
            Data data = Data.getinstance();
            data.lat = currentL.getLatitude();
            data.lon = currentL.getLongitude();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(data.lat, data.lon), 15));
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(new LatLng(data.lat, data.lon)).title("Marker"));
            CircleOptions circleOptions = new CircleOptions().center(new LatLng(data.lat, data.lon)).radius(data.rangeOfAlert);
            mMap.addCircle(circleOptions);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected void sendSMSMessage() {
        Log.d("Send SMS", "");

        Log.d("location SMS", "Location: lat- " + data.lat + " lon- " + data.lon);
        SmsManager smsManager = SmsManager.getDefault();
        for (Person person : data.contactArray) {
            if (person.getPhoneNumber().length() > 1) {
                try {
                    smsManager.sendTextMessage(person.getPhoneNumber(), null, "Location: lat- " + data.lat + " lon- " + data.lon + ", " + data.smsMassege, null, null);
                    Log.d("SMS sent", "");
                    Log.d("phone number", "" + person.getPhoneNumber());
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "SMS faild.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }

        showDialog(this, "2 FIND", "Messages have been sent to contacts");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    public void showDialog(Activity activity, String title, CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        if (title != null) builder.setTitle(title);

        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}


