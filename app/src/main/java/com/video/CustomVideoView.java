package com.video;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

/**
 * Created by tx on 2018/4/9.
 */

public class CustomVideoView extends StandardGSYVideoPlayer {
    public CustomVideoView(Context context, Boolean fullFlag) {
        super(context, fullFlag);
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_DEFAULT);
        init();
    }

    public CustomVideoView(Context context) {
        super(context);
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_DEFAULT);
        init();
    }

    public CustomVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_DEFAULT);
        init();
    }

    private void init() {
        mStartButton.setVisibility(GONE);
        mLockScreen.setVisibility(GONE);
        mLoadingProgressBar.setVisibility(GONE);
        mProgressBar.setVisibility(GONE);
        mBackButton.setVisibility(GONE);
        mTopContainer.setVisibility(GONE);
        mBottomContainer.setVisibility(GONE);
        mBottomProgressBar.setVisibility(GONE);
        mLoadingProgressBar.setVisibility(GONE);

//        mDialogSeekTime.setVisibility(GONE);
//        mDialogTotalTime.setVisibility(GONE);
    }

    @Override
    protected void showProgressDialog(float deltaX, String seekTime, int seekTimePosition, String totalTime, int totalTimeDuration) {
        super.showProgressDialog(deltaX, seekTime, seekTimePosition, totalTime, totalTimeDuration);
        mDialogSeekTime.setVisibility(GONE);
        mDialogTotalTime.setVisibility(GONE);
        mDialogIcon.setVisibility(GONE);
        mDialogProgressBar.setVisibility(GONE);
    }

    @Override
    protected void onClickUiToggle() {
        super.onClickUiToggle();
        init();
    }

    @Override
    protected void updateStartImage() {
        super.updateStartImage();
        init();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        super.onProgressChanged(seekBar, progress, fromUser);
//        mBottomContainer.setVisibility(GONE);
        mBottomProgressBar.setVisibility(GONE);
    }


    @Override
    protected void hideAllWidget() {
        super.hideAllWidget();
        mBottomProgressBar.setVisibility(GONE);
    }

    @Override
    protected void changeUiToPreparingShow() {
        super.changeUiToPreparingShow();
        mLoadingProgressBar.setVisibility(GONE);
        setViewShowState(mTopContainer, GONE);
        setViewShowState(mBottomContainer, GONE);
    }


}
