package dartmouth.cs.qiyaozuo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import static dartmouth.cs.qiyaozuo.HistoryFragment.HISTORY_ITEM_POS;

//show history entry in a list view after an item is clicked
public class HistoryItem extends AppCompatActivity {

    public static final int RESULT_DELETE = 3;
    private ListView mListView;
    private ArrayList mItems;
    private ListAdapter mAdapter;
    private static CommentsDataSource dataSource;
    private int id;//position of item selected


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_item);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initialize list view
        mListView = findViewById(R.id.history_list_view);
        mItems = new ArrayList<>();
        //mItems.clear();

        mAdapter = new ManualInputAdapter(this, mItems);
        //mAdapter.clear();
        id = getIntent().getExtras().getInt(HISTORY_ITEM_POS);
        initialFields(id);
        mListView.setAdapter(mAdapter);

    }

    //initialize list values
    private void initialFields(int id) {
        ExerciseEntryModel entry = HistoryFragment.values.get(id);

        mItems.add(new ManualEntryModel("Input Type", "Manual Entry"));
        mItems.add(new ManualEntryModel("Activity Type",
                entry.getType()));

        //convert time information to string
        mItems.add(new ManualEntryModel("Date and time",
                entry.getDate()));
        float duration = entry.getDuration();
        int min = (int) duration;
        int sec = (int) ((duration - (float) min) * 60);

        //convert distance float to string
        mItems.add(new ManualEntryModel("Duration",
                min + " minutes "
                        + sec + " secs"));
        String distanceString = "";
        if (MainActivity.isMetric) {
            //is kms
            if (entry.getUnit().equals("imperial")) {
                distanceString = entry.getDistance() * 1.6 + " KM";
            } else {
                distanceString = entry.getDistance() + " KM";
            }
        } else {
            //is miles
            if (MainActivity.isMetric.equals("metric")) {
                distanceString = entry.getDistance() / 1.6 + " Miles";
            } else {
                distanceString = entry.getDistance() + " Miles";
            }
        }


        mItems.add(new ManualEntryModel("Distance", distanceString));
        mItems.add(new ManualEntryModel("Calories",
                Integer.toString(entry.getCalories()) + " cals"));
        mItems.add(new ManualEntryModel("Heart Rate",
                Integer.toString(entry.getHeartRate()) + " bpm"));
        mItems.add(new ManualEntryModel("Comment",
                entry.getComment()));

    }

    //if delete button is clicked
    public void onDeleteClick(View view) {
        Intent returnIntent = new Intent();
        setResult(RESULT_DELETE, returnIntent);
        finish();
    }
}