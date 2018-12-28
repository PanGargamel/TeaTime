package pl.piotrskiba.teatime.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import pl.piotrskiba.teatime.Constants;
import pl.piotrskiba.teatime.R;
import pl.piotrskiba.teatime.TeaDetailsActivity;

public class CountDownTimerService extends Service {

    private int mTeaIndex = -1;
    private CountDownTimer mCountDownTimer;

    public CountDownTimerService(){
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mTeaIndex = intent.getIntExtra(Constants.EXTRA_INDEX, -1);
        int seconds = intent.getIntExtra(Constants.EXTRA_SECONDS, 0);

        mCountDownTimer = new CountDownTimer(seconds * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                Intent timerUpdate = new Intent(Constants.TIMER_UPDATE_ACTION);
                timerUpdate.putExtra(Constants.EXTRA_INDEX, mTeaIndex);
                timerUpdate.putExtra(Constants.EXTRA_SECONDS, (int)millisUntilFinished/1000);
                sendBroadcast(timerUpdate);

                String tea_name = getResources().getStringArray(R.array.tea_names)[mTeaIndex];
                createNotification(tea_name, (int)millisUntilFinished/1000);
            }

            public void onFinish() {
                Intent timerUpdate = new Intent(Constants.TIMER_UPDATE_ACTION);
                timerUpdate.putExtra(Constants.EXTRA_INDEX, mTeaIndex);
                timerUpdate.putExtra(Constants.EXTRA_SECONDS, 0);
                sendBroadcast(timerUpdate);

                Intent timerFinishedIntent = new Intent(getApplicationContext(), TeaDetailsActivity.class);
                timerFinishedIntent.putExtra(Constants.EXTRA_INDEX, mTeaIndex);
                timerFinishedIntent.putExtra(Constants.EXTRA_OPEN_TIMER, true);
                timerFinishedIntent.putExtra(Constants.EXTRA_START_ALARM, true);
                timerFinishedIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(timerFinishedIntent);

                stopSelf();
            }
        };
        mCountDownTimer.start();

        String tea_name = getResources().getStringArray(R.array.tea_names)[mTeaIndex];
        createNotification(tea_name, seconds);

        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotification(String title, int timeleft){
        Intent notificationIntent = new Intent(this, TeaDetailsActivity.class);
        notificationIntent.putExtra(Constants.EXTRA_INDEX, mTeaIndex);
        notificationIntent.putExtra(Constants.EXTRA_OPEN_TIMER, true);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel(Constants.TIMER_NOTIFICATION_ID, getString(R.string.default_notification_channel_name), NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        int minutes = 0;
        int seconds = timeleft;
        while(seconds >= 60){
            minutes += 1;
            seconds -= 60;
        }

        Notification notification = new NotificationCompat.Builder(this, Constants.TIMER_NOTIFICATION_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(getString(R.string.notification_timer_format, minutes, seconds))
                .setContentIntent(pendingIntent).build();

        startForeground(Constants.TIMER_FOREGROUND_NOTIFICATION_ID, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCountDownTimer.cancel();
    }
}
