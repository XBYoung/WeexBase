package com.young.weexbase.weex.util.cache;

public interface FileCacheCallBack {

    /**空地址*/
    public final static int STATE_IS_EMPTY = -3;
    /**网络文件下载失败*/
    public final static int STATE_DOWN_ERROR = -2;
    /**非网络地址，本地无文件*/
    public final static int STATE_UNKNOWN_URL = -1;
    /**本地文件*/
    public final static int STATE_LOCAL_FILE = 0;
    /**网络文件下载成功*/
    public final static int STATE_DOWN_SUCCESS = 1;
    /**网络文件本地缓存*/
    public final static int STATE_NET_CACHE = 2;

    public void callBack(String path, int state);

    public void callBackContent(String content);
}
