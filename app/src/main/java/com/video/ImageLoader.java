package com.video;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;

/**
 * Created by tx on 2017/7/24.
 * 图片加载，使用Glide
 */

public class ImageLoader {

    /**
     * 默认图片加载方法
     *
     * @param context
     * @param url     图片url
     * @param iv      加载的imageView
     */
    public static void loadImage(Context context, String url, ImageView iv) {
        Glide.with(context).load(url).dontAnimate().into(iv);
    }

    /**
     * 自定义错误图片加载
     *
     * @param context
     * @param url
     * @param iv
     */
    public static void loadImage(Context context, String url, int errorImg, ImageView iv) {
        Glide.with(context).load(url).dontAnimate().error(errorImg).into(iv);
    }

    /**
     * 图片加载方法
     *
     * @param context
     * @param url      图片url
     * @param erroImg  加载错误图片
     * @param emptyImg 占位图
     * @param iv       加载的imageView
     */
    public static void loadImage(Context context, String url, int erroImg, int emptyImg, ImageView iv) {
        Glide.with(context).load(url).dontAnimate().placeholder(emptyImg).error(erroImg).into(iv);
    }

    /**
     * 加载gif
     *
     * @param context
     * @param url     gif的url
     * @param iv      加载的imageView
     */
    public static void loadGifImage(Context context, String url, ImageView iv) {
        Glide.with(context).load(url).
                asGif().
                diskCacheStrategy(DiskCacheStrategy.SOURCE).into(iv);
    }

    /**
     * 加载本地图片
     *
     * @param context
     * @param file      图片路径
     * @param imageView 加载的imageView
     */
    public static void loadImage(Context context, final File file, final ImageView imageView) {
        Glide.with(context).load(file).into(imageView);
    }

    /**
     * 加载资源Id的图片
     *
     * @param context
     * @param resourceId 图片资源Id
     * @param imageView  加载的imageView
     */
    public static void loadImage(Context context, final int resourceId, final ImageView imageView) {
        Glide.with(context).load(resourceId).dontAnimate().into(imageView);
    }
}
