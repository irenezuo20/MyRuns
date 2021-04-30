package dartmouth.cs.qiyaozuo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class Preference {
    private final SharedPreferences sharedPreferences;

    public Preference(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void clearProfile() {
        sharedPreferences.edit().remove("name").apply();
        sharedPreferences.edit().remove("email").apply();
        sharedPreferences.edit().remove("class").apply();
        sharedPreferences.edit().remove("phone").apply();
        sharedPreferences.edit().remove("major").apply();
        sharedPreferences.edit().remove("gender").apply();
        sharedPreferences.edit().remove("picture").apply();

    }

    public String getEmail() {
        return sharedPreferences.getString("email", "");
    }

    public void setEmail(String email) {
        sharedPreferences.edit().putString("email", email).apply();
    }

    public String getName() {
        return sharedPreferences.getString("name", "");
    }

    public void setName(String name) {
        sharedPreferences.edit().putString("name", name).apply();
    }

    public String getPhone() {
        return sharedPreferences.getString("phone", "");
    }

    public void setPhone(String phone) {
        sharedPreferences.edit().putString("phone", phone).apply();
    }

    public String getDartClass() {
        return sharedPreferences.getString("class", "");
    }

    public void setDartClass(String dartClass) {
        sharedPreferences.edit().putString("class", dartClass).apply();
    }

    public void setProfilePic(String path) {
        sharedPreferences.edit().putString("picture", path).apply();
    }

    public String getProfilePic() {
        return sharedPreferences.getString("picture", "");
    }


    public void setGender(int checkedRadioButtonId) {
        sharedPreferences.edit().putInt("gender", checkedRadioButtonId).apply();
    }

    public int getGenderId() {
        return sharedPreferences.getInt("gender", 0);
    }

    public String getMajor() {
        return sharedPreferences.getString("major", "");
    }

    public void setMajor(String major) {
        sharedPreferences.edit().putString("major", major).apply();
    }
}
