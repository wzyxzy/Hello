package com.rock.hello;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.rock.hello.utils.CommonUtil;
import com.rock.hello.utils.LightnessController;
import com.rock.hello.utils.VolumeController;

/**
 * Vitamio 第三方
 * 做一个简单的视频播放
 * VideoView
 */
public class MainActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, CompoundButton.OnCheckedChangeListener, View.OnTouchListener, SeekBar.OnSeekBarChangeListener, Handler.Callback {

    private static final String TAG = MainActivity.class.getSimpleName();
    /**
     * 播放视频的VideoView
     */
    private VideoView mVideo;
    /**
     * VideoView加载资源是否准备好了
     */
    private boolean isPrepared;
    /**
     * 控制播放的布局
     */
    private View controller;
    /**
     * 当前播放时间的View
     */
    private TextView mCurrentTime;
    /**
     * 视频总时长
     */
    private TextView mTotalTime;
    /**
     * 视频播放进度的进度条
     */
    private SeekBar mPlayerProgress;
    /**
     * 屏幕宽度
     */
    private int mScreenWidth;
    /**
     * 屏幕高度
     */
    private int mScreenHeight;
    /**
     * 是否是横屏
     */
    private boolean isLandscape;
    /**
     * 更新进度的消息
     */
    private static final int PROGRESS = 1;
    /**
     *
     */
    private Handler mHandler;
    /**
     * 每次滑动的X
     */
    private float mLastMotionX;
    /**
     * 每次滑动后的Y
     */
    private float mLastMotionY;
    /**
     * 触摸起点X
     */
    private int startX;
    /**
     * 触摸起点Y
     */
    private int startY;
    /**
     * 滑动有效的临界值
     */
    private int threshold = 30;
    /**
     * 进度改变的标志位
     */
    private boolean isChangeProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e(TAG, "onCreate");
        initView();
    }

    private void initView() {
        mVideo = ((VideoView) findViewById(R.id.video_view));

        if (mVideo != null) {
            mVideo.setOnPreparedListener(this);
        }
        // 获取屏幕高度
        mScreenHeight = getResources().getDisplayMetrics().heightPixels;
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;

        // 设置VideoView高为屏幕的三分之一
        mVideo.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mScreenHeight / 3));

        mVideo.setOnTouchListener(this);

        // 加载资源
        mVideo.setVideoURI(Uri.parse("http://7rflo2.com2.z0.glb.qiniucdn.com/5714b0b53c958.mp4"));
        isPrepared = false;

        CheckBox playerScreen = (CheckBox) findViewById(R.id.player_full_screen);
        if (playerScreen != null) {
            playerScreen.setOnCheckedChangeListener(this);
        }
        CheckBox playerPlay = (CheckBox) findViewById(R.id.player_play);
        if (playerPlay != null) {
            playerPlay.setOnCheckedChangeListener(this);
        }
        controller = findViewById(R.id.player_controll_view);

        mCurrentTime = ((TextView) findViewById(R.id.player_current_time));
        mTotalTime = ((TextView) findViewById(R.id.player_total_time));

        mPlayerProgress = ((SeekBar) findViewById(R.id.player_progress));
        if (mPlayerProgress != null) {
            mPlayerProgress.setOnSeekBarChangeListener(this);
        }

        mHandler = new Handler(this);

    }

    // 设置准备好了的监听
    @Override
    public void onPrepared(MediaPlayer mp) {
        mVideo.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        int duration = mVideo.getDuration();
        mTotalTime.setText(CommonUtil.formatTime(duration));
        mPlayerProgress.setMax(duration);
        isPrepared = true;

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.player_full_screen:
                if (isChecked) {
                    // 添加全屏的标记
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    // 请求窗口全屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    // 清除窗口标记
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                break;
            case R.id.player_play:
                if (isChecked && isPrepared) {
                    mVideo.start();
                    mHandler.sendEmptyMessage(PROGRESS);
                } else if (!isChecked && isPrepared) {
                    mVideo.pause();
                    mHandler.removeMessages(PROGRESS);
                }
                break;
        }
    }

    /**
     * 屏幕配置发生改变
     *
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.e(TAG, "ORIENTATION_LANDSCAPE");
            isLandscape = true;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.e(TAG, "ORIENTATION_PORTRAIT");
            isLandscape = false;
        }
        super.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = x;
                mLastMotionY = y;
                startX = (int) x;
                startY = (int) y;
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = event.getX() - mLastMotionX;
                float deltaY = event.getY() - mLastMotionY;
                float absDeltaX = Math.abs(deltaX);
                float absDeltaY = Math.abs(deltaY);
                if (isLandscape) {
                    if (absDeltaX > threshold & absDeltaY > threshold) {
                        if (absDeltaX > absDeltaY) {
                            isChangeProgress = true;
                        } else {
                            isChangeProgress = false;
                        }
                    } else if (absDeltaX > threshold & absDeltaY < threshold) {
                        isChangeProgress = true;
                    } else if (absDeltaY > threshold & absDeltaX < threshold) {
                        isChangeProgress = false;
                    } else {
                        return true;
                    }

                    if (isChangeProgress) {

                        if (deltaX > 0) {
                            //TODO 前进
                            Log.e(TAG, "前进");
                            fastForward(deltaX);
                        } else {
                            //TODO 回退
                            Log.e(TAG, "后退");
                            fastRewind(deltaX);
                        }
                    } else {
                        // 以屏幕中间为分界线
                        if (x > mScreenHeight / 2) {
                            // TODO 改变音量
                            if (deltaY > 0) {
                                //TODO 降低音量
                                Log.e(TAG, "音量减");
                                VolumeController.volumeDown(this,mScreenWidth,deltaY);
                            } else {
                                // TODO 增加音量
                                Log.e(TAG, "音量加");
                                VolumeController.volumeUp(this,mScreenWidth,deltaY);
                            }
                        } else {
                            // 改变屏幕亮度
                            if (deltaY > 0) {
                                // TODO 降低屏幕亮度
                                Log.e(TAG, "降低亮度");
                                LightnessController.lightnessDown(this,mScreenHeight,deltaY);
                            } else {
                                // TODO 提升屏幕亮度
                                Log.e(TAG, "提升亮度");
                                LightnessController.lightnessUp(this,mScreenHeight,deltaY);
                            }
                        }
                    }
                    mLastMotionX = x;
                    mLastMotionY = y;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(x - startX) > threshold || Math.abs(y - startY) > threshold) {

                } else {
                    showOrHide();
                }

                break;
        }

        return true;
    }

    // 显示或者隐藏底部顶部控制
    private void showOrHide() {
        if (controller.getVisibility() == View.VISIBLE) {
            Animation exit = AnimationUtils.loadAnimation(this, R.anim.controller_exit);
            controller.startAnimation(exit);
            controller.setVisibility(View.INVISIBLE);
        } else if (controller.getVisibility() == View.INVISIBLE) {
            Animation enter = AnimationUtils.loadAnimation(this, R.anim.controller_enter);
            controller.startAnimation(enter);
            controller.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (isPrepared) {
            mVideo.pause();
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (isPrepared) {
            mVideo.seekTo(seekBar.getProgress());
            mVideo.start();
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case PROGRESS:
                int currentPosition = mVideo.getCurrentPosition();
                mPlayerProgress.setProgress(currentPosition);
                mCurrentTime.setText(CommonUtil.formatTime(currentPosition));
                mHandler.sendEmptyMessageDelayed(PROGRESS, 1000);
                break;
        }
        return true;
    }

    /**
     * 快退
     * @param deltaX
     */
    private void fastRewind(float deltaX){
        int currentPosition = mVideo.getCurrentPosition();
        int duration = mVideo.getDuration();
        float forwardTime = 0.2f * duration * deltaX / mScreenHeight;
        int currentTime = (int) (currentPosition + forwardTime);
        if (currentTime >= 0){
            mVideo.seekTo(currentTime);
            mPlayerProgress.setProgress(currentTime);
            mCurrentTime.setText(CommonUtil.formatTime(currentTime));
        }else{
            mVideo.seekTo(0);
            mPlayerProgress.setProgress(0);
            mCurrentTime.setText(CommonUtil.formatTime(0));
        }
    }

    /**
     * 快进
     * @param deltaX
     */
    private void fastForward(float deltaX){
        int currentPosition = mVideo.getCurrentPosition();
        int duration = mVideo.getDuration();
        float forwardTime = 0.2f * duration * deltaX / mScreenHeight;
        int currentTime = (int) (currentPosition + forwardTime);
        if (currentTime <= duration){
            mVideo.seekTo(currentTime);
            mPlayerProgress.setProgress(currentTime);
            mCurrentTime.setText(CommonUtil.formatTime(currentTime));
        }else{
            mVideo.seekTo(duration);
            mPlayerProgress.setProgress(duration);
            mCurrentTime.setText(CommonUtil.formatTime(duration));
        }
    }


}
