package pl.piotrskiba.teatime

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import butterknife.BindView
import butterknife.ButterKnife
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import pl.piotrskiba.teatime.services.CountDownTimerService

class TeaTimerFragment : Fragment(), OnSeekBarChangeListener, View.OnClickListener {

    @BindView(R.id.sb_timer)
    lateinit var mTimerSeekBar: SeekBar

    @BindView(R.id.tv_seekbar_title)
    lateinit var mTimerSeekBarTitle: TextView

    @BindView(R.id.pb_timer)
    lateinit var mTimerProgressBar: CircularProgressBar

    @BindView(R.id.tv_timer)
    lateinit var mTimerTextView: TextView

    @BindView(R.id.btn_start_timer)
    lateinit var mTimerStartButton: Button

    @BindView(R.id.btn_stop_timer)
    lateinit var mTimerStopButton: Button

    @BindView(R.id.btn_disable_alarm)
    lateinit var mDisableAlarmButton: Button

    private var mTeaIndex = 0
    private var mTotalBrewingTime: Long = 0
    private var mTimerUpdateReceiver: TimerUpdateReceiver? = null

    var showAlarmLayout = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_tea_timer, container, false)

        ButterKnife.bind(this, rootView)

        if (savedInstanceState != null && savedInstanceState.containsKey(Constants.EXTRA_INDEX)) {
            mTeaIndex = savedInstanceState.getInt(Constants.EXTRA_INDEX)
        }

        return rootView
    }

    fun setTeaIndex(index: Int) {
        mTeaIndex = index
    }

    private fun populateFragment() {
        mTimerSeekBar.setOnSeekBarChangeListener(this)

        if (showAlarmLayout) {
            showAlarmLayout()
        } else if (isServiceRunning) {
            val teaId = resources.getStringArray(R.array.tea_ids)[mTeaIndex]
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

            val timeleft = sharedPreferences.getLong(getString(R.string.pref_timeleft_key, teaId), 0)
            if (timeleft > 0) {
                setDefaultSeekBarProgress()
                showBrewingLayout()
                updateTimerText(timeleft)

                mTotalBrewingTime = sharedPreferences.getLong(getString(R.string.pref_total_time_key, teaId), 1)
                val progress = (timeleft.toFloat() / mTotalBrewingTime * 1000).toInt()
                mTimerProgressBar.progress = progress.toFloat()
            } else {
                setDefaultSeekBarProgress()
            }
        } else {
            setDefaultSeekBarProgress()
        }

        mTimerStartButton.setOnClickListener(this)
        mTimerStopButton.setOnClickListener(this)
        mDisableAlarmButton.setOnClickListener(this)
    }

    private fun setDefaultSeekBarProgress() {
        val max = resources.getIntArray(R.array.tea_max_brewing_time)[mTeaIndex]
        if (max != 0) {
            val min = resources.getIntArray(R.array.tea_min_brewing_time)[mTeaIndex]
            val defVal = resources.getIntArray(R.array.tea_default_brewing_time)[mTeaIndex].toFloat()
            val sbval = ((defVal - min) / (max - min) * 100).toInt()

            mTimerSeekBar.progress = sbval
            onProgressChanged(mTimerSeekBar, mTimerSeekBar!!.progress, false)
        }
    }

    private fun getSeekBarValue(progress: Int): Int {
        val min = resources.getIntArray(R.array.tea_min_brewing_time)[mTeaIndex]
        val max = resources.getIntArray(R.array.tea_max_brewing_time)[mTeaIndex]

        var seekBarProgress = (progress.toFloat() / 100 * (max - min)).toInt()
        seekBarProgress /= 5
        seekBarProgress *= 5
        seekBarProgress += min
        return seekBarProgress * 1000
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        val milliseconds = getSeekBarValue(progress)
        updateTimerText(milliseconds.toLong())
    }

    private fun updateTimerText(milliseconds: Long) {
        var seconds = milliseconds.toInt() / 1000
        val minutes = seconds / 60
        seconds -= minutes * 60

        mTimerTextView.text = getString(R.string.timer_format, minutes, seconds)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}

    override fun onStopTrackingTouch(seekBar: SeekBar) {}

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_start_timer -> {
                startBrewing()
            }
            R.id.btn_stop_timer -> {
                cancelBrewing()
            }
            R.id.btn_disable_alarm -> {
                (context as TeaDetailsActivity?)!!.stopAlarm()
                setDefaultSeekBarProgress()
                showDefaultLayout()
            }
        }
    }

    private fun startBrewing() {
        mTotalBrewingTime = getSeekBarValue(mTimerSeekBar.progress).toLong()

        val timerService = Intent(context, CountDownTimerService::class.java)
        timerService.putExtra(Constants.EXTRA_INDEX, mTeaIndex)
        timerService.putExtra(Constants.EXTRA_MILLISECONDS, mTotalBrewingTime)
        context!!.applicationContext.startService(timerService)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context!!.applicationContext)
        val editor = sharedPreferences.edit()
        val teaId = resources.getStringArray(R.array.tea_ids)[mTeaIndex]
        editor.putLong(getString(R.string.pref_total_time_key, teaId), mTotalBrewingTime)
        editor.apply()

        showBrewingLayout()
    }

    private fun cancelBrewing() {
        context!!.applicationContext.stopService(Intent(context, CountDownTimerService::class.java))

        showDefaultLayout()

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context!!.applicationContext)
        val editor = sharedPreferences.edit()
        val teaId = resources.getStringArray(R.array.tea_ids)[mTeaIndex]
        editor.putLong(getString(R.string.pref_timeleft_key, teaId), 0)
        editor.apply()

        val seconds = getSeekBarValue(mTimerSeekBar.progress)
        updateTimerText(seconds.toLong())
    }

    private fun showBrewingLayout() {
        mTimerStartButton.visibility = View.GONE
        mTimerStopButton.visibility = View.VISIBLE
        mDisableAlarmButton.visibility = View.GONE
        mTimerSeekBar.visibility = View.INVISIBLE
        mTimerSeekBarTitle.visibility = View.INVISIBLE
    }

    private fun showDefaultLayout() {
        mTimerStartButton.visibility = View.VISIBLE
        mTimerStopButton.visibility = View.GONE
        mDisableAlarmButton.visibility = View.GONE
        mTimerSeekBar.visibility = View.VISIBLE
        mTimerSeekBarTitle.visibility = View.VISIBLE
        mTimerProgressBar.setProgressWithAnimation(1000f)
    }

    private fun showAlarmLayout() {
        updateTimerText(0)
        mTimerProgressBar.progress = 0f
        mTimerStartButton.visibility = View.GONE
        mTimerStopButton.visibility = View.GONE
        mDisableAlarmButton.visibility = View.VISIBLE
        mTimerSeekBar.visibility = View.INVISIBLE
        mTimerSeekBarTitle.visibility = View.INVISIBLE
    }

    private val isServiceRunning: Boolean
        get() {
            val manager = context!!.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                if (CountDownTimerService::class.java.name == service.service.className) {
                    return true
                }
            }
            return false
        }

    override fun onResume() {
        super.onResume()

        if (mTimerUpdateReceiver == null)
            mTimerUpdateReceiver = TimerUpdateReceiver()

        val intentFilter = IntentFilter(Constants.TIMER_UPDATE_ACTION)
        context!!.registerReceiver(mTimerUpdateReceiver, intentFilter)

        inForeground = true

        populateFragment()
    }

    override fun onPause() {
        super.onPause()

        if (mTimerUpdateReceiver != null)
            context!!.unregisterReceiver(mTimerUpdateReceiver)

        inForeground = false
    }

    private inner class TimerUpdateReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Constants.TIMER_UPDATE_ACTION) {
                val teaIndex = intent.getIntExtra(Constants.EXTRA_INDEX, -1)
                if (teaIndex == mTeaIndex) {
                    val milliseconds = intent.getLongExtra(Constants.EXTRA_MILLISECONDS, 0)

                    if (milliseconds == 0L) {
                        showAlarmLayout()
                        (getContext() as TeaDetailsActivity?)!!.startAlarm()
                    } else {
                        updateTimerText(milliseconds)
                        val progress = (milliseconds.toFloat() / mTotalBrewingTime * 1000).toInt()
                        mTimerProgressBar.progress = progress.toFloat()
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(Constants.EXTRA_INDEX, mTeaIndex)
    }

    companion object {
        @JvmField
        var inForeground = true
    }
}