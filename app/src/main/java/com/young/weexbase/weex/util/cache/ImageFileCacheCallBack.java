package com.young.weexbase.weex.util.cache;

import android.graphics.Bitmap;

public interface ImageFileCacheCallBack {
    /**加载图片错误*/
    public final static int STATE_ERROR = -1;
    /**加载图片成功*/
    public final static int STATE_SUCCESS = 1;
    /**加载图片网络等待*/
    public final static int STATE_WAITTING = 0;

    public void callBack(Bitmap bitmap, String path, int state,boolean isGif);
}
