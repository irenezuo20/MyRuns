package dartmouth.cs.qiyaozuo;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static dartmouth.cs.qiyaozuo.SensorsService.count;
import static dartmouth.cs.qiyaozuo.SensorsService.sum;
import static dartmouth.cs.qiyaozuo.StartFragment.ACTIVITY_TYPE;
import static java.lang.Math.abs;


public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = "MapActivity";
    public static final String STOP_FOREGROUND = "stop service";
    public static final String START_FOREGROUND = "start service";
    private CommentsDataSource dataSource;
    private GoogleMap mMap;
    public static Boolean ifNeedClear, isTracking = false;
    public static Boolean inService = false;//if the tracking service starts
    public Marker whereAmI, start;
    private double mAvgSpeed, mCurSpeed, mClimb, mCalorie, mDistance = 0;//in km
    private ArrayList<Double> mCurSpeeds = new ArrayList<Double>();
    private TextView mCalorieView, mTypeView, mAvgSpeedView, mClimbView, mDistanceView, mCurSpeedView;
    public static String mType = "";
    private Intent serviceIntent;
    private ArrayList<Polyline> polylines = new ArrayList<Polyline>();
    PolylineOptions rectOptions;
    public static ArrayList<LatLng> latlngList = new ArrayList<LatLng>();
    public static ArrayList<Location> locList = new ArrayList<Location>();
    private ArrayList<Long> timeList = new ArrayList<Long>();
    private String mUnit;
    private int mHour, mMinute, mSecond, mYear, mMonth, mDay = 0;
    DecimalFormat df = new DecimalFormat("0.0000");
    DecimalFormat df1 = new DecimalFormat("0.00000000");
    ServiceConnection con;
    private TrackingService trackingService;
    private Intent mSensorIntent;
    private boolean isAuto = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");

        //initialize UI
        setContentView(R.layout.activity_map);
        mClimbView = findViewById(R.id.text_climb);
        mCalorieView = findViewById(R.id.text_calorie);
        mTypeView = findViewById(R.id.text_type);
        mAvgSpeedView = findViewById(R.id.text_avg_speed);
        mCurSpeedView = findViewById(R.id.text_cur_speed);
        mDistanceView = findViewById(R.id.text_distance);
        mCurSpeedView = findViewById(R.id.text_cur_speed);

        if (getIntent().getExtras() != null) {
            mType = getIntent().getExtras().getString(ACTIVITY_TYPE, "unknown");
            Log.d(TAG, "onCreate: " + mType);
            if (mType.equals("auto")) {
                isAuto = true;
                mType = "standing";
                startSensorService();

            } else {
                isAuto = false;
            }
        }

        mTypeView.setText("type:" + mType);

        //get google map
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //check permission
        if (!checkPermission()) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        } else {
            startTrackingService();
            isTracking = true;
        }

        //create broadcast
        LocalBroadcastManager.getInstance(this).registerReceiver(mLocationBroadcastReceiver,
                new IntentFilter(TrackingService.BROADCAST_LOCATION));

        dataSource = HistoryFragment.getData();
    }

    private void startSensorService() {
        Log.d(TAG, "startSensorService: ");
        mSensorIntent = new Intent(this, SensorsService.class);
        startService(mSensorIntent);
    }

    //set up broadcast receiver
    BroadcastReceiver mLocationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TrackingService.BROADCAST_LOCATION)) {
                Location location = intent.getParcelableExtra("location");
                timeList.add(System.currentTimeMillis());
                updateLocation(location);
                updateText();
            }
        }
    };

    //update activity information
    private void updateText() {
        if (locList.size() > 1 && timeList.size() > 1) {
            //update distance
            float distance = locList.get(locList.size() - 2).distanceTo(locList.get(locList.size() - 1));
            mDistance += abs(distance / 1000);
            //Log.d(TAG, "updateText: " + mDistance);

            if (MainActivity.isMetric) {
                mDistanceView.setText("Distance:" + df.format(mDistance) + " km");
            } else {
                mDistanceView.setText("Distance:" + df.format(0.62 * mDistance) + " miles");
            }

            //update current speed
            long X = timeList.get(timeList.size() - 2);
            long Y = timeList.get(timeList.size() - 1);
            float timeElapsed = (Y - X) / 1000;
            mCurSpeed = abs(distance) / (timeElapsed * 60 * 60);
            //Log.d(TAG, "updateText: current speed: " + mCurSpeed + " current timelapse: " + timeElapsed);
            if (MainActivity.isMetric)
                mCurSpeedView.setText("cur speed:" + df1.format(mCurSpeed) + " km/h");
            else
                mCurSpeedView.setText("cur speed:" + df1.format(mCurSpeed * 0.62) + " m/h");


            //update average speed
            X = timeList.get(0);
            timeElapsed = (Y - X) / 1000;
            mAvgSpeed = (1000 * mDistance) / (timeElapsed * 60 * 60);
//            Log.d(TAG, "updateText: time" + timeElapsed);
//            Log.d(TAG, "updateText: distance" + mDistance);
//            Log.d(TAG, "updateText: avg speed" + mAvgSpeed);
            if (MainActivity.isMetric) {
                mAvgSpeedView.setText("avg speed:" + df1.format(mAvgSpeed) + " km/h");

            } else {
                mAvgSpeedView.setText("avg speed:" + df1.format(mAvgSpeed * 0.62) + " m/h");

            }

            //update climb
            double alt = locList.get(locList.size() - 1).getAltitude()
                    - locList.get(locList.size() - 2).getAltitude();
            mClimb += abs(alt / 1000);

            if (MainActivity.isMetric) {
                mClimbView.setText("climb:" + df.format(mClimb) + " km");

            } else {
                mClimbView.setText("climb:" + df.format(mClimb * 0.62) + " miles");

            }

            //update calorie
            //[(Age x 0.074) — (Weight x 0.05741) + (Heart Rate x 0.4472) — 20.4022] x Time / 4.184.
            mCalorie = 20 * 0.074 - 110 * 0.05741 + (60 * 0.4472 - 20.4022) *
                    (timeList.get(timeList.size() - 1) - timeList.get(0)) / 4184;
            mCalorieView.setText("calorie:" + (int) mCalorie);

            //update text
            mTypeView.setText(mType);

        }

    }


    //save info to database
    public void onMapSaveButtonClick(View view) {

        ExerciseEntryModel entry = new ExerciseEntryModel();

        //set type for auto
        if (isAuto) {
            sum = sum / (count + 1);
            if (sum <= 0.4) {
                mType = "standing";
            } else if (sum <= 1.0) {
                mType = "walking";
            } else {
                mType = "running";
            }
        }


        //set unit preference
        if (MainActivity.isMetric) {
            mUnit = "metric";
        } else {
            mUnit = "imperial";
            mAvgSpeed *= 0.62;
            mClimb *= 0.62;
            mDistance *= 0.62;
        }
        //set time
        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        mSecond = c.get(Calendar.SECOND);

        //set column values
        if (isAuto) {
            entry.setInputType("Auto");
        } else {
            entry.setInputType("GPS");
        }
        entry.setType(mType);
        entry.setSpeed((float) mAvgSpeed);
        entry.setClimb((float) mClimb);
        entry.setDistance((float) mDistance);
        entry.setCalories((int) mCalorie);
        String date = mHour + ":" + mMinute + ":" + mSecond
                + "  " + ManualEntryActivity.getMonth(mMonth) + " " + mDay + " " + mYear;
        entry.setDate(date);
        entry.setUnit(mUnit);
        float s = (timeList.get(timeList.size() - 1) - timeList.get(0)) / 1000;
        entry.setDuration(s / 60);

        //store location list into database
        String locations = "";
        for (int i = 0; i < locList.size(); i++) {
            Location l = locList.get(i);
            locations = locations + l.getLatitude() + "," + l.getLongitude() + ";";
        }
        entry.setLocation(locations);

        dataSource.createComment(entry);
        Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show();
        finish();
    }

    //map activity cancel
    public void onMapCancelButtonClick(View view) {
        finish();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //bindService(new Intent(this, TrackingService.class), mConnection, Context.BIND_AUTO_CREATE);

        //check user permission
//        if (!checkPermission())
//            ActivityCompat.requestPermissions(this, new String[]
//                    {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
//        else
//            startTrackingService();
    }

    //start foreground tracking service
    private void startTrackingService() {
        if (!inService) {
            serviceIntent = new Intent(this, TrackingService.class);
            serviceIntent.setAction(START_FOREGROUND);
            startForegroundService(serviceIntent);
            //startService(serviceIntent);
            inService = true;
        } else {
            serviceIntent = new Intent(this, TrackingService.class);
            serviceIntent.setAction(STOP_FOREGROUND);
            startService(serviceIntent);
        }
    }

