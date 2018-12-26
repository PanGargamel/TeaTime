package pl.piotrskiba.teatime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.piotrskiba.teatime.services.CountDownTimerService;

public class TeaTimerFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    @BindView(R.id.sb_timer)
    SeekBar mTimerSeekBar;

    @BindView(R.id.tv_timer)
     TextView mTimerTextView;

    @BindView(R.id.btn_start_timer)
    TextView mTimerStartButton;

    private int mTeaIndex;

    private TimerUpdateReceiver mTimerUpdateReceiver;

    public TeaTimerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_tea_timer, container, false);

        ButterKnife.bind(this, rootView);

        populateFragment();

        return rootView;
    }

    public void setTeaIndex(int index){
        mTeaIndex = index;
    }

    private void populateFragment() {
        mTimerSeekBar.setOnSeekBarChangeListener(this);
        setDefaultSeekBarProgress();

        mTimerStartButton.setOnClickListener(this);
    }

    private void setDefaultSeekBarProgress() {
        int max = getResources().getIntArray(R.array.tea_max_brewing_time)[mTeaIndex];

        if (max != 0) {
            int min = getResources().getIntArray(R.array.tea_min_brewing_time)[mTeaIndex];
            float defVal = getResources().getIntArray(R.array.tea_default_brewing_time)[mTeaIndex];
            int sbval = (int)((defVal-min) / (max-min) * 100);
            mTimerSeekBar.setProgress(sbval);

            onProgressChanged(mTimerSeekBar, mTimerSeekBar.getProgress(), false);
        }
    }

    private int getSeekBarValue(int progress){
        int min = getResources().getIntArray(R.array.tea_min_brewing_time)[mTeaIndex];
        int max = getResources().getIntArray(R.array.tea_max_brewing_time)[mTeaIndex];

        progress = (int)((float)progress/100 * (max-min));
        progress /= 5;
        progress *= 5;
        progress += min;

        return progress;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int seconds = getSeekBarValue(progress);
        updateTimerText(seconds);
    }

    private void updateTimerText(int seconds){
        int minutes = 0;

        while(seconds >= 60){
            seconds -= 60;
            minutes += 1;
        }

        mTimerTextView.setText(getString(R.string.timer_format, minutes, seconds));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_start_timer) {
            Intent timerService = new Intent(getContext(), CountDownTimerService.class);
            timerService.putExtra(Constants.EXTRA_INDEX, mTeaIndex);
            timerService.putExtra(Constants.EXTRA_SECONDS, getSeekBarValue(mTimerSeekBar.getProgress()));
            getContext().getApplicationContext().startService(timerService);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mTimerUpdateReceiver == null)
            mTimerUpdateReceiver = new TimerUpdateReceiver();
        IntentFilter intentFilter = new IntentFilter(Constants.TIMER_UPDATE_ACTION);
        getContext().registerReceiver(mTimerUpdateReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();

        if(mTimerUpdateReceiver != null)
            getContext().unregisterReceiver(mTimerUpdateReceiver);
    }

    private class TimerUpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.TIMER_UPDATE_ACTION)) {
                int teaIndex = intent.getIntExtra(Constants.EXTRA_INDEX, -1);
                if(teaIndex == mTeaIndex) {
                    int seconds = intent.getIntExtra(Constants.EXTRA_SECONDS, -1);
                    updateTimerText(seconds);
                }
            }
        }
    }
}
