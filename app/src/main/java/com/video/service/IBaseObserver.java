package com.video.service;

import io.reactivex.Observer;

/**
 *
 * @author tx
 * @date 2017/10/13
 */

public interface IBaseObserver<T> extends Observer<T> {

    /**
     * 自定义错误
     * @param code 错误码
     * @param msg 错误信息
     */
    void onCustomError(int code, String msg);

}