package best.customseekbarproject;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.FileDescriptor;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by BG207369 on 2016/1/5.
 */
public class MusicSeekBar extends FrameLayout implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    private SeekBar mSeekBar;
    private TextView mFinishTime;
    private ImageView mController;

    private MediaPlayer mediaPlayer;
    private String mSourceUrl;
    private int mPlayingTime;
    private Timer mTempTimer;


    public MusicSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MusicSeekBar(Context context) {
        this(context, null);
    }

    public MusicSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.custom_seekbar, this);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mFinishTime = (TextView) findViewById(R.id.finish_time);
        mController = (ImageView) findViewById(R.id.controller);
        mController.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(this);
        mediaPlayer = new MediaPlayer();
    }

    public void setMediaSource(String url) {
        this.mSourceUrl = url;
        try {
            if (!TextUtils.isEmpty(mSourceUrl)) {
                mediaPlayer.setDataSource(url);
                mediaPlayer.prepare();
                calculateTime();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMediaSource(FileDescriptor fileDescriptor) {
        try {
            if (fileDescriptor != null) {
                mediaPlayer.setDataSource(fileDescriptor);
                mediaPlayer.prepare();
                calculateTime();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calculateTime() {
        int duration = mPlayingTime;
        int hours = 0;
        int min = 0;
        int second = 0;
        second = duration % 60;
        min = duration / 60;
        if (min > 60) {
            hours = min / 60;
            min = min % 60;
        }
        StringBuffer sb = new StringBuffer();
        if (hours > 0) {
            sb.append(hours + ":");
        }
        if (min < 10) {
            sb.append(0);
        }
        sb.append(min + ":");
        if (second < 10)
            sb.append(0);
        sb.append(second);
        mFinishTime.setText(sb.toString());
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (progress == seekBar.getMax()) {
            if (mTempTimer != null) {
                mTempTimer.cancel();
                mTempTimer.purge();
                // TODO: 2016/1/6  controller 标志
                seekBar.setProgress(0);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int progress = seekBar.getProgress();
        int duration = mediaPlayer.getDuration();
        mediaPlayer.seekTo(duration * progress / seekBar.getMax());
    }

    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            mTempTimer.cancel();
            mTempTimer.purge();

        }
    }

    public void resume() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
        Timer timer = new Timer();
        mTempTimer = timer;
        timer.schedule(new MyTimerTask(), 0, 1000);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.controller) {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    pause();
                } else {
                    resume();
                }
            }
        }
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            Message message = handler.obtainMessage();
            handler.sendMessage(message);
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mPlayingTime++;
            calculateTime();
            mSeekBar.setProgress(mPlayingTime * mSeekBar.getMax() * 1000 / mediaPlayer.getDuration());
        }
    };
}
