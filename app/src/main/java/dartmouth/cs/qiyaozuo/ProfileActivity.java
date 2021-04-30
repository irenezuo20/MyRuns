package dartmouth.cs.qiyaozuo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    private static final int REQUEST_TAKE_PIC_FROM_CAMERA = 0;
    private static final int PERMISSION_REQUEST = 0;
    private static final String KEY_NAME = "name";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_CLASS = "class";
    private static final String KEY_PROFILE_URI = "PIC_CROP_URI";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_MAJOR = "major";
    private static final int REQUEST_SELECT_PIC_FROM_LIBRARY = 1;
    private Uri mCroppedUri;
    private ImageView mImageView;
    private EditText mNameView, mEmailView, mPhoneView, mClassView, mMajorView;
    //private File mImageFile;
    private Preference preference;
    private String mPicPath = "null";//abs path of profile pic
    private File photoFile = null;
    private RadioGroup mGenderView;
    //private RadioButton mGenderButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //set UI elements
        mImageView = findViewById(R.id.image_profile);
        mNameView = findViewById(R.id.edit_name);
        mEmailView = findViewById(R.id.edit_email);
        mPhoneView = findViewById(R.id.edit_phone);
        mClassView = findViewById(R.id.edit_class);
        mGenderView = findViewById(R.id.radio_gender);
        mMajorView = findViewById(R.id.edit_major);

        //restore value from shared preferences
        preference = new Preference(this);
        mClassView.setText(preference.getDartClass());
        mNameView.setText(preference.getName());
        mEmailView.setText(preference.getEmail());
        mPhoneView.setText(preference.getPhone());
        mGenderView.check(preference.getGenderId());
        mMajorView.setText(preference.getMajor());


        //check permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA}, PERMISSION_REQUEST);
        }
        loadSnap();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater i = getMenuInflater();
        i.inflate(R.menu.menu, menu);
        return true;
    }


    /*load profile photo from storage*/
    private void loadSnap() {
        try {
            FileInputStream fIn = openFileInput(preference.getProfilePic());
            Bitmap profileImg = BitmapFactory.decodeStream(fIn);
            mImageView.setImageBitmap(profileImg);
            fIn.close();
        } catch (IOException e) {
            mImageView.setImageResource(R.drawable.profile_default);
        }
    }

    //request permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED
                || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("permission required");
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA}, PERMISSION_REQUEST);
                }
            });
            builder.show();
        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_NAME, String.valueOf(mNameView.getText()));
        outState.putString(KEY_PHONE, String.valueOf(mPhoneView.getText()));
        outState.putString(KEY_EMAIL, String.valueOf(mEmailView.getText()));
        outState.putString(KEY_CLASS, String.valueOf(mClassView.getText()));
        outState.putInt(KEY_GENDER, mGenderView.getCheckedRadioButtonId());
        outState.putString(KEY_MAJOR, String.valueOf(mMajorView.getText()));
        if (mCroppedUri != null)
            outState.putParcelable(KEY_PROFILE_URI, mCroppedUri);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            mPhoneView.setText(savedInstanceState.getString(KEY_PHONE));
            mMajorView.setText(savedInstanceState.getString(KEY_MAJOR));
            mEmailView.setText(savedInstanceState.getString(KEY_EMAIL));
            mClassView.setText(savedInstanceState.getString(KEY_CLASS));
            mGenderView.check(savedInstanceState.getInt(KEY_GENDER));
            mCroppedUri = savedInstanceState.getParcelable(KEY_PROFILE_URI);


            if (mCroppedUri != null) {
                try {
                    setProfileImageView();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    //set bitmap to image view
    private void setProfileImageView() throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mCroppedUri);
        mImageView.setImageBitmap(bitmap);
    }

    //create a new image file
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("MMddHHmmss").format(new Date());
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(timeStamp, ".jpg", dir);
        mPicPath = image.getAbsolutePath();
        return image;
    }

    //save entries in profile
    private void saveProfile() {
        String name = mNameView.getText().toString();
        String email = mEmailView.getText().toString();
        String dartClass = mClassView.getText().toString();
        String phone = mPhoneView.getText().toString();
        String major = mMajorView.getText().toString();

        preference.clearProfile();
        preference.setEmail(email);
        preference.setDartClass(dartClass);
        preference.setPhone(phone);
        preference.setName(name);
        preference.setGender(mGenderView.getCheckedRadioButtonId());
        preference.setMajor(major);

//        if (mPicPath.equalsIgnoreCase("null"))
//            preference.setProfilePic(mPicPath);
    }

    //change profile photo
    public void onChangeButtonClick(View view) {
        displayDialog(MyDialogFragment.DIALOG_ID_PHOTO_PICK);
    }

    //show photo picking dialog
    public void displayDialog(int id) {
        DialogFragment frag = MyDialogFragment.newInstance(id, "");
        frag.show(getSupportFragmentManager(), getString(R.string.tag_dialog_frag_photo_picker));
    }

    //select photo picker
    public void onPhotoPickerItemSelected(int item) {
        //if take pictures from camera
        if (item == MyDialogFragment.ID_PHOTO_PICKER_CAMERA) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //get Uri
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID, photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            }
            //invoke activity: take pictures from camera
            try {
                startActivityForResult(intent, REQUEST_TAKE_PIC_FROM_CAMERA);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        } else if (item == MyDialogFragment.ID_PHOTO_FROM_LIBRARY) {
            //pick photo from library
            Intent intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID, photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            }
            //intent.putExtra("return_data", true);
            try {
                startActivityForResult(intent, REQUEST_SELECT_PIC_FROM_LIBRARY);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //Log.d("crash", "onActivityResult: ");
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            //back from taking pictures
            case REQUEST_TAKE_PIC_FROM_CAMERA:
                Bitmap photo = null;
                Uri mUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, photoFile);

                try {
                    photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    FileOutputStream fOut = new FileOutputStream(photoFile);
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                    fOut.flush();
                    fOut.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                //start cropping
                beginCrop(mUri);
                break;

            //back from cropping
            case Crop.REQUEST_CROP:
                handleCrop(resultCode, data);
                break;

            //back from selecting photos from library
            case REQUEST_SELECT_PIC_FROM_LIBRARY:
                beginCrop(data.getData());
                break;

        }

    }

    //crop images
    private void beginCrop(Uri source) {
        if (photoFile != null) {
            Uri des = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, photoFile);
            Crop.of(source, des).asSquare().start(this);
        }
    }

    //crop image handler
    private void handleCrop(int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            Uri croppedUri = Crop.getOutput(intent);
            try {
                Bitmap img = MediaStore.Images.Media.getBitmap(this.getContentResolver(), croppedUri);
                mImageView.setImageBitmap(img);
                mCroppedUri = croppedUri;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, "crop error", Toast.LENGTH_SHORT).show();
        }
    }

    //save profile
    public void onSaveButtonClick(View view) throws IOException {
        saveProfile();
        mImageView.buildDrawingCache();
        Bitmap img = mImageView.getDrawingCache();
        try {
            String fileName = "profile.png";
            FileOutputStream fOut = openFileOutput(fileName, MODE_PRIVATE);
            preference.setProfilePic(fileName);
            img.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finish();
        Toast.makeText(this, "save success", Toast.LENGTH_SHORT).show();

    }

    public void onRadioButtonClicked(View view) {
//        boolean checked = ((RadioButton) view).isChecked();
//        if (checked) {
//            selectedId = mGenderView.getCheckedRadioButtonId();
//        }
    }

    public void onCancelButtonClick(View view) {
        finish();
    }
}