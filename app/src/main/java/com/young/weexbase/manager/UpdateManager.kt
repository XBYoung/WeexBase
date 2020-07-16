package com.young.weexbase.manager

import android.content.Context
import com.young.weexbase.BuildConfig
import com.young.weexbase.constants.JS_VERSION_CODE_KEY
import com.young.weexbase.utils.FileUtils
import com.young.weexbase.weex.application.WeexApplication
import com.young.weexbase.weex.util.KeyStore
import kotlinx.coroutines.supervisorScope

object UpdateManager {

    private fun saveCurrentVersionCode(context: Context) {
        KeyStore.getInstance(context).put(JS_VERSION_CODE_KEY, BuildConfig.VERSION_CODE)
    }

    suspend fun checkUpdated(context: Context) {
        val currentJsVersionCode = KeyStore.getInstance(context).get(JS_VERSION_CODE_KEY, -1)
        if (currentJsVersionCode == -1  || !FileUtils.jsCacheIsExist()) {
            //直接下载
            copyJs()
            saveCurrentVersionCode(context)
        } else{
            if(BuildConfig.VERSION_CODE > currentJsVersionCode){
                //删除下载
                FileUtils.clearJsCache()
                copyJs()
                saveCurrentVersionCode(context)
            }
        }
    }

    private suspend fun copyJs() {
        runCatching {
            supervisorScope {
                WeexApplication.application?.baseContext?.let { context ->
                    FileUtils.copyAssetsToSd(context, "weex")
                }
            }
        }.onFailure {
            it.printStackTrace()
            FileUtils.clearJsCache()
        }
    }

}