package dartmouth.cs.qiyaozuo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static dartmouth.cs.qiyaozuo.HistoryItem.RESULT_DELETE;

//history tab
public class HistoryFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<ExerciseEntryModel>> {


    public static final String HISTORY_ITEM_POS = "history item position";
    public static final String TAG = "HistoryFragment";
    private static final int ACTIVITY_HISTORY_ITEM = 0;
    private static final int ACTIVITY_GPS_ITEM = 1;
    private static final int ALL_COMMENTS_LOADER_ID = 1;
    private ManualInputAdapter mAdapter;
    private static CommentsDataSource dataSource;
    private ListView mListView;
    private ArrayList mItems;
    public static List<ExerciseEntryModel> values;
    private int currentEntry;//selected item position


    public HistoryFragment() {
        // Required empty public constructor
    }

    // new instance of fragment
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static CommentsDataSource getData() {
        return dataSource;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = view.findViewById(R.id.list);
        //store selected item position
        if (savedInstanceState != null)
            currentEntry = savedInstanceState.getInt(HISTORY_ITEM_POS);

        //manual data source
        dataSource = new CommentsDataSource(getContext());

        if (dataSource != null) {
            dataSource.open();
            //values = dataSource.getAllComments();
            //create loader for database
            //LoaderManager.getInstance(this).initLoader(ALL_COMMENTS_LOADER_ID, null, this);
            //mLoader = LoaderManager.getInstance(this);
            //mLoader.initLoader(ALL_COMMENTS_LOADER_ID, null, this).forceLoad();
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        if (dataSource != null) {
            dataSource.open();
            //values = dataSource.getAllComments();
            //loading database
            LoaderManager.getInstance(this).initLoader(ALL_COMMENTS_LOADER_ID, null, this);
//            if (values != null) {
//                mItems = new ArrayList<>();
//                mItems.clear();
//                mAdapter = new ManualInputAdapter(getContext(), mItems);
//                mAdapter.clear();
//                mListView.setAdapter(mAdapter);
//                initialFields(values);
//
//                //create list click listener
//                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        Log.d(TAG, "onItemClick: ");
//                        ExerciseEntryModel entry = values.get(position);
//                        if (entry.getInputType().equals("manual")) {
//                            Intent myIntent = new Intent(view.getContext(), HistoryItem.class);
//                            myIntent.putExtra(HISTORY_ITEM_POS, position);
//                            currentEntry = position;
//                            startActivityForResult(myIntent, ACTIVITY_HISTORY_ITEM);
//                        } else {
//                            Intent myIntent = new Intent(view.getContext(), GpsItem.class);
//                            myIntent.putExtra(HISTORY_ITEM_POS, position);
//                            currentEntry = position;
//                            startActivityForResult(myIntent, ACTIVITY_GPS_ITEM);
//                        }
//
//                    }
//                });
//            }

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_HISTORY_ITEM || requestCode == ACTIVITY_GPS_ITEM) {
            //delete selected item
            if (resultCode == RESULT_DELETE) {
                if (dataSource != null) {
                    dataSource.open();
                    if (dataSource.deleteComment(values.get(currentEntry)))
                        Toast.makeText(getContext(), "deleted", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void initialFields(List<ExerciseEntryModel> values) {
        for (int i = 0; i < values.size(); i++) {
            //add items to list
            ExerciseEntryModel v = values.get(i);
            mItems.add(new ManualEntryModel(v.getInputType() + ":" + v.getType() + " "
                    + " " + v.getDate(),
                    v.toString(MainActivity.isMetric, v.getUnit())));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (dataSource != null)
            dataSource.close();
    }

    @NonNull
    @Override
    public Loader<List<ExerciseEntryModel>> onCreateLoader(int id, @Nullable Bundle args) {
        if (id == ALL_COMMENTS_LOADER_ID)
            return new CommentListLoader(getContext());
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<ExerciseEntryModel>> loader, List<ExerciseEntryModel> data) {
        if (loader.getId() == ALL_COMMENTS_LOADER_ID) {
            //update list adapter
            if (data.size() >= 0) {
                values = data;
                mItems = new ArrayList<>();
                mItems.clear();
                mAdapter = new ManualInputAdapter(getContext(), mItems);
                mAdapter.clear();
                mAdapter.notifyDataSetChanged();
                mListView.setAdapter(mAdapter);
                initialFields(values);

            }
            if (values != null) {
                mItems = new ArrayList<>();
                mItems.clear();
                mAdapter = new ManualInputAdapter(getContext(), mItems);
                mAdapter.clear();
                mListView.setAdapter(mAdapter);
                initialFields(values);

                //create list click listener
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ExerciseEntryModel entry = values.get(position);
                        if (entry.getInputType().equals("manual")) {
                            Intent myIntent = new Intent(view.getContext(), HistoryItem.class);
                            myIntent.putExtra(HISTORY_ITEM_POS, position);
                            currentEntry = position;
                            startActivityForResult(myIntent, ACTIVITY_HISTORY_ITEM);
                        } else {
                            Intent myIntent = new Intent(view.getContext(), GpsItem.class);
                            myIntent.putExtra(HISTORY_ITEM_POS, position);
                            currentEntry = position;
                            startActivityForResult(myIntent, ACTIVITY_GPS_ITEM);
                        }

                    }
                });
            }
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<ExerciseEntryModel>> loader) {

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //save selected item position
        outState.putInt(HISTORY_ITEM_POS, currentEntry);
    }
}