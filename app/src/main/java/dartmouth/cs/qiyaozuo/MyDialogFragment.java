package dartmouth.cs.qiyaozuo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class MyDialogFragment extends DialogFragment {
    private static final String KEY_DIALOG_ID = "dialog_id";
    public static final int DIALOG_ID_PHOTO_PICK = 1;
    public static final int ID_PHOTO_PICKER_CAMERA = 0;
    public static final int ID_PHOTO_FROM_LIBRARY = 1;
    public static final int DIALOG_ID_INPUT = 2;
    private static final String KEY_DIALOG_TITLE = "dialog_title";

    public static MyDialogFragment newInstance(int id, String title) {

        Bundle args = new Bundle();

        MyDialogFragment fragment = new MyDialogFragment();
        args.putInt(KEY_DIALOG_ID, id);
        args.putString(KEY_DIALOG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        int id = getArguments().getInt(KEY_DIALOG_ID);

        //pick photo from storage or camera
        if (id == DIALOG_ID_PHOTO_PICK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.dialog_photo_picker_title);
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((ProfileActivity) getActivity()).onPhotoPickerItemSelected(which);
                }
            };
            //create dialog
            builder.setItems(R.array.dialog_img_picker_items, listener);
            return builder.create();

        } else if (id == DIALOG_ID_INPUT) {
            //input dialog in manual entry activity
            String title = getArguments().getString(KEY_DIALOG_TITLE);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(title);
            LayoutInflater inflater = getActivity().getLayoutInflater();

            //set layout according to title
            View content;
            EditText mEditText;
            //for text input dialog
            if (title.equals("Comment")) {
                content = inflater.inflate(R.layout.dialog_fragment_text, null);
                mEditText = content.findViewById(R.id.dialog_input_text);
            } else if (title.equals("Duration") || title.equals("Distance")) {
                //decimal number input dialog
                content = inflater.inflate(R.layout.dialog_fragment_decimal, null);
                mEditText = content.findViewById(R.id.dialog_input_decimal);
            } else {
                //number input dialog
                content = inflater.inflate(R.layout.dialog_fragment_int, null);
                mEditText = content.findViewById(R.id.dialog_input_int);
            }

            builder.setView(content);

            //when ok button is clicked
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setEntryValue(title, mEditText);
                    return;
                }
            });
            //when cancel button is clicked
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            return builder.create();
        } else {
            return null;
        }
    }

    //save edit text value to database if ok button is clicked
    private void setEntryValue(String title, EditText data) {
        String input = String.valueOf(data.getText());
        switch (title) {
            case "Comment":
                ManualEntryActivity.mComment = input;
                break;
            case "Distance":
                ManualEntryActivity.mDistance = Float.parseFloat(input);
                break;
            case "Calories":
                ManualEntryActivity.mCalories = Integer.parseInt(input);
                break;
            case "Duration":
                ManualEntryActivity.mDuration = Float.parseFloat(input);
                break;
            case "Heart Rate":
                ManualEntryActivity.mHeartRate = Integer.parseInt(input);
                break;
            default:
                break;
        }

    }


}


