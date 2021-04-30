package dartmouth.cs.qiyaozuo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

//fragment dialog for time picker
public class TimePickerFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        int h = c.get(Calendar.HOUR_OF_DAY);
        int m = c.get(Calendar.MINUTE);
        int s = c.get(Calendar.SECOND);
        ManualEntryActivity.mHour = h;
        ManualEntryActivity.mMinute = m;
        ManualEntryActivity.mSecond = s;

        return new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                ManualEntryActivity.mHour = hourOfDay;
                ManualEntryActivity.mMinute = minute;
                ManualEntryActivity.mSecond = 00;
            }
        }, h, m, false);
    }
}
