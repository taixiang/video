package com.video;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.model.VideoOptionModel;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.video.service.IBaseView;
import com.video.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class MainActivity extends RxAppCompatActivity implements IBaseView {


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
    private int index = 0;

    public static void actMain(RxAppCompatActivity activity, int type) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.putExtra("type", type);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        player = findViewById(R.id.player);
        iv = findViewById(R.id.iv);

        int type = getIntent().getIntExtra("type", 0);
        if(type == 1){ //软解降帧
            Log.i("》》》》","软解降帧");
            VideoOptionModel videoOptionModel =
                    new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 27);
            List<VideoOptionModel> list = new ArrayList<>();
            list.add(videoOptionModel);
            GSYVideoManager.instance().setOptionModelList(list);
        }else if(type == 2){
            Log.i("》》》》","硬解");
            GSYVideoType.enableMediaCodec();
            GSYVideoType.enableMediaCodecTexture();
        }else if(type == 3){
            Log.i("》》》》","硬解降帧");
            VideoOptionModel videoOptionModel =
                    new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 27);
            List<VideoOptionModel> list = new ArrayList<>();
            list.add(videoOptionModel);
            GSYVideoManager.instance().setOptionModelList(list);

            GSYVideoType.enableMediaCodec();
            GSYVideoType.enableMediaCodecTexture();
        }


        Log.i("》》》  ","是否硬解  "+GSYVideoType.isMediaCodec() +"  "+ GSYVideoType.isMediaCodecTexture());
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

    private void initData() {

        Data data4 = new Data();
        data4.setType(0);
        data4.setUrl("https://cms.bscwin.com/upload/201803/19/756a7bef-6513-487b-ae9d-c5d7dae644b3.mp4");
        list.add(data4);


        Data data = new Data();
        data.setType(0);
        data.setUrl("https://cms.bscwin.com/upload/201801/30/9f9d9ebc-6f67-4e09-9b14-d59849c5fdd2.mp4");
//        list.add(data);

        Data data1 = new Data();
        data1.setType(0);
        data1.setUrl("https://cms.bscwin.com/upload/201801/30/9f9d9ebc-6f67-4e09-9b14-d59849c5fdd2.mp4");
//        list.add(data1);

        Data data2 = new Data();
        data2.setType(1);
        data2.setUrl("http://www.kaoyanvip.cn/static/img/index/banner1/bg.jpg");
        list.add(data2);

        Data data3 = new Data();
        data3.setType(1);
        data3.setUrl("http://www.kaoyanvip.cn/static/img/index/banner2/bg.jpg");
//        list.add(data3);


    }

    private void play() {
        nextType = list.get(index).getType();
        if (nextType == 0) {
            player.setUp(list.get(index).getUrl(), true, "");
            player.startPlayLogic();
            player.setVisibility(View.VISIBLE);
            iv.setVisibility(View.GONE);

        } else {
            ImageLoader.loadImage(MainActivity.this, list.get(index).getUrl(), iv);
            iv.setVisibility(View.VISIBLE);
            startNewTask();
            player.setVisibility(View.GONE);
        }
        index++;
        if (index >= list.size()) {
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

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showErrorData() {

    }

    @Override
    public void showEmptyData() {

    }

    @Override
    public <T> LifecycleTransformer<T> bindToLife() {
        return this.<T>bindUntilEvent(ActivityEvent.DESTROY);
    }
}
