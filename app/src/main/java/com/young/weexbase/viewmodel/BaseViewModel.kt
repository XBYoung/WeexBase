package com.young.weexbase.viewmodel

import androidx.lifecycle.ViewModel
import com.young.weexbase.model.apiAbout.ApiHelper
import org.apache.weex.bridge.JSCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.xutils.common.util.LogUtil
import retrofit2.HttpException
import java.net.ConnectException
import java.text.ParseException

open class BaseViewModel : ViewModel(), CoroutineScope by MainScope() {
    var jsCallback: JSCallback? = null
    val tokenApi by lazy { ApiHelper.createTokenApi() }

    fun tryNet(doFail:((Throwable)->Unit)?=null,func: suspend () -> Unit) {
        launch {
            runCatching {
                func.invoke()
            }.onFailure {
                doFail?.invoke(it)
                it.printStackTrace()
                LogUtil.e(it.message.toString())
                when (it) {
                    is ConnectException -> {
                    }
                    is HttpException -> {
                        when (it.code()) {
                            in 500 until Int.MAX_VALUE -> {
                                LogUtil.e(it.code().toString() + "服务器内部异常")
                            }
                            in 400 until 500 -> {
                                LogUtil.e(it.code().toString() + "请求异常")
                            }
                            else -> LogUtil.e(it.code().toString() + "网络异常")
                        }
                    }
                    is ParseException -> LogUtil.e(it.toString() + "数据异常")
                    else -> LogUtil.e(it.toString() + "网络异常")
                }
            }
        }
    }
}