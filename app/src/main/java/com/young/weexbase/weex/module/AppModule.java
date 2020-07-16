package com.young.weexbase.weex.module;

import com.young.weexbase.anno.LogAsp;
import org.apache.weex.annotation.JSMethod;
import org.apache.weex.bridge.JSCallback;
import org.apache.weex.common.WXModule;
import org.xutils.common.util.LogUtil;
import java.util.Map;

public class AppModule extends WXModule {



    @LogAsp
    @JSMethod
    public void openPage(String page, Map<String, Object> map) {
        //开启页面clearUserCache
        AppModuleLogic.openPage(mWXSDKInstance, page, map);
    }


    @LogAsp
    @JSMethod
    public void toBack(Map<String, Object> map) {
        //返回页面
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            LogUtil.d("JSMethod  toBack -> \r\n key = " + entry.getKey() + "  value = " + entry.getValue().toString());
        }
        AppModuleLogic.toBack(mWXSDKInstance, map);
    }


    @LogAsp
    @JSMethod
    public void startWaitting(String waitting) {
        //弹出系统等待框
        AppModuleLogic.startWaitting(mWXSDKInstance, waitting);
    }

    @LogAsp
    @JSMethod
    public void stopWaitting() {
        //关闭系统等待框
        AppModuleLogic.stopWaitting(mWXSDKInstance);
    }


    @LogAsp
    @JSMethod
    public void pickImage(Map<String, Object> map, JSCallback callback) {
        //选择图片
        AppModuleLogic.pickImage(mWXSDKInstance, map, callback);
    }

    @LogAsp
    @JSMethod
    public void pickVideo(Map<String, Object> map, JSCallback callback) {
        //选择视频
        AppModuleLogic.pickVideo(mWXSDKInstance, map, callback);
    }

    @LogAsp
    @JSMethod
    public void getSafeArea(JSCallback callback) {
        AppModuleLogic.getSafeArea(mWXSDKInstance, callback);
    }

    @LogAsp
    @JSMethod
    public void log(String log) {
        AppModuleLogic.weexLog(log);
    }


}
