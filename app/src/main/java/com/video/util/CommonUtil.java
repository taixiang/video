package com.video.util;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by tx on 2017/7/28.
 * 常用工具类
 */

public class CommonUtil {

    /**
     * 获取屏幕宽高
     *
     * @param context
     * @return 数组存放宽高
     */
    public static int[] getWidthAndHeight(Context context) {
        int[] wh = new int[2];
        DisplayMetrics metric = context.getResources().getDisplayMetrics();
        wh[0] = metric.widthPixels;
        wh[1] = metric.heightPixels;
        return wh;
    }
}
