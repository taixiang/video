package com.video.service;

import com.video.Data;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author tx
 * @date 2018/4/12
 */
public interface AppUrlApi {
    @GET("api/TvChannel/ItemList")
    Observable<BaseBean<List<Data>>> getData();
}
