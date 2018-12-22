package pl.piotrskiba.teatime.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import pl.piotrskiba.teatime.Constants;
import pl.piotrskiba.teatime.R;
import pl.piotrskiba.teatime.TeaTimerFragment;

public class CountDownTimerService extends Service {

    public CountDownTimerService(){
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int seconds = intent.getIntExtra(Constants.EXTRA_SECONDS, 0);

        new CountDownTimer(seconds * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                Intent timerUpdate = new Intent(Constants.TIMER_UPDATE_ACTION);
                timerUpdate.putExtra(Constants.EXTRA_SECONDS, (int)millisUntilFinished/1000);
                sendBroadcast(timerUpdate);

            }

            public void onFinish() {
                Intent timerUpdate = new Intent(Constants.TIMER_UPDATE_ACTION);
                timerUpdate.putExtra(Constants.EXTRA_SECONDS, 0);
                sendBroadcast(timerUpdate);

                stopSelf();
            }
        }.start();

        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
