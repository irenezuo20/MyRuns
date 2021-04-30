package dartmouth.cs.qiyaozuo;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

/**
 * the first tab start fragment
 */
public class StartFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PAGE_NUMBER = "page_number";
    public static final String ACTIVITY_TYPE = "activity type";
    private static final String TAG = "startfragment";
    private Spinner mInputSpinner;
    private Spinner mActivitySpinner;
    private Button mButtonStart;
    private String[] activities;
    private String position;

    public StartFragment() {
        // Required empty public constructor
    }


    public static StartFragment newInstance(String position) {
        StartFragment fragment = new StartFragment();
        Bundle args = new Bundle();
        //save page number
        args.putString(PAGE_NUMBER, position);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getString(PAGE_NUMBER);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_start, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createInputSpinner(view);
        createActivitySpinner(view);

        mButtonStart = view.findViewById(R.id.button_start1);

        //if start button is clicked
        mButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                int pos;
                switch (v.getId()) {
                    //manual entry activity
                    case R.id.button_start1:
                        switch (mInputSpinner.getSelectedItemPosition()) {
                            case 0://manual
                                pos = mActivitySpinner.getSelectedItemPosition();
                                intent = new Intent(getContext(), ManualEntryActivity.class);
                                intent.putExtra(ACTIVITY_TYPE, activities[pos]);
                                break;

                            case 1://GPS activity
                                pos = mActivitySpinner.getSelectedItemPosition();
                                intent = new Intent(getContext(), MapActivity.class);
                                intent.putExtra(ACTIVITY_TYPE, activities[pos]);
                                Log.d(TAG, "onClick: " + activities[pos]);
                                break;
                            case 2://auto activity
                                intent = new Intent(getContext(), MapActivity.class);
                                intent.putExtra(ACTIVITY_TYPE, "auto");
                                break;

                        }
                }
                if (intent != null)
                    startActivity(intent);
            }

        });

    }

    //input type spinner
    private void createInputSpinner(View view) {
        mInputSpinner = view.findViewById(R.id.spinner_input_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.input_type_array, android.R.layout.simple_spinner_item);
        //set dropdown values
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mInputSpinner.setAdapter(adapter);
    }

    //activity type spinner
    private void createActivitySpinner(View view) {
        mActivitySpinner = view.findViewById(R.id.spinner_activity_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.act_type_array, android.R.layout.simple_spinner_item);
        //set dropdown values
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mActivitySpinner.setAdapter(adapter);
        activities = getResources().getStringArray(R.array.act_type_array);

    }


}