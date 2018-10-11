package com.example.a14412.tvoid1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.io.UnsupportedEncodingException;
import java.util.function.Consumer;

import static android.graphics.BitmapFactory.decodeResource;
import static com.tencent.rtmp.TXLiveConstants.VIDEO_QUALITY_SUPER_DEFINITION;

public class FullDemo extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "------FullDemo-------";
    /**
     * Viwe
     */

    private Button Camera;
    private Button flashlight;
    private Button stop;
    private Button pause;
    private Button watermark;
    private Button coding;
    private Button send;
    private Button music;
    private TXCloudVideoView video_view;
    private SeekBar Face;
    /**
     * Data
     */
    private TXLivePushConfig mLivePushConfig;
    private TXLivePusher mLivePusher;
    //标记播放状态0为播放1为停止
    private int startStatus = 1;
    //标记是否为播放和暂停状态
    private int pauseStatus  = 0;
    //闪光灯
    private boolean mFlashTurnOn = true;
    //标记是否添加水印
    private boolean water;
    //标记开启硬件编码
    private boolean mHWVideoEncode;
    //推流地址换自己的，链接肯定失效
    private String rtmpUrl = "rtmp://33173.livepush.myqcloud.com/live/33173_742ed29813?bizid=33173&txSecret=a3cd76c609d304371167a345d3aeff6f&txTime=5BBCD07F";
    //主播发的字
    String questionInfo="111";
    private RxPermissions rxPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fulldemo);
        initView();
        rxPermissions = new RxPermissions(this);
        //Rxjava获取危险权限
        getPermissions();
        //创建一个 TXLivePusher对象，我们后面主要用它来完成推流工作。
        mLivePusher = new TXLivePusher(this);
        //该对象的用途是决定 LivePush 推流时各个环节的配置参数
        mLivePushConfig = new TXLivePushConfig();
        mLivePushConfig.enableNearestIP(true);
        mLivePusher.setConfig(mLivePushConfig);
        // 只有在推流启动前设置启动纯音频推流才会生效，推流过程中设置不会生效。
        mLivePushConfig.enablePureAudioPush(false);
        //设置清晰度
        mLivePusher.setVideoQuality(VIDEO_QUALITY_SUPER_DEFINITION, false, false);
        initFace();
        //美颜
        mLivePusher.setBeautyFilter(0, 5, 5, 5);
        //等候图
        mLivePushConfig.setPauseImg(300, 5);
        // 300 为后台播放暂停图片的最长持续时间,单位是秒
        // 10 为后台播放暂停图片的帧率,最小值为 5,最大值为 20
        Bitmap bitmap = decodeResource(getResources(), R.mipmap.ic_launcher);
        mLivePushConfig.setPauseImg(bitmap);
        // 设置推流暂停时,后台播放的暂停图片, 图片最大尺寸不能超过 1920*1920.
        mLivePusher.setConfig(mLivePushConfig);
        // true 为启动纯音频推流，而默认值是 false
        mLivePusher.setConfig(mLivePushConfig);
        // 重新设置 config
        //设置镜像
        mLivePusher.setMirror(true);
        mLivePusher.startPusher(rtmpUrl);
        mLivePusher.startCameraPreview(video_view);
        //设置主播暂时离开的画面
        setLeave();
    }

    private void setWaterMark() {
        //设置水印图片必须是png格式的后面两个参数分别是水印位置的 X 轴坐标和 Y 轴坐标
        mLivePushConfig.setWatermark(BitmapFactory.decodeResource(getResources(), R.drawable.d), 100, 100);
        //重新设置 config
        mLivePusher.setConfig(mLivePushConfig);
       
    }

    private void initFace() {
        Face.setMax(9);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Camera:
                // 默认是前置摄像头
                mLivePusher.switchCamera();
                break;
            case R.id.flashlight:
                //打开闪光灯
                if (!mLivePusher.turnOnFlashLight(mFlashTurnOn)) {
                    Toast.makeText(FullDemo.this, "打开闪光灯失败:绝大部分手机不支持前置闪光灯!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.stop:
                switch (startStatus) {
                    case 0:
                        mLivePusher.startCameraPreview(video_view);
                        stop.setText("结束");
                        Log.e(TAG,"---开始播放---");
                        startStatus = 1;
                        break;
                    case 1:
                        stopRtmpPublish();
                        stop.setText("播放");
                        startStatus = 0;
                        Log.e(TAG,"--结束播放---");
                        break;
                    default:
                }
                break;
            case R.id.pause:
                switch (pauseStatus) {
                    case 0:
                        mLivePusher.pausePusher();
                        pauseStatus=1;
                        pause.setText("继续播放");
                        Log.e(TAG,"--暂停播放---");
                        break;
                    case 1:
                        mLivePusher.resumePusher();
                        pauseStatus=0;
                        Log.e(TAG,"--结束播放---");
                        pause.setText("暂停播放");
                        break;
                }
                break;
            case R.id.watermark:
                if (!water) {
                    setWaterMark();
                    water = true;
                }
                break;
            case R.id.coding:
                setCoding();
                break;
            case R.id.send:
                try {
                    mLivePusher.sendMessage(questionInfo.getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.music:
                Toast.makeText(this, "music", Toast.LENGTH_SHORT).show();
                mLivePusher.playBGM("");
                break;
            default:

        }
    }

    private void stopRtmpPublish() {
        mLivePusher.stopCameraPreview(true);
        //停止摄像头预览
        mLivePusher.stopPusher();
        //停止推流
        mLivePusher.setPushListener(null);
        //解绑 listener
    }

    private void setCoding() {
        if (!mHWVideoEncode) {
            if (mLivePushConfig != null) {
                if (Build.VERSION.SDK_INT < 18) {
                    Toast.makeText(getApplicationContext(), "硬件加速失败，当前手机 API 级别过低（最低 18）",
                            Toast.LENGTH_SHORT).show();
                    mHWVideoEncode = false;
                } else {
                    mLivePushConfig.setHardwareAcceleration(TXLiveConstants.ENCODE_VIDEO_HARDWARE);
                    mLivePusher.setConfig(mLivePushConfig);
                    Toast.makeText(getApplicationContext(), "硬件加速已开启",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    private void setLeave() {
        mLivePushConfig.setPauseImg(300, 5);
        // 300 为后台播放暂停图片的最长持续时间,单位是秒
        // 10 为后台播放暂停图片的帧率,最小值为 5,最大值为 20
        Bitmap bitmap = decodeResource(getResources(), R.drawable.gengduo);
        mLivePushConfig.setPauseImg(bitmap);
        // 设置推流暂停时,后台播放的暂停图片, 图片最大尺寸不能超过 1920*1920.
        mLivePusher.setConfig(mLivePushConfig);
        Toast.makeText(this, "主播暂时离开了", Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // 自动旋转打开，Activity随手机方向旋转之后，只需要改变推流方向
        int mobileRotation = this.getWindowManager().getDefaultDisplay().getRotation();
        int pushRotation = TXLiveConstants.VIDEO_ANGLE_HOME_DOWN;
        switch (mobileRotation) {
            case Surface.ROTATION_0:
                pushRotation = TXLiveConstants.VIDEO_ANGLE_HOME_DOWN;
                break;
            case Surface.ROTATION_90:
                pushRotation = TXLiveConstants.VIDEO_ANGLE_HOME_RIGHT;
                break;
            case Surface.ROTATION_270:
                pushRotation = TXLiveConstants.VIDEO_ANGLE_HOME_LEFT;
                break;
            default:
                break;
        }

        //通过设置config是设置生效（可以不用重新推流，腾讯云是少数支持直播中热切换分辨率的云商之一）
        mLivePusher.setRenderRotation(0);
        mLivePushConfig.setHomeOrientation(pushRotation);
        mLivePusher.setConfig(mLivePushConfig);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
        video_view.onPause();  // video_view 是摄像头的图像渲染view
        mLivePusher.pausePusher(); // 通知 SDK 进入“后台推流模式”了
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRtmpPublish();
        }
    private void initView() {
        video_view = (TXCloudVideoView) findViewById(R.id.video_view);
        Camera = (Button) findViewById(R.id.Camera);
        Camera.setOnClickListener(this);
        flashlight = (Button) findViewById(R.id.flashlight);
        flashlight.setOnClickListener(this);
        stop = (Button) findViewById(R.id.stop);
        stop.setOnClickListener(this);
        pause = (Button) findViewById(R.id.pause);
        pause.setOnClickListener(this);
        watermark = (Button) findViewById(R.id.watermark);
        watermark.setOnClickListener(this);
        coding = (Button) findViewById(R.id.coding);
        coding.setOnClickListener(this);
        send = (Button) findViewById(R.id.send);
        send.setOnClickListener(this);
        music = (Button) findViewById(R.id.music);
        music.setOnClickListener(this);
        Face = (SeekBar) findViewById(R.id.Face);
        Face.setOnClickListener(this);

    }

    @SuppressLint("CheckResult")
    public void getPermissions() {
        rxPermissions.request(Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO)
                .subscribe(new io.reactivex.functions.Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean){
                            Toast.makeText(FullDemo.this, "成功", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(FullDemo.this, "失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
