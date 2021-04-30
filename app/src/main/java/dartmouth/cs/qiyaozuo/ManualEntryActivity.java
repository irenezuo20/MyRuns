package dartmouth.cs.qiyaozuo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Calendar;

import static dartmouth.cs.qiyaozuo.MyDialogFragment.DIALOG_ID_INPUT;
import static dartmouth.cs.qiyaozuo.StartFragment.ACTIVITY_TYPE;

//this activity take user input manually and store entries in database
public class ManualEntryActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_DIALOG_TEXT = 3;
    private static final String TAG = "TAG";
    private ListView mListView;
    private ArrayList mItems;
    private ListAdapter mAdapter;
    private long mDate;
    private CommentsDataSource dataSource;
    private int nextInt;
    public static float mDistance, mDuration = 0.f;
    public static String mComment, mType, mUnit = "";
    public static int mHour, mMinute, mSecond, mYear, mMonth, mDay, mHeartRate, mCalories = 0;
    private boolean datePicked, timePicked = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_entry);

        //initialize list view
        mType = getIntent().getExtras().getString(ACTIVITY_TYPE, "unknown");

        mListView = findViewById(R.id.list_view_id);
        mItems = new ArrayList<>();
        //mItems.clear();

        mAdapter = new ManualInputAdapter(this, mItems);
        //mAdapter.clear();
        mListView.setAdapter(mAdapter);
        initialFields();

        //when list item is clicked
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case (0):
                        getDatePicker();
                        datePicked = true;
                        break;
                    case (1):
                        getTimePicker();
                        timePicked = true;
                        break;
                    default:
                        getInputDialog(position);
                }
            }
        });

        //initialize database
        dataSource = HistoryFragment.getData();
    }

    //set fields to default values
    private void resetValue() {
        datePicked = false;
        timePicked = false;
        mDistance = 0;
        mDuration = 0;
        mHour = 0;
        mMinute = 0;
        mSecond = 0;
        mYear = 0;
        mMonth = 0;
        mDay = 0;
        mHeartRate = 0;
        mCalories = 0;
        mType = "";
        mComment = "";
    }

    //get time picker fragment
    private void getTimePicker() {
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "");
    }

    //get date picker fragment
    private void getDatePicker() {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "");
    }

    //get general input dialog fragment
    private void getInputDialog(int position) {
        ManualEntryModel item = (ManualEntryModel) mItems.get(position);
        MyDialogFragment alert = MyDialogFragment.newInstance(DIALOG_ID_INPUT, item.getTitle());
        alert.show(getSupportFragmentManager(), getString(R.string.tag_dialog_frag_input));
    }

    //add items to list view
    private void initialFields() {
        mItems.add(new ManualEntryModel("Date", ""));
        mItems.add(new ManualEntryModel("Time", ""));
        mItems.add(new ManualEntryModel("Duration", ""));
        mItems.add(new ManualEntryModel("Distance", ""));
        mItems.add(new ManualEntryModel("Calories", ""));
        mItems.add(new ManualEntryModel("Heart Rate", ""));
        mItems.add(new ManualEntryModel("Comment", ""));
    }

    //when save button is clicked
    public void onSaveButtonClickManual(View view) {
        //get current time if not set by user
        Calendar c = Calendar.getInstance();
        if (!datePicked) {
            //when no date picked
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
        }
        if (!timePicked) {
            //if no time picked
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);
            mSecond = c.get(Calendar.SECOND);
        }

        ExerciseEntryModel entry = new ExerciseEntryModel();
        entry.setComment(mComment);

        //set unit preference
        if (MainActivity.isMetric) {
            mUnit = "metric";
        } else {
            mUnit = "imperial";
        }

        //set column values
        entry.setInputType("manual");
        entry.setUnit(mUnit);
        entry.setDistance(mDistance);
        entry.setHeartRate(mHeartRate);
        entry.setDuration(mDuration);
        entry.setCalories(mCalories);
        entry.setType(mType);
        String date = mHour + ":" + mMinute + ":" + mSecond
                + "  " + getMonth(mMonth) + " " + mDay + " " + mYear;
        entry.setDate(date);
        //add entry to database
        dataSource.createComment(entry);
        Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show();
        finish();
    }

    //return if canceled
    public void onCancelButtonClickManual(View view) {
        finish();
        Toast.makeText(this, "canceled", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        resetValue();
    }

    //convert int to month
    public static String getMonth(int month) {
        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return monthNames[month];
    }
}
