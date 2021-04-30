package dartmouth.cs.qiyaozuo;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

//the main activity
public class MainActivity extends AppCompatActivity {

    private static final String KEY_TAB_POSITION = "tab index";
    private TextView mTextView;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private int mPosition = 0;
    public static Boolean isMetric = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = findViewById(R.id.view_pager);
        PageAdapter adapter = new PageAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mViewPager.setAdapter(adapter);

        //initialize UI element
        mTabLayout = findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setText("start");
        mTabLayout.getTabAt(1).setText("history");
        mTabLayout.getTabAt(2).setText("settings");


    }

}