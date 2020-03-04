package pl.piotrskiba.teatime

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.preference.PreferenceManager
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.tabs.TabLayout
import java.util.*

class TeaDetailsActivity : AppCompatActivity() {

    @BindView(R.id.toolbar)
    lateinit var mToolbar: Toolbar

    @BindView(R.id.pager)
    lateinit var mViewPager: ViewPager

    @BindView(R.id.tabs)
    lateinit var mTabLayout: TabLayout

    private var mTeaIndex = 0
    private lateinit var mTeaTimerFragment: TeaTimerFragment
    private lateinit var mVibrator: Vibrator
    private lateinit var mMediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_tea_details)

        ButterKnife.bind(this)

        mTeaIndex = intent.getIntExtra(Constants.EXTRA_INDEX, -1)

        setupActionBar()
        setupViewPager()

        if (intent.hasExtra(Constants.EXTRA_OPEN_TIMER)) {
            mViewPager.currentItem = 1
        }
        if (intent.hasExtra(Constants.EXTRA_START_ALARM)) {
            startAlarm()
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(mToolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val teaName = resources.getStringArray(R.array.tea_names)[mTeaIndex]
        supportActionBar?.title = teaName
    }

    private fun setupViewPager() {
        val teaInfoFragment = TeaInfoFragment()
        teaInfoFragment.setTeaIndex(mTeaIndex)

        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(teaInfoFragment, getString(R.string.tab_info))

        val max = resources.getIntArray(R.array.tea_max_brewing_time)[mTeaIndex]
        if (max != 0) {
            mTeaTimerFragment = TeaTimerFragment()
            mTeaTimerFragment.setTeaIndex(mTeaIndex)
            adapter.addFragment(mTeaTimerFragment, getString(R.string.tab_timer))
        }

        mViewPager.adapter = adapter
        mTabLayout.setupWithViewPager(mViewPager)
    }

    fun startAlarm() {
        mVibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        setWindowFlags()
        startVibrator()
        startMediaPlayer()

        mTeaTimerFragment.showAlarmLayout = true
    }

    fun stopAlarm() {
        mVibrator.cancel()

        if (mMediaPlayer.isPlaying)
            mMediaPlayer.stop()

        val wnd = window
        wnd.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun setWindowFlags() {
        val wnd = window
        wnd.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        wnd.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        wnd.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
    }

    private fun startVibrator() {
        if (mVibrator.hasVibrator()) {
            val pattern = longArrayOf(0, 500, 500)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mVibrator.vibrate(VibrationEffect.createWaveform(pattern, 0))
            } else {
                mVibrator.vibrate(pattern, 0)
            }
        }
    }

    private fun startMediaPlayer() {
        mMediaPlayer = MediaPlayer()

        try {
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            mMediaPlayer.setDataSource(this, uri)

            val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

            var stream = AudioManager.STREAM_NOTIFICATION
            if (sharedPreferences.getBoolean(getString(R.string.pref_play_while_silent_key), true))
                stream = AudioManager.STREAM_ALARM

            if (audioManager.getStreamVolume(stream) != 0) {
                mMediaPlayer.setAudioStreamType(stream)
                mMediaPlayer.isLooping = true
                mMediaPlayer.prepare()
                mMediaPlayer.start()
            }
        } catch (e: Exception) {
            Log.e(this.javaClass.name, "Can't start media player")
        }
    }

    internal inner class ViewPagerAdapter(manager: FragmentManager?) : FragmentPagerAdapter(manager!!) {

        private val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList: MutableList<String> = ArrayList()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }
}