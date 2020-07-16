package com.young.weexbase.weex.module

import android.app.*
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import com.young.weexbase.R
import com.young.weexbase.ex.getStrValue
import com.young.weexbase.manager.WeexViewProxy
import com.young.weexbase.ui.activity.WebActivity
import com.young.weexbase.ui.activity.WeexActivity
import com.young.weexbase.ui.activity.WeexBaseActivity.Companion.DATA
import com.young.weexbase.ui.activity.WeexBaseActivity.Companion.PAGE
import com.young.weexbase.ui.activity.WeexResultActivity.Companion.REQUEST_CODE_BACK_PAGE
import com.young.weexbase.utils.StatusBarUtils.getStatusBarHeight
import com.google.gson.Gson
import org.apache.weex.WXSDKInstance
import org.apache.weex.bridge.JSCallback
import org.xutils.common.util.LogUtil
import java.util.*

object AppModuleLogic {
    @JvmStatic
    fun weexLog(log: String?) {
        Log.d("weex_", log)
    }

    @JvmStatic
    fun getSafeArea(mWXSDKInstance: WXSDKInstance, callback: JSCallback) {
        if (mWXSDKInstance.context is WeexActivity) {
            val params: MutableMap<String, Int> = HashMap()
            val statusHeight = getStatusBarHeight(mWXSDKInstance.context)
            LogUtil.d("statusHeight $statusHeight")
            params["top"] = statusHeight
            params["bottom"] = 0
            callback.invoke(params)
        }
    }

    /**
     * 开启页面
     *
     * @param mWXSDKInstance
     * @param page
     * @param map
     */
    @JvmStatic
    fun openPage(mWXSDKInstance: WXSDKInstance, page: String?, map: Map<String, Any?>?) {
        if (TextUtils.isEmpty(page)) {
            return
        }
        if (mWXSDKInstance.context is WeexActivity) {
            val activity = mWXSDKInstance.context as Activity
            //首页操作判断
            val operate = map.getStrValue("operate")
            val toSys = map.getStrValue("toSys")
            if (toSys == "true") {
                if (page != null && (page.startsWith("http://") || page.startsWith("https://"))) {
                    //系统页面打开
                    val intent = Intent()
                    intent.action = "android.intent.action.VIEW"
                    val content_url = Uri.parse(page)
                    Log.d("demoApp", "page = $page      content_url = $content_url")
                    intent.data = content_url
                    activity.startActivity(intent)
                    return
                }
            }
            when (operate) {
                "main" -> {
                    val intent = Intent(activity, WeexActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.putExtra(PAGE, page)
                    if (map != null) {
                        intent.putExtra(DATA, Gson().toJson(map).toString())
                    }
                    activity.startActivity(intent)
                    activity.overridePendingTransition(R.anim.anim_alpha_in, R.anim.anim_alpha_out)
                }
                "web" -> {
                    val intent = Intent(activity, WebActivity::class.java)
                    intent.putExtra(WebActivity.WEB_TAG, page)
                    activity.startActivity(intent)
                }
                else -> {
                    val intent = Intent(activity, WeexActivity::class.java)
                    intent.putExtra(PAGE, page)
                    if (map != null) {
                        intent.putExtra(DATA, Gson().toJson(map).toString())
                    }
                    activity.startActivityForResult(intent, REQUEST_CODE_BACK_PAGE)
                    activity.overridePendingTransition(R.anim.anim_alpha_in, R.anim.anim_alpha_out)
                }
            }
        }
    }

    /**
     * 返回页面
     *
     * @param mWXSDKInstance
     * @param map
     */
    @JvmStatic
    fun toBack(mWXSDKInstance: WXSDKInstance, map: Map<String?, Any?>?) {
        if (mWXSDKInstance.context is WeexActivity) {
            val activity = mWXSDKInstance.context as Activity
            if (map == null || getMapValue(map, "toBackPage").isNullOrEmpty()) {
                activity.setResult(RESULT_CANCELED)
            } else {
                val intent = Intent()
                intent.putExtra(DATA, Gson().toJson(map).toString())
                activity.setResult(RESULT_OK, intent)
            }
            activity.finish()
            activity.overridePendingTransition(R.anim.anim_alpha_in, R.anim.anim_alpha_out)
        }
    }


    /**
     * 弹出系统等待框
     *
     * @param mWXSDKInstance
     * @param waitting
     */
    @JvmStatic
    fun startWaitting(mWXSDKInstance: WXSDKInstance, waitting: String?) {
        if (mWXSDKInstance.context is WeexActivity) {
            WeexViewProxy.instance().waitingDialog?.show()
        }
    }

    /**
     * 关闭系统等待框
     *
     * @param mWXSDKInstance
     */
    @JvmStatic
    fun stopWaitting(mWXSDKInstance: WXSDKInstance) {
        if (mWXSDKInstance.context is WeexActivity) {
            WeexViewProxy.instance().waitingDialog?.dismiss()
        }
    }


    /**
     * 获取图片
     *
     * @param mWXSDKInstance
     * @param map
     * @param callback
     */
    @JvmStatic
    fun pickImage(mWXSDKInstance: WXSDKInstance, map: Map<String, Any?>?, callback: JSCallback) {

    }

    /**
     * 获取视频
     *
     * @param mWXSDKInstance
     * @param map
     * @param callback       "{
     * maxTime: 视频时长
     * maxSize: byte
     * width: 视频宽
     * height: 视频高
     * crop: true/false 是否裁剪
     * }"
     */
    @JvmStatic
    fun pickVideo(mWXSDKInstance: WXSDKInstance, map: Map<String, Any?>?, callback: JSCallback?) {

    }


    /**以下是工具方法 */
    /**
     * 获取MAP字段的String格式
     *
     * @param map
     * @param key
     * @param defValue
     * @return
     */
    @JvmStatic
    fun getMapValue(map: Map<*, *>?, key: String?, defValue: String =""): String {
        if (map != null) {
            val obj = map[key]
            if (obj != null) {
                if (obj.toString().isNotEmpty()) {
                    return obj.toString()
                }
            }
        }
        return defValue
    }
}