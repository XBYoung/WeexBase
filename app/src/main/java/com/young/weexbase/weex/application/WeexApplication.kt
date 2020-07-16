package com.young.weexbase.weex.application


import android.content.Context
import android.text.TextUtils
import androidx.multidex.MultiDexApplication
import com.alibaba.sdk.android.push.CommonCallback
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory
import com.young.weexbase.anno.LogAsp
import com.young.weexbase.constants.MEMACCNO
import com.young.weexbase.weex.adapter.InterceptWXHttpAdapter
import com.young.weexbase.weex.adapter.WeexImageAdapter
import com.young.weexbase.weex.component.WeexImage
import com.young.weexbase.weex.module.AppModule
import com.young.weexbase.weex.module.AppModuleLogic
import com.young.weexbase.weex.module.pickers.WXPickersModule
import com.young.weexbase.weex.util.DeviceInfoUtil
import com.young.weexbase.weex.util.KeyStore
import com.young.weexbase.weex.component.CustomView
import com.young.weexbase.widget.NotificationChannelCustom
import com.google.gson.Gson
import com.young.weexbase.BuildConfig
import org.apache.weex.InitConfig
import org.apache.weex.WXSDKEngine
import org.apache.weex.ui.component.WXBasicComponentType
import org.xutils.common.util.LogUtil
import org.xutils.x


class WeexApplication : MultiDexApplication() {

    companion object {
        @JvmStatic
        var application: WeexApplication? = null
    }


    override fun onCreate() {
        super.onCreate()
        application = this
        if (TextUtils.equals(DeviceInfoUtil.getProcessName(applicationContext, android.os.Process.myPid()), packageName)) {
            //确定主进程
            //初始化WEEX
            x.Ext.init(this)
            x.Ext.setDebug(BuildConfig.DEBUG)
            initWeex()
            initCloudChannel(this)
            //Debuger.enable()
        } else {
            //当前其它进程，比如 推送服务进程。
            initCloudChannel(this)
        }
    }



    /**
     * 初始化WEEX
     */
    @LogAsp
    private fun initWeex() {
        if (WXSDKEngine.isInitialized()) {
            return
        }
        val config = InitConfig.Builder()
                .setImgAdapter(WeexImageAdapter())
                .setHttpAdapter(InterceptWXHttpAdapter())
                .build()
        try {
            WXSDKEngine.initialize(this, config)
            WXSDKEngine.registerModule("AppModule", AppModule::class.java)
            WXSDKEngine.registerModule("picker", WXPickersModule::class.java)
            WXSDKEngine.registerComponent(WXBasicComponentType.IMG, WeexImage::class.java)
            WXSDKEngine.registerComponent(WXBasicComponentType.IMAGE, WeexImage::class.java)
            WXSDKEngine.registerComponent("custom-view", CustomView::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 初始化云推送通道
     * @param applicationContext
     */
    private fun initCloudChannel(applicationContext: Context) {
        PushServiceFactory.init(applicationContext)
        NotificationChannelCustom.registChannel(this@WeexApplication)
        var cloudPushService = PushServiceFactory.getCloudPushService()
        //注册
        cloudPushService.register(applicationContext, object : CommonCallback {
            override fun onSuccess(response: String) {
                LogUtil.d("Init  " + "init cloudchannel success")
                //获取账号
                val value = KeyStore.getInstance(applicationContext).get<String>("userInfo", null)
                if (value != null && value.isNotEmpty()) {
                    var map: Map<String, Any> = Gson().fromJson<Map<String, Any>>(value, Map::class.java)
                    var mobileNumber = AppModuleLogic.getMapValue(map, MEMACCNO, "")
                    if (mobileNumber != null && mobileNumber.isNotEmpty()) {
                        //绑定账号
                        cloudPushService.bindAccount(mobileNumber, object : CommonCallback {
                            override fun onSuccess(response: String) {
                                LogUtil.d("BindAccount  Application bind account($mobileNumber) success")
                            }

                            override fun onFailed(errorCode: String, errorMessage: String) {
                                LogUtil.d("BindAccount  Application bind account($mobileNumber)  failed -- errorcode:$errorCode -- errorMessage:$errorMessage")
                            }
                        })
                    }
                }

            }

            override fun onFailed(errorCode: String, errorMessage: String) {
                LogUtil.d("Init  init cloudchannel failed -- errorcode:$errorCode -- errorMessage:$errorMessage")
            }
        })
    }

}
