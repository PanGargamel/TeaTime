package pl.piotrskiba.teatime;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class TimerFinishedActivity extends AppCompatActivity {

    Vibrator mVibrator;
    MediaPlayer mMediaPlayer = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_finished);

        Window wnd = getWindow();
        wnd.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        wnd.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        wnd.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mVibrator.hasVibrator()) {
            long[] pattern = new long[]{0, 500, 500};

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mVibrator.vibrate(VibrationEffect.createWaveform(pattern, 0));
            } else {
                mVibrator.vibrate(pattern, 0);
            }
        }

        try {
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            mMediaPlayer.setDataSource(this, uri);

            final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        }
        catch(Exception e){
            Log.e(this.getClass().getName(), "Can't start media player");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if(mVibrator.hasVibrator())
            mVibrator.cancel();
        if(mMediaPlayer.isPlaying())
            mMediaPlayer.stop();
    }
}
