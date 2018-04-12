package com.video.service;

import com.video.bean.Data;
import com.video.bean.BaseBean;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * @author tx
 * @date 2018/4/12
 */
public interface AppUrlApi {
    @GET("api/TvChannel/ItemList")
    Observable<BaseBean<List<Data>>> getData();
}
