package pl.piotrskiba.teatime.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import pl.piotrskiba.teatime.Constants;
import pl.piotrskiba.teatime.R;
import pl.piotrskiba.teatime.TeaDetailsActivity;
import pl.piotrskiba.teatime.TeaTimerFragment;

public class CountDownTimerService extends Service {

    private int mTeaIndex = -1;
    private CountDownTimer mCountDownTimer;

    public CountDownTimerService(){
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mTeaIndex = intent.getIntExtra(Constants.EXTRA_INDEX, -1);
        long milliseconds = intent.getLongExtra(Constants.EXTRA_MILLISECONDS, 0);

        mCountDownTimer = new CountDownTimer(milliseconds, 100) {

            public void onTick(long millisUntilFinished) {
                Intent timerUpdate = new Intent(Constants.TIMER_UPDATE_ACTION);
                timerUpdate.putExtra(Constants.EXTRA_INDEX, mTeaIndex);
                timerUpdate.putExtra(Constants.EXTRA_MILLISECONDS, millisUntilFinished);
                sendBroadcast(timerUpdate);

                String tea_name = getResources().getStringArray(R.array.tea_names)[mTeaIndex];
                createNotification(tea_name, millisUntilFinished);

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String tea_id = getResources().getStringArray(R.array.tea_ids)[mTeaIndex];
                editor.putLong(getString(R.string.pref_timeleft_key, tea_id), millisUntilFinished);
                editor.apply();
            }

            public void onFinish() {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String tea_id = getResources().getStringArray(R.array.tea_ids)[mTeaIndex];
                editor.putLong(getString(R.string.pref_timeleft_key, tea_id), 0);
                editor.apply();

                if(TeaTimerFragment.inForeground) {
                    Intent timerUpdate = new Intent(Constants.TIMER_UPDATE_ACTION);
                    timerUpdate.putExtra(Constants.EXTRA_INDEX, mTeaIndex);
                    timerUpdate.putExtra(Constants.EXTRA_MILLISECONDS, 0);
                    sendBroadcast(timerUpdate);
                }
                else {
                    Intent timerFinishedIntent = new Intent(getApplicationContext(), TeaDetailsActivity.class);
                    timerFinishedIntent.putExtra(Constants.EXTRA_INDEX, mTeaIndex);
                    timerFinishedIntent.putExtra(Constants.EXTRA_OPEN_TIMER, true);
                    timerFinishedIntent.putExtra(Constants.EXTRA_START_ALARM, true);
                    timerFinishedIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(timerFinishedIntent);
                }

                stopSelf();
            }
        };
        mCountDownTimer.start();

        String tea_name = getResources().getStringArray(R.array.tea_names)[mTeaIndex];
        createNotification(tea_name, milliseconds);

        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private int lastseconds = -1;
    private void createNotification(String title, long timeleft){
        if(lastseconds != (int) timeleft / 1000) {
            lastseconds = (int) timeleft / 1000;

            Intent notificationIntent = new Intent(this, TeaDetailsActivity.class);
            notificationIntent.putExtra(Constants.EXTRA_INDEX, mTeaIndex);
            notificationIntent.putExtra(Constants.EXTRA_OPEN_TIMER, true);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            int minutes = 0;
            int seconds = (int)timeleft/1000;
            while(seconds >= 60){
                minutes += 1;
                seconds -= 60;
            }

            Notification notification = new NotificationCompat.Builder(this, Constants.TIMER_NOTIFICATION_ID)
                    .setOngoing(true)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setColor(getResources().getColor(R.color.colorPrimary))
                    .setContentTitle(title)
                    .setContentText(getString(R.string.notification_timer_format, minutes, seconds))
                    .setContentIntent(pendingIntent).build();

            startForeground(Constants.TIMER_FOREGROUND_NOTIFICATION_ID, notification);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCountDownTimer.cancel();
    }
}
