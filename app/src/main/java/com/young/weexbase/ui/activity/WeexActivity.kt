package com.young.weexbase.ui.activity

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.young.weexbase.BuildConfig
import com.young.weexbase.R
import com.young.weexbase.constants.DOMAIN_NAME
import com.young.weexbase.ex.parseData
import com.young.weexbase.manager.WeexViewProxy
import com.young.weexbase.model.UpLoadBean
import com.young.weexbase.utils.Eyes
import com.young.weexbase.viewmodel.WeexMainViewModel
import com.young.weexbase.weex.util.KeyStore
import kotlinx.android.synthetic.main.activity_weex.*
import org.xutils.common.util.LogUtil

class WeexActivity : WeexResultActivity() {

    private val weexMainModel by lazy {
        ViewModelProviders.of(this).get(WeexMainViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Eyes.transparencyBar(this)
        Eyes.setLightStatusBar(this, true)
        injectLiveData()

    }

    override fun createContentView(): Int {
        return R.layout.activity_weex
    }

    private fun injectLiveData() {
        val downApkTagObserver = Observer<UpLoadBean> {
            installApk(it)
        }
        weexMainModel.downApkTag.observe(this, downApkTagObserver)
    }


    override fun doWorkBeforeInit() {
        WeexViewProxy.instance().apply {
            injectRootView(weex_content)
            injectErrorView(View.inflate(this@WeexActivity, R.layout.activity_weex_error, null)) {
                it.findViewById<LinearLayout>(R.id.weex_no_data).setOnTouchListener { v, event ->
                    if (event.action == MotionEvent.ACTION_DOWN)
                        LogUtil.d("viewId_nocontent = ${it.id}")
                    reLoad()
                    true

                }

            }
        }
    }

    override fun getUrlAndData(doNext: (String, Map<String, Any?>) -> Unit) {
        val url = getUrl()
        val data = parseData()
        LogUtil.d("url = $url     data = $data")
        doNext(url, data)
    }


    /**
     * 1 、来自welcome  ->  登录/主页
     * 2 、非welcome  ->  子页面
     */
    private fun getUrl(): String {
        var url = KeyStore.getInstance(this).get(DOMAIN_NAME, BuildConfig.BASE_PAGE_URL)
        defaultPage = intent.getStringExtra(PAGE) ?: defaultPage
        return if (url.endsWith("/")) {
            "${url}dist/app/$defaultPage"
        } else {
            "$url/dist/app/$defaultPage"
        }
    }


}
