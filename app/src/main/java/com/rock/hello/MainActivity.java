package com.rock.hello;

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.VideoView;

/**
 * Vitamio 第三方
 * 做一个简单的视频播放
 * VideoView
 *
 *
 *
 *
 */
public class MainActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, CompoundButton.OnCheckedChangeListener, View.OnTouchListener {

    private VideoView mVideo;
    // 声明媒体控制器
//    private MediaController mediaController;
    private boolean isPrepared;
    private View controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mVideo = ((VideoView) findViewById(R.id.video_view));
        // 实例化媒体控制器
//        mediaController = new MediaController(this);
        //  设置媒体控制器
//        mVideo.setMediaController(mediaController);
//        //  为VideoView设置播放的URI,设置Uri是一个相对耗时的操作
//        mVideo.setVideoURI(Uri.parse("http://7rflo2.com2.z0.glb.qiniucdn.com/5714b0b53c958.mp4"));
//        //  播放
//        mVideo.start();
        mVideo.setOnPreparedListener(this);
        // 获取屏幕高度
        int heightPixels = getResources().getDisplayMetrics().heightPixels;
        // 设置VideoView高为屏幕的三分之一
        mVideo.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightPixels / 3));

        mVideo.setOnTouchListener(this);

        // 在异步中加载资源
        new Thread(){
            @Override
            public void run() {
                super.run();
//                mVideo.setVideoURI(Uri.parse("http://7rflo2.com2.z0.glb.qiniucdn.com/5714b0b53c958.mp4"));
                mVideo.setVideoURI(Uri.parse("/storage/emulated/0/UCDownloads/zhou.mp4"));
            }
        }.start();

        ((CheckBox) findViewById(R.id.player_full_screen)).setOnCheckedChangeListener(this);
        ((CheckBox) findViewById(R.id.player_play)).setOnCheckedChangeListener(this);
        controller = findViewById(R.id.player_controll_view);
    }
// 设置准备好了的监听
    @Override
    public void onPrepared(MediaPlayer mp) {
        mVideo.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        isPrepared = true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.player_full_screen:
                if (isChecked) {
                    // 添加全屏的标记
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    // 请求窗口全屏
                   // requestWindowFeature(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }else{
                    // 清除窗口标记
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                break;
            case R.id.player_play:
                if (isChecked && isPrepared){
                    mVideo.start();
                }else if(isPrepared && !isChecked){
                    mVideo.pause();
                }
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        showOrHide();
        return false;
    }

    private void showOrHide(){
        if (controller.getVisibility() == View.VISIBLE) {
            Animation exit = AnimationUtils.loadAnimation(this, R.anim.controller_exit);
            controller.startAnimation(exit);
            controller.setVisibility(View.INVISIBLE);
        }else if(controller.getVisibility() == View.INVISIBLE){
            Animation enter = AnimationUtils.loadAnimation(this, R.anim.controller_enter);
            controller.startAnimation(enter);
            controller.setVisibility(View.VISIBLE);
        }
    }

}
