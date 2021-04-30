package dartmouth.cs.qiyaozuo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import java.util.Calendar;


public class DatePickerFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //set date to current date
        Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH);
        int d = c.get(Calendar.DAY_OF_MONTH);
        ManualEntryActivity.mYear = y;
        ManualEntryActivity.mMonth = m;
        ManualEntryActivity.mDay = d;

        return new DatePickerDialog(getContext(), mDateSetListener, y, m, d);

    }

    //if a date is picked
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        // onDateSet method
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            ManualEntryActivity.mDay = dayOfMonth;
            ManualEntryActivity.mMonth = monthOfYear;
            ManualEntryActivity.mYear = year;
        }
    };


}