//    private ServiceConnection mConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//
//        }
//    };

    //draw line in map
    private void drawLine() {
        rectOptions = new PolylineOptions().color(Color.RED);
        for (int i = 0; i < latlngList.size(); i++) {
            rectOptions.add(latlngList.get(i));
        }
        mMap.addPolyline(rectOptions);
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //Log.d(TAG, "onLocationChanged: ");
            //updateLocation(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            //Log.d(TAG, "onProviderDisabled: ");

        }
    };

    //when location is received, update map and marker, zoom
    private void updateLocation(Location location) {

        LatLng latlng = fromLocationToLatLng(location);

        //update marker
        if (latlngList.size() == 1) {
            start = mMap.addMarker(new MarkerOptions()
                    .position(latlng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .title("start"));

        } else {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17));

            if (whereAmI != null)
                whereAmI.remove();

            whereAmI = mMap.addMarker(new MarkerOptions()
                    .position(latlng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .title("current"));
            drawLine();
        }

    }

    //convert location to LatLng
    public static LatLng fromLocationToLatLng(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;
        else
            return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //updateMap();
            startTrackingService();
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            //finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");

        latlngList.clear();
        locList.clear();
        mCurSpeeds.clear();
        timeList.clear();
        mDistance = 0;
        mAvgSpeed = 0;
        mClimb = 0;
        mCalorie = 0;
        mCurSpeed = 0;
        polylines.clear();
        isTracking = false;
//        Intent stopIntent = new Intent(this, TrackingService.class);
//        stopIntent.setAction(STOP_FOREGROUND);
//        startService(stopIntent);

        //unbindService(mConnection);

        if (mSensorIntent != null) {
            stopService(mSensorIntent);
        }
        stopService(serviceIntent);

    }

    @Override
    protected void onResume() {
        dataSource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        dataSource.close();
        super.onPause();
    }

}
