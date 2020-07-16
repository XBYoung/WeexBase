package com.young.weexbase.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import com.young.weexbase.BuildConfig
import com.young.weexbase.R
import com.young.weexbase.constants.*
import com.young.weexbase.ex.*
import com.young.weexbase.manager.UpdateManager
import com.young.weexbase.ui.activity.WeexBaseActivity.Companion.PAGE
import com.young.weexbase.utils.Eyes
import com.young.weexbase.utils.FileUtils
import com.young.weexbase.weex.application.WeexApplication
import com.young.weexbase.weex.util.KeyStore
import com.young.weexbase.weex.util.PackageUtils
import com.young.weexbase.weex.util.cache.FileCache
import com.young.weexbase.weex.util.cache.ImageFileCache
import kotlinx.android.synthetic.main.activity_debug.*
import kotlinx.coroutines.*
import org.xutils.common.util.LogUtil
import permissions.dispatcher.*
import java.io.File

@RuntimePermissions
class DebugActivity : AppCompatActivity(), CoroutineScope by MainScope() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug)
        Eyes.transparencyBar(this)
        Eyes.setLightStatusBar(this, true)
        configPageMore.visibility = View.INVISIBLE

        launch {
            UpdateManager.checkUpdated(this@DebugActivity.applicationContext)
        }
        KeyStore.getInstance(this).get(DOMAIN_NAME, BuildConfig.BASE_PAGE_URL)?.let {
            if (it.protocolExist()) {
                inputPageHttps.isChecked = it.isHttps()
                inputPage.text = toEditable(KeyStore.getInstance(this).get(DOMAIN_NAME, BuildConfig.BASE_PAGE_URL).removeProtocol())
            }
        }

        sdPage.click {
            FROM_SD = true
            launch {
                copyFileFromAssert()
                goMainPage()
            }
            startApp(JS_CACHE_DIR + File.separator)
        }

        clearData.click {
            clearCache()
        }
        configPage.click {
            configPageMore.visibility = if (configPageMore.visibility == View.INVISIBLE) View.VISIBLE else View.INVISIBLE
        }
        clearInputPage.click { inputPage.setText("") }
        clearInputPagePath.click { inputPagePath.setText("") }

        confirm.click {
            FROM_SD = false
            startApp(inputPage.text.toString())
        }

    }

    private fun copyFileFromAssert() {
        WeexApplication.application?.baseContext?.let { context ->
            FileUtils.copyAssetsToSd(context, "weex")
        }
    }

    private fun goMainPage() {
        //计算总时间，此页面至少延迟2秒
        launch {
            KeyStore.getInstance(this@DebugActivity).put(DOMAIN_NAME, JS_CACHE_DIR)
            //进入主页
            var intent = Intent(this@DebugActivity, WeexActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    /**
     *         删除缓存文件
     */
    private fun clearCache() {
        FileCache.deleteCacheFile()
        ImageFileCache.getInstance().clearCache()
        KeyStore.getInstance(this@DebugActivity).clear()
    }

    private fun toEditable(text: String): Editable {
        return Editable.Factory.getInstance().newEditable(text)
    }


    /**
     * 开启
     */
    private fun startApp(serverAddress: String) {
        var intent = Intent(this, WeexActivity::class.java)

        //去掉域名前 http
        var serve = if (FROM_SD) {
            serverAddress
        } else {
            val page = inputPagePath.text.toString()
            if (!page.isNullOrBlank()){
                intent.putExtra(PAGE,page)
            }

            if (!serverAddress.protocolExist()) {
                serverAddress.appendProtocol(inputPageHttps.isChecked)
            } else {
                serverAddress
            }

        }
        inputPage.text = toEditable(serve)
        KeyStore.getInstance(this).put(DOMAIN_NAME, serve)
        startActivity(intent)
    }

    @SuppressLint("MissingPermission")
    @NeedsPermission(Manifest.permission.READ_PHONE_STATE)
    fun afterReadStatus() {

    }

    @OnPermissionDenied(Manifest.permission.READ_PHONE_STATE)
    fun permissionDenied() {
        LogUtil.d("permission,  permissionDenied")
        showPermissionDialog(View.OnClickListener { view: View? -> PackageUtils.jumpDetailSettingIntent(this) })
    }

    @OnNeverAskAgain(Manifest.permission.READ_PHONE_STATE)
    fun permissionNeverAsk() {
        LogUtil.d("permission,  permissionNeverAsk")
        showPermissionDialog(View.OnClickListener { view: View? -> PackageUtils.jumpDetailSettingIntent(this) })
    }

    @OnShowRationale(Manifest.permission.READ_PHONE_STATE)
    fun showRationale(request: PermissionRequest) {
        LogUtil.d("permission,  showRationale")
        showPermissionDialog(View.OnClickListener { view: View? -> request.proceed() })
    }

    private fun showPermissionDialog(listener: View.OnClickListener) {
        val dialog = Dialog(this, R.style.AlertDialog)
        val parent: View = LayoutInflater.from(this).inflate(R.layout.dialog_permission_need, null)
        dialog.setContentView(parent)
        dialog.setCancelable(true)
        dialog.show()
        parent.findViewById<View>(R.id.nextBtn).setOnClickListener { view ->
            dialog.dismiss()
            listener.onClick(view)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}