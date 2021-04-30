package dartmouth.cs.qiyaozuo;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PageAdapter extends FragmentPagerAdapter {
    public PageAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        if (position == 0) {
            fragment = StartFragment.newInstance(String.valueOf(position));

        } else if (position == 1) {
            fragment = HistoryFragment.newInstance("your", "history");
        } else {
            //fragment = StartFragment.newInstance(String.valueOf(position));
            fragment = new SettingsFragment();

        }


        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

}
