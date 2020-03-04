package pl.piotrskiba.teatime.services

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import pl.piotrskiba.teatime.Constants
import pl.piotrskiba.teatime.R
import pl.piotrskiba.teatime.TeaDetailsActivity
import pl.piotrskiba.teatime.TeaTimerFragment

class CountDownTimerService : Service() {

    private var mTeaIndex = -1
    private lateinit var mCountDownTimer: CountDownTimer

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mTeaIndex = intent.getIntExtra(Constants.EXTRA_INDEX, -1)
        val milliseconds = intent.getLongExtra(Constants.EXTRA_MILLISECONDS, 0)

        mCountDownTimer = object : CountDownTimer(milliseconds, 100) {
            override fun onTick(millisUntilFinished: Long) {
                val timerUpdate = Intent(Constants.TIMER_UPDATE_ACTION)
                timerUpdate.putExtra(Constants.EXTRA_INDEX, mTeaIndex)
                timerUpdate.putExtra(Constants.EXTRA_MILLISECONDS, millisUntilFinished)
                sendBroadcast(timerUpdate)

                val teaName = resources.getStringArray(R.array.tea_names)[mTeaIndex]
                createNotification(teaName, millisUntilFinished)

                val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                val editor = sharedPreferences.edit()

                val teaId = resources.getStringArray(R.array.tea_ids)[mTeaIndex]
                editor.putLong(getString(R.string.pref_timeleft_key, teaId), millisUntilFinished)
                editor.apply()
            }

            override fun onFinish() {
                val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                val editor = sharedPreferences.edit()

                val teaId = resources.getStringArray(R.array.tea_ids)[mTeaIndex]
                editor.putLong(getString(R.string.pref_timeleft_key, teaId), 0)
                editor.apply()

                if (TeaTimerFragment.inForeground) {
                    val timerUpdate = Intent(Constants.TIMER_UPDATE_ACTION)
                    timerUpdate.putExtra(Constants.EXTRA_INDEX, mTeaIndex)
                    timerUpdate.putExtra(Constants.EXTRA_MILLISECONDS, 0)

                    sendBroadcast(timerUpdate)
                } else {
                    val timerFinishedIntent = Intent(applicationContext, TeaDetailsActivity::class.java)
                    timerFinishedIntent.putExtra(Constants.EXTRA_INDEX, mTeaIndex)
                    timerFinishedIntent.putExtra(Constants.EXTRA_OPEN_TIMER, true)
                    timerFinishedIntent.putExtra(Constants.EXTRA_START_ALARM, true)
                    timerFinishedIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                    startActivity(timerFinishedIntent)
                }
                stopSelf()
            }
        }

        mCountDownTimer.start()

        val teaName = resources.getStringArray(R.array.tea_names)[mTeaIndex]
        createNotification(teaName, milliseconds)

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private var lastseconds = -1
    private fun createNotification(title: String, timeleft: Long) {
        if (lastseconds != timeleft.toInt() / 1000) {
            lastseconds = timeleft.toInt() / 1000

            val notificationIntent = Intent(this, TeaDetailsActivity::class.java)
            notificationIntent.putExtra(Constants.EXTRA_INDEX, mTeaIndex)
            notificationIntent.putExtra(Constants.EXTRA_OPEN_TIMER, true)
            val pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            var minutes = 0
            var seconds = timeleft.toInt() / 1000
            while (seconds >= 60) {
                minutes += 1
                seconds -= 60
            }

            val notification = NotificationCompat.Builder(this, Constants.TIMER_NOTIFICATION_ID)
                    .setOngoing(true)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setColor(resources.getColor(R.color.colorPrimary))
                    .setContentTitle(title)
                    .setContentText(getString(R.string.notification_timer_format, minutes, seconds))
                    .setContentIntent(pendingIntent).build()

            startForeground(Constants.TIMER_FOREGROUND_NOTIFICATION_ID, notification)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        mCountDownTimer.cancel()
    }
}