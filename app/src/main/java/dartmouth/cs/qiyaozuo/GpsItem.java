package dartmouth.cs.qiyaozuo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;

import static dartmouth.cs.qiyaozuo.HistoryFragment.HISTORY_ITEM_POS;
import static dartmouth.cs.qiyaozuo.HistoryItem.RESULT_DELETE;

//display history GPS entry
public class GpsItem extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "GPSITEM";
    private int id;
    private TextView mCurSpeedView, mSpeedView, mClimbView, mCalorieView, mTypeView, mDistanceView;
    private String mCurSpeed, mSpeed, mClimb, mCalorie, mType, mDistance;
    private String[] locList;
    private PolylineOptions rectOptions;
    private GoogleMap mMap;
    private Marker start;
    private Marker end;
    DecimalFormat df = new DecimalFormat("0.0000");
    DecimalFormat df1 = new DecimalFormat("0.000000");


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_item);
        id = getIntent().getExtras().getInt(HISTORY_ITEM_POS);
        mCurSpeedView = findViewById(R.id.text_cur_speed_his);
        mSpeedView = findViewById(R.id.text_avg_speed_his);
        mClimbView = findViewById(R.id.text_climb_his);
        mCalorieView = findViewById(R.id.text_calorie_his);
        mTypeView = findViewById(R.id.text_type_his);
        mDistanceView = findViewById(R.id.text_distance_his);

        mCurSpeedView.setText("unknown");

        initialFields(id);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_history);
        mapFragment.getMapAsync(this);

    }

    //restore route from database
    private void drawRoute(int id) {
        ExerciseEntryModel entry = HistoryFragment.values.get(id);
        String locations = entry.getLocation();
        locList = locations.split(";");
        rectOptions = new PolylineOptions().color(Color.RED);

        //iterate through location list and draw segment
        for (int i = 0; i < locList.length; i++) {
            String[] l = locList[i].split(",");
            LatLng latLng = new LatLng(Double.parseDouble(l[0]), Double.parseDouble(l[1]));
            rectOptions.add(latLng);
            if (i == 0) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                start = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .title("start"));
            } else if (i == locList.length - 1) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                end = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        .title("end"));
            }
        }
        mMap.addPolyline(rectOptions);
    }

    //initial text fields on map
    private void initialFields(int id) {
        ExerciseEntryModel entry = HistoryFragment.values.get(id);

        if (MainActivity.isMetric) {
            //is kms
            if (entry.getUnit().equals("imperial")) {
                mDistance = df.format(entry.getDistance() * 1.6) + " KM";
                mSpeed = df1.format(entry.getSpeed() * 1.6) + " KM/h";
                mClimb = df.format(entry.getClimb() * 1.6) + " KM";

            } else {
                mDistance = df.format(entry.getDistance()) + " KM";
                mSpeed = df1.format(entry.getSpeed()) + " KM/h";
                mClimb = df.format(entry.getClimb()) + " KM";

            }
        } else {
            //is miles
            if (entry.getUnit().equals("metric")) {
                mDistance = df.format(entry.getDistance() / 1.6) + " Miles";
                mSpeed = df1.format(entry.getSpeed() / 1.6) + " Miles/h";
                mClimb = df.format(entry.getClimb() / 1.6) + " Miles";
            } else {
                mDistance = df.format(entry.getDistance()) + " Miles";
                mSpeed = df1.format(entry.getSpeed()) + " Miles/h";
                mClimb = df.format(entry.getClimb()) + " Miles";
            }
        }

        mSpeedView.setText("avg speed: " + mSpeed);
        mClimbView.setText("climb: " + mClimb);
        mCalorieView.setText("calorie: " + Integer.toString((int) entry.getCalories()));
        mTypeView.setText("type: " + entry.getType());
        mDistanceView.setText("distance: " + mDistance);
    }

    //delete GPS entry item
    public void onDeleteClick(View view) {
        Intent returnIntent = new Intent();
        setResult(RESULT_DELETE, returnIntent);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d(TAG, "onMapReady: ");
        drawRoute(id);
    }
}
