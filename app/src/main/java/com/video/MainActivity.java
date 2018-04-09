package com.video;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private CustomVideoView player;
    private ImageView iv;
    private Timer timer;
    private TimerTask task;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            play();
        }
    };

    private List<Data> list = new ArrayList<>();
    private int nextType;
    private int index =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        player = findViewById(R.id.player);
        iv = findViewById(R.id.iv);
        timer = new Timer();
        initData();
        play();

        player.setStandardVideoAllCallBack(new VideoViewListener() {

            @Override
            public void onPrepared(String url, Object... objects) {
                super.onPrepared(url, objects);
                int videoWidth = GSYVideoManager.instance().getCurrentVideoWidth();
                int videoHeight = GSYVideoManager.instance().getCurrentVideoHeight();

                int width = CommonUtil.getWidthAndHeight(MainActivity.this)[0];
                int height = width * videoHeight / videoWidth;

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) player.getLayoutParams();
                params.height = height;
            }

            @Override
            public void onAutoComplete(String url, Object... objects) {
                super.onAutoComplete(url, objects);

                play();

            }
        });
    }

    private void initData(){

        Data data4 = new Data();
        data4.setType(1);
        data4.setUrl("http://kaoyanvip.cn/static/img/index/one-2-m.jpg");
        list.add(data4);


        Data data = new Data();
        data.setType(0);
        data.setUrl("https://cms.bscwin.com/upload/201801/30/9f9d9ebc-6f67-4e09-9b14-d59849c5fdd2.mp4");
        list.add(data);

        Data data1 = new Data();
        data1.setType(0);
        data1.setUrl("https://cms.bscwin.com/upload/201801/30/9f9d9ebc-6f67-4e09-9b14-d59849c5fdd2.mp4");
        list.add(data1);

        Data data2 = new Data();
        data2.setType(1);
        data2.setUrl("http://kaoyanvip.cn/static/img/index/one-2-m.jpg");
        list.add(data2);

        Data data3 = new Data();
        data3.setType(1);
        data3.setUrl("http://kaoyanvip.cn/static/img/index/one-2-r.jpg");
        list.add(data3);

        Log.i("》》》》 ",list.toString());


    }

    private void play(){

        nextType = list.get(index).getType();
        if(nextType == 0){
            player.setUp(list.get(index).getUrl(), true, "");
            player.startPlayLogic();
            player.setVisibility(View.VISIBLE);
            iv.setVisibility(View.GONE);

        }else {
            ImageLoader.loadImage(MainActivity.this, list.get(index).getUrl(), iv);
            iv.setVisibility(View.VISIBLE);
            startNewTask();
            player.setVisibility(View.GONE);
        }
        index++;
        if(index >= list.size()){
            index = 0;
        }
    }

    private void startNewTask() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        task = new TimerTask() {
            @Override
            public void run() {
                Log.i("》》》", "重新播放");
                handler.sendEmptyMessage(0);
            }
        };
        timer.schedule(task, 3000);
    }

    @Override
    protected void onResume() {
        getCurPlay().onVideoResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        getCurPlay().release();
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
        if (task != null) {
            task.cancel();
        }
    }

    private GSYVideoPlayer getCurPlay() {
        if (player.getFullWindowPlayer() != null) {
            return player.getFullWindowPlayer();
        }
        return player;
    }

}
