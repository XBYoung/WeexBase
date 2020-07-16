package com.young.weexbase.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.young.weexbase.constants.FROM_SD
import com.young.weexbase.manager.WeexViewProxy
import com.google.gson.Gson
import org.apache.weex.WXSDKInstance
import java.lang.ref.WeakReference

abstract class WeexBaseActivity : AppCompatActivity() {
    companion object {
        const val DATA = "data"
        const val PAGE = "page"

        /**返回路径 */
        const val TO_BACK_PAGE = "toBackPage"

        /**返回仅刷新数据*/
        const val ONLY_REFRESH_DATA = "onlyRefreshData"

        /**主页*/
        const val MAIN_PAGE = "index.js"

        /**登录页*/
        const val LOGIN_PAGE = "account/login.js"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(createContentView())
        doWorkBeforeInit()
        getUrlAndData { url, data ->
            weexByUrl(url, data)
        }
    }


    /**页面 */
    var defaultPage: String? = MAIN_PAGE

    /**页面地址 */
    var pageUrl: String = ""

    /**返回键标示 */


    open val mWXSDKInstance: WXSDKInstance by lazy {
        WeexViewProxy.instance().mWXSDKInstance!!
    }


    private fun initWeex() {
        WeexViewProxy.instance().initWeexSdk(WeakReference(this))
    }

    abstract fun createContentView(): Int

    /**
     * 获取路径参数
     */
    abstract fun getUrlAndData(doNext: (String, Map<String, Any?>) -> Unit)


    abstract fun doWorkBeforeInit()

    fun reLoad() {
        getUrlAndData { url, data ->
            weexByUrl(url, data)
        }
    }

    /**
     * 加载页面
     */
    fun weexByUrl(url: String, options: Map<String, Any?>) {
        var jsonInitData: String? = null
        if (options != null) {
            jsonInitData = Gson().toJson(options)
        }
        initWeex()
        val page = if (FROM_SD) "file://$url" else url
        Log.d("pageUrl", page)
        WeexViewProxy.instance().render(page, jsonInitData)
        //  mWXSDKInstance.renderByUrl(resources.getString(R.string.app_name), page, HashMap(), jsonInitData, WXRenderStrategy.APPEND_ASYNC)

    }


    override fun onResume() {
        super.onResume()
        mWXSDKInstance?.let {
            it?.onActivityResume()
        }

    }


    override fun onPause() {
        super.onPause()
        mWXSDKInstance?.let {
            it.onActivityPause()
        }
    }

    override fun onStop() {
        super.onStop()
        if (mWXSDKInstance != null) {
            mWXSDKInstance.onActivityStop()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mWXSDKInstance != null) {
            mWXSDKInstance.onActivityDestroy()
        }

    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
           WeexViewProxy.instance().exitPage()
        }
        return super.onKeyDown(keyCode, event)
    }


}