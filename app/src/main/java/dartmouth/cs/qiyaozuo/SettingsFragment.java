package dartmouth.cs.qiyaozuo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        //if cs dartmouth web page is clicked
        Preference webPage = findPreference("page");
        webPage.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse("https://web.cs.dartmouth.edu"));
                startActivity(browse);
                return true;
            }
        });

        //if profile activity is clicked
        Preference profile = findPreference("profile");
        profile.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent profileActivity = new Intent(getContext(), ProfileActivity.class);

                startActivity(profileActivity);
                return true;
            }
        });

        //if unit preference is changed
        ListPreference unit = findPreference("unit");
        if (unit.getValue().equals("imperial")) {
            MainActivity.isMetric = false;
        }

        unit.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue.toString().equals("imperial")) {
                    MainActivity.isMetric = false;
                } else {
                    MainActivity.isMetric = true;
                }
                return true;
            }
        });
    }

}