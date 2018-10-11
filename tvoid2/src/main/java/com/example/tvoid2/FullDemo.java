package com.example.tvoid2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.io.UnsupportedEncodingException;

public class FullDemo extends AppCompatActivity {
    //播放地址换自己的，链接肯定失效
    private String flvUrl = "http://33173.liveplay.myqcloud.com/live/33173_456a1ec570.flv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //mPlayerView 即 step1 中添加的界面 view
        TXCloudVideoView mView = (TXCloudVideoView)findViewById(R.id.video_view);
        //创建 player 对象
        TXLivePlayer mLivePlayer = new TXLivePlayer(this);
        //关键 player 对象与界面 view
        mLivePlayer.setPlayerView(mView);
        //推荐 FLV
        mLivePlayer.startPlay(flvUrl, TXLivePlayer.PLAY_TYPE_LIVE_FLV);
        // 设置填充模式
        mLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);
        // 设置画面渲染方向
        mLivePlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_LANDSCAPE);

        mLivePlayer.setPlayListener(new ITXLivePlayListener() {
            @Override
            public void onPlayEvent(int event, Bundle param) {
                if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT) {
                    Log.e("----", "[AnswerRoom] 拉流失败：网络断开");
                    Log.e("----", "网络断开，拉流失败");
                } else if (event == TXLiveConstants.PLAY_EVT_GET_MESSAGE) {
                    String msg = null;
                    try {
                        msg = new String(param.getByteArray(TXLiveConstants.EVT_GET_MSG), "UTF-8");
                        Log.e("----", msg.toString());
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNetStatus(Bundle status) {
            }
        });
    }
}
