package pl.piotrskiba.teatime;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TeaDetailsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.pager)
    ViewPager mViewPager;

    @BindView(R.id.tabs)
    TabLayout mTabLayout;

    int mTeaIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tea_details);

        ButterKnife.bind(this);

        this.setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        TeaInfoFragment teaInfoFragment = new TeaInfoFragment();
        TeaTimerFragment teaTimerFragment = new TeaTimerFragment();

        Intent parentIntent = getIntent();
        if(parentIntent.hasExtra(Constants.EXTRA_INDEX)){
            mTeaIndex = parentIntent.getIntExtra(Constants.EXTRA_INDEX, -1);
            teaInfoFragment.setTeaIndex(mTeaIndex);
            teaTimerFragment.setTeaIndex(mTeaIndex);

            populateUi();
        }

        adapter.addFragment(teaInfoFragment, getString(R.string.tab_info));

        int max = getResources().getIntArray(R.array.tea_max_brewing_time)[mTeaIndex];
        if(max != 0) {
            adapter.addFragment(teaTimerFragment, getString(R.string.tab_timer));
        }

        mViewPager.setAdapter(adapter);

        mTabLayout.setupWithViewPager(mViewPager);

        if(parentIntent.hasExtra(Constants.EXTRA_OPEN_TIMER)) {
            mViewPager.setCurrentItem(1);
        }
    }

    private void populateUi(){
        String tea_name = getResources().getStringArray(R.array.tea_names)[mTeaIndex];

        getSupportActionBar().setTitle(tea_name);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
