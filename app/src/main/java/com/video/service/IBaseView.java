package com.video.service;


import com.trello.rxlifecycle2.LifecycleTransformer;

/**
 * Created by tx on 2017/7/19.
 * 基础BaseView接口
 */
public interface IBaseView {

    /**
     * 显示加载
     */
    void showLoading();

    /**
     * 隐藏加载
     */
    void hideLoading();

    /**
     * 数据加载失败，异常情况
     */
    void showErrorData();

    /**
     * 数据为空
     */
    void showEmptyData();

    /**
     * 绑定生命周期
     * @param <T>
     * @return
     */
    <T> LifecycleTransformer<T> bindToLife();

}
