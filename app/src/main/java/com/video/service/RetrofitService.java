package com.video.service;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.ArrayMap;

import com.trello.rxlifecycle2.LifecycleTransformer;
import com.video.util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 *
 * @author tx
 * @date 2017/7/20
 * 网络获取类
 */

public class RetrofitService {

    //在max-age规定的秒数内，浏览器将不会发送对应的请求到服务器，数据由缓存直接返回)
    static final String CACHE_CONTROL_NETWORK = "Cache-Control:  max-age=20";
    //断网只查询缓存而不会请求服务器，max-stale可以配合设置缓存失效时间
    private static final String CACHE_CONTROL_CACHE = "only-if-cached, max-stale=60";

    /**
     * 初始化
     * 返回值通过
     */
    public static Retrofit init(Context context, String url, ArrayMap<String, String> headers) {
        // 指定缓存路径,缓存大小100Mb
        File file;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            file = new File(context.getExternalCacheDir(), "HttpCache");
        } else {
            file = new File(context.getCacheDir(), "HttpCache");
        }
        Cache cache = new Cache(file, 1024 * 1024 * 100);
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(logger);
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder().cache(cache)
                .retryOnConnectionFailure(true)
                .addInterceptor(logInterceptor)
                .addInterceptor(setAppInterceptor(headers))
                .addNetworkInterceptor(setNetInterceptor())
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(url)
                .build();
        return retrofit;
    }

    /**
     * 打印返回的数据拦截器
     */
    private static HttpLoggingInterceptor.Logger logger = new HttpLoggingInterceptor.Logger() {
        @Override
        public void log(String message) {
            LogUtil.i(" HttpLog = ", message + " currentThread  " + Thread.currentThread().getName());
        }
    };


    /**
     * 缓存设置
     */
    private static Interceptor setAppInterceptor(final ArrayMap<String, String> headers) {
        Interceptor cacheInterceptor = new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request request = chain.request();
                String needCache;

                Request.Builder builder = request.newBuilder().removeHeader("Pragma");
                for(int i=0;headers != null && i<headers.size();i++){
                    builder.addHeader(headers.keyAt(i), headers.valueAt(i));
                }
                request = builder.build();

                LogUtil.i("request header  " + request.headers().toString());

                //有无缓存设置
                if (request.header("Cache-Control") != null
                        && request.header("Cache-Control").length() > 0) {
                    needCache = request.header("needCache");
                    //不需要缓存，直接走网络，用于下拉刷新
                    if (needCache != null && needCache.equals("false")) {
                        LogUtil.i("不需要缓存，直接走网络，用于下拉刷新");
                        request = request.newBuilder()
                                .removeHeader("Pragma")
                                .cacheControl(CacheControl.FORCE_NETWORK)
                                .build();
                    } else {//需要缓存，有无网络走缓存
                        LogUtil.i("需要缓存，走缓存");
                        request = request.newBuilder()
                                .removeHeader("Pragma")
                                .build();
                    }
                }
                Response response = chain.proceed(request);
                return response.newBuilder().build();
            }
        };
        return cacheInterceptor;
    }


    private static Interceptor setNetInterceptor() {
        Interceptor cacheInterceptor = new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request request = chain.request();
                //有无缓存设置
                if (request.header("Cache-Control") != null
                        && request.header("Cache-Control").length() > 0) {
                    String needCache = request.header("needCache");
                    Response response = chain.proceed(request);
                    //按照当前配置的header cacheControl
                    String cacheControl = request.cacheControl().toString();
                    if (needCache != null && needCache.equals("false")) { //刷新后直接缓存刷新的数据
                        cacheControl = "max-age=20";
                    }
                    LogUtil.i("cacheControl  " + cacheControl);
                    response = response.newBuilder()
                            .header("Cache-Control", cacheControl)
                            .removeHeader("Pragma")
                            .build();
                    return response;
                }
                Response response = chain.proceed(request);
                return response.newBuilder().build();
            }
        };
        return cacheInterceptor;
    }


    /**
     * 封装执行网络请求
     * @param <T>
     * @param ob        观察者，通过IUrlApi获取
     * @param lifecycle 固定生命周期
     * @param observer  订阅者，获取请求结果
     * @param view
     * @param isShowLoading
     */
    public static <T> boolean doSubscribeWithData(Observable<BaseBean<T>> ob,
                                               LifecycleTransformer<T> lifecycle,
                                               final BaseObserver<T> observer,
                                               final IBaseView view,
                                               boolean isShowLoading) {
        if(isShowLoading){
            view.showLoading();
        }
        ob.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<BaseBean<T>, T>() {
                    @Override
                    public T apply(@io.reactivex.annotations.NonNull BaseBean<T> tBaseBean)
                            throws Exception {
                        if (tBaseBean.getCode() >= 50000) {
                            throw new ApiException(tBaseBean.getCode(), tBaseBean.getMsg());
                        }
                        return tBaseBean.getData();
                    }
                })
                .compose(lifecycle)
                .subscribe(new Observer<T>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                        observer.onSubscribe(d);
                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull T t) {
                        if(view != null){
                            view.hideLoading();
                        }
                        observer.onNext(t);
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        if(view != null){
                            view.hideLoading();
                        }
                        if (e instanceof ApiException) {
                            ApiException ae = (ApiException) e;
                            observer.onCustomError(ae.getErrorCode(), ae.getMessage());
                        } else {
                            observer.onCustomError(-1, null);
                        }
                        LogUtil.i(" onerror  " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        observer.onComplete();
                    }
                });
        return true;
    }

    /**
     * 数据直接返回包含code、msg
     *
     * @param ob
     * @param lifecycle
     * @param observer
     * @param <T>
     */
    public static <T> boolean doSubscribeWithAllData(Observable<BaseBean<T>> ob,
                                                  LifecycleTransformer<BaseBean<T>> lifecycle,
                                                  final BaseObserver<BaseBean<T>> observer) {
        ob.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<BaseBean<T>, BaseBean<T>>() {
                    @Override
                    public BaseBean<T> apply(@io.reactivex.annotations.NonNull BaseBean<T> tBaseBean)
                            throws Exception {
                        if (tBaseBean.getCode() >= 50000) {
                            throw new ApiException(tBaseBean.getCode(), tBaseBean.getMsg());
                        }
                        return tBaseBean;
                    }
                })
                .compose(lifecycle)
                .subscribe(new Observer<BaseBean<T>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                        observer.onSubscribe(d);
                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull BaseBean<T> t) {
                        observer.onNext(t);
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        if (e instanceof ApiException) {
                            ApiException ae = (ApiException) e;
                            observer.onCustomError(ae.getErrorCode(), ae.getMessage());
                        } else {
                            observer.onCustomError(-1, null);
                        }
                        LogUtil.i(" onerror  " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        observer.onComplete();
                    }
                });
        return true;
    }

    /**
     * 不带生命周期的方法
     * @param ob
     * @param observer
     * @param <T>
     */
    public static <T> boolean doSubscribeNoLifecycle(Observable<BaseBean<T>> ob,
                                              final BaseObserver<T> observer){
        ob.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<BaseBean<T>, T>() {
                    @Override
                    public T apply(@io.reactivex.annotations.NonNull BaseBean<T> tBaseBean)
                            throws Exception {
                        if (tBaseBean.getCode() >= 50000) {
                            throw new ApiException(tBaseBean.getCode(), tBaseBean.getMsg());
                        }
                        return tBaseBean.getData();
                    }
                })
                .subscribe(new Observer<T>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                        observer.onSubscribe(d);
                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull T t) {
                        observer.onNext(t);
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        if (e instanceof ApiException) {
                            ApiException ae = (ApiException) e;
                            observer.onCustomError(ae.getErrorCode(), ae.getMessage());
                        } else {
                            observer.onCustomError(-1, null);
                        }
                        LogUtil.i(" onerror  " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        observer.onComplete();
                    }
                });
        return true;
    }

    /**
     * 直接返回String
     *
     * @param ob
     * @param lifecycle
     * @param observer
     */
    public static void doSubscribeWithString(Observable<String> ob,
                                             LifecycleTransformer<String> lifecycle,
                                             final BaseObserver<String> observer) {
        ob.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(lifecycle)
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                        observer.onSubscribe(d);
                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull String s) {
                        observer.onNext(s);
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        observer.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        observer.onComplete();
                    }
                });
    }
}