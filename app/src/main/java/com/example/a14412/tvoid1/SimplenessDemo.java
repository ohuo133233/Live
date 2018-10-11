package com.example.a14412.tvoid1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;

public class SimplenessDemo extends AppCompatActivity {
    //简单的Demo
    private TXCloudVideoView video;
    private String rtmpUrl = "rtmp://33173.livepush.myqcloud.com/live/33173_56469d87cabe11e892905cb9018cf0d4?bizid=33173";
    private TXLivePusher mLivePusher;
    private TXLivePushConfig mLivePushConfig;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simpleness_demo);
        //找到TXCloudVideoView控件
        video = (TXCloudVideoView) findViewById(R.id.video);
        //这个对象用来完成推流工作
        mLivePusher = new TXLivePusher(this);
        //初始化该对象的用途是决定 LivePush 推流时各个环节的配置参数
        mLivePushConfig = new TXLivePushConfig();
        //设置config
        mLivePusher.setConfig(mLivePushConfig);
        //设置URL
        mLivePusher.startPusher(rtmpUrl);
        //关联TXCloudVideoView控件
        mLivePusher.startCameraPreview(video);

    }
}
