package com.young.weexbase.manager

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import com.young.weexbase.R
import com.young.weexbase.weex.dialog.WaitingDialog
import org.apache.weex.IWXRenderListener
import org.apache.weex.WXSDKInstance
import org.apache.weex.common.WXRenderStrategy
import org.apache.weex.utils.WXLogUtils
import java.lang.ref.WeakReference
import java.util.HashMap

class WeexViewProxy : IWXRenderListener {
    private lateinit var context: Context

    companion object {
        @JvmStatic
        fun instance() = Inner.proxy
    }

    class Inner {
        companion object {
            val proxy = WeexViewProxy()
        }
    }

    /**等待弹出框 */
    lateinit var waitingDialog: WaitingDialog
    private var load: ((View) -> Unit)? = null


    /**拦截返回键*/
    var mWXSDKInstance: WXSDKInstance? = null
    lateinit var rootView: FrameLayout
    private var errorView: View? = null
    private var contentView: View? = null

    fun initWeexSdk(context: WeakReference<Context>) {
        context.get()?.let {
            this.context = it
            mWXSDKInstance = WXSDKInstance(it)
            mWXSDKInstance?.registerRenderListener(this)

        }

    }

    /**
     * 注册根View
     */
    fun injectRootView(view: FrameLayout) {
        rootView = view
    }

    fun injectErrorView(view: View, reload: (View) -> Unit) {

        if (view != null) {
            errorView = view
            load = reload
            load?.invoke(errorView!!)
            errorView?.setOnClickListener {
                load?.invoke(errorView!!)
            }

        }
    }

    /**
     * 替换内容View
     */
    fun converView(view: View?) {
        view?.let {
            if (rootView.childCount > 0) {
                val alphaAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_alpha_out)
                rootView.getChildAt(0).startAnimation(alphaAnimation)
                rootView.removeAllViewsInLayout()
            }
            rootView.addView(it, FrameLayout.LayoutParams(-1, -1))
            it.requestLayout()
            it.requestFocus()
            val alphaAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_alpha_in)
            it.startAnimation(alphaAnimation)
        }
    }

    private fun converSuccess() {
        converView(contentView)

    }

    private fun converError() {
        converView(errorView)
        errorView?.let {
            load?.invoke(it)
        }
    }


    fun render(pageUrl: String, dataJson: String?) {
        Log.d("pageUrl", "$pageUrl   data = $dataJson")
        waitingDialog = WaitingDialog(context)
        waitingDialog.show()
        mWXSDKInstance?.renderByUrl(context.resources.getString(R.string.app_name), pageUrl, HashMap(), dataJson, WXRenderStrategy.APPEND_ASYNC)
    }


    fun exitPage(): Boolean {
        if (rootView?.getChildAt(0) == errorView) {
            (context as Activity).finish()
        } else {
            if (mWXSDKInstance != null) {
                mWXSDKInstance?.fireGlobalEventCallback("toBack", HashMap())
                return true
            }
        }
        return false
    }


    override fun onRefreshSuccess(instance: WXSDKInstance?, width: Int, height: Int) {
        waitingDialog.dismiss()
    }

    override fun onException(instance: WXSDKInstance?, errCode: String?, msg: String?) {
        WXLogUtils.d("renderEx errCode = $errCode  msg = $msg")
        converError()
        waitingDialog.dismiss()
    }

    override fun onViewCreated(instance: WXSDKInstance?, view: View?) {
        contentView = view
    }

    override fun onRenderSuccess(instance: WXSDKInstance?, width: Int, height: Int) {
        WXLogUtils.d("renderEx Success")
        converSuccess()
        waitingDialog.dismiss()
    }
}