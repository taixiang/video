package com.video;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.video.bean.Data;
import com.video.service.BaseObserver;
import com.video.service.IBaseView;
import com.video.service.RetrofitService;
import com.video.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

        GSYVideoType.enableMediaCodec();
        GSYVideoType.enableMediaCodecTexture();

        timer = new Timer();
        initData();

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

    /**
     * 网络请求
     */
    private void initData() {
        RetrofitService.doSubscribeWithData(Constant.appUrlApi.getData(), this.<List<Data>>bindToLife(), new BaseObserver<List<Data>>() {
            @Override
            public void onNext(List<Data> data) {
                super.onNext(data);
                list.addAll(data);
                play();
            }
        }, this, false);
    }

    /**
     * 播放0 视频&1 图片
     */
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

    /**
     * 图片播放延迟5s
     */
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
        timer.schedule(task, 5000);
    }

    @Override
    protected void onResume() {
        getCurPlay().onVideoResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        getCurPlay().onVideoPause();
        super.onPause();
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
