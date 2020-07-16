package com.young.weexbase.model.apiAbout

import com.young.weexbase.BuildConfig
import com.young.weexbase.model.ApiHeader
import com.young.weexbase.utils.DeviceUtils
import com.young.weexbase.weex.application.WeexApplication
import com.young.weexbase.weex.util.KeyStore
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.Response
import org.xutils.common.util.LogUtil

/**
 * Created by Administrator on 2018/2/8.
 */


const val TAG_HTTP = "http_info"
val header: ApiHeader by lazy {
    ApiHeader("Android",
            DeviceUtils.product,
            DeviceUtils.phoneModel,
            DeviceUtils.getSystemVersion(),
            BuildConfig.VERSION_NAME

    )
}



fun getToken(): String {
    return KeyStore.getInstance(WeexApplication.application).get("logintoken", "")
}


class LogInterceptor : Interceptor {
    override fun intercept(chian: Interceptor.Chain?): Response {
        val builder = chian?.request()?.newBuilder()
        builder?.apply {
            addHeader("Content-Type", "application/json;charset:UTF-8")
            addHeader("Accept", "application/json")
            addHeader("charset", "UTF-8")
          //  LogUtil.d(TAG_HTTP + "  token =  " + getToken())
            addHeader("Authorization", "Bearer ${getToken()}")
        }
        val request = builder?.build()
        val url = request?.url()
        val connection = chian?.connection()
        val headers = request?.headers()
        LogUtil.d(" $TAG_HTTP method = ${request?.method()} body = ${request?.log()}" +
                "\r\nurl = $url " +
                "\r\nconnection = $connection" +
                "\r\nheaders = $headers")
        var response: Response? = chian?.proceed(request)
        val peekBody = response?.peekBody(1024 * 1024)
        val headers1 = response?.headers()
        response?.log()
        LogUtil.d("$TAG_HTTP peeekBody = $peekBody  headers1 = $headers1")
        return response!!
    }

}





