package pl.piotrskiba.teatime.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import pl.piotrskiba.teatime.Constants;
import pl.piotrskiba.teatime.R;

public class NotificationUtils {

    public static void createNotificationChannel(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            NotificationChannel notificationChannel = new NotificationChannel(
                    Constants.TIMER_NOTIFICATION_ID,
                    context.getString(R.string.default_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setVibrationPattern(new long[]{0});
            notificationChannel.enableVibration(true);
            notificationChannel.setSound(null, null);

            notificationChannel.setImportance(NotificationManager.IMPORTANCE_DEFAULT);

            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
