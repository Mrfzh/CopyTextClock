package com.feng.textclock;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final int UPDATE_CLOCK = 1;

    private TextClock mTextClock;
    private Timer mTimer;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_CLOCK:
                    mTextClock.doInvalidate();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        runClock();
    }

    private void initView() {
        mTextClock = findViewById(R.id.tc_main_clock);
    }

    /**
     * 开启时钟，每秒重绘一次
     */
    private void runClock() {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.obtainMessage(UPDATE_CLOCK).sendToTarget();
            }
        }, 0, 1000);

    }

    @Override
    protected void onDestroy() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        super.onDestroy();
    }
}
