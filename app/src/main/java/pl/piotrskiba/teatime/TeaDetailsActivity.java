package pl.piotrskiba.teatime;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
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
import android.view.Window;
import android.view.WindowManager;

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

    private int mTeaIndex;

    private TeaTimerFragment mTeaTimerFragment;

    private Vibrator mVibrator;
    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tea_details);

        ButterKnife.bind(this);

        Intent parentIntent = getIntent();
        mTeaIndex = parentIntent.getIntExtra(Constants.EXTRA_INDEX, -1);

        setupActionBar();

        setupViewPager();

        if(parentIntent.hasExtra(Constants.EXTRA_OPEN_TIMER)) {
            mViewPager.setCurrentItem(1);
        }

        if(parentIntent.hasExtra(Constants.EXTRA_START_ALARM)) {
            startAlarm();
        }
    }

    private void setupActionBar(){
        this.setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        String tea_name = getResources().getStringArray(R.array.tea_names)[mTeaIndex];
        getSupportActionBar().setTitle(tea_name);
    }

    private void setupViewPager(){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        TeaInfoFragment teaInfoFragment = new TeaInfoFragment();
        teaInfoFragment.setTeaIndex(mTeaIndex);
        adapter.addFragment(teaInfoFragment, getString(R.string.tab_info));

        int max = getResources().getIntArray(R.array.tea_max_brewing_time)[mTeaIndex];
        if(max != 0) {
            mTeaTimerFragment = new TeaTimerFragment();
            mTeaTimerFragment.setTeaIndex(mTeaIndex);
            adapter.addFragment(mTeaTimerFragment, getString(R.string.tab_timer));
        }

        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    public void startAlarm(){
        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        setWindowFlags();
        startVibrator();
        startMediaPlayer();

        mTeaTimerFragment.showAlarmLayout = true;
    }

    public void stopAlarm(){
        mVibrator.cancel();
        if(mMediaPlayer.isPlaying())
            mMediaPlayer.stop();

        Window wnd = getWindow();
        wnd.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void setWindowFlags(){
        Window wnd = getWindow();
        wnd.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        wnd.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        wnd.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    private void startVibrator(){
        if(mVibrator.hasVibrator()) {
            long[] pattern = new long[]{0, 500, 500};

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mVibrator.vibrate(VibrationEffect.createWaveform(pattern, 0));
            } else {
                mVibrator.vibrate(pattern, 0);
            }
        }
    }

    private void startMediaPlayer(){
        mMediaPlayer = new MediaPlayer();

        try {
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            mMediaPlayer.setDataSource(this, uri);

            final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            int stream = AudioManager.STREAM_NOTIFICATION;
            if(sharedPreferences.getBoolean(getString(R.string.pref_play_while_silent_key), true))
                stream = AudioManager.STREAM_ALARM;

            if (audioManager.getStreamVolume(stream) != 0) {
                mMediaPlayer.setAudioStreamType(stream);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        }
        catch(Exception e){
            Log.e(this.getClass().getName(), "Can't start media player");
        }
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
