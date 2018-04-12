package com.video;

import android.app.Application;

import com.video.service.AppUrlApi;
import com.video.service.RetrofitService;

import retrofit2.Retrofit;

/**
 * @author tx
 * @date 2018/4/12
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Retrofit retrofit = RetrofitService.init(this, Constant.base_url, null);
        Constant.appUrlApi = retrofit.create(AppUrlApi.class);

    }
}
