package com.video.service;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 *
 * @author tx
 * @date 2017/10/13
 */

public class BaseObserver<T> implements IBaseObserver<T> {

    @Override
    public void onCustomError(int code, String msg) {

    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {

    }

    @Override
    public void onNext(@NonNull T t) {

    }

    @Override
    public void onError(@NonNull Throwable e) {

    }

    @Override
    public void onComplete() {

    }
}
