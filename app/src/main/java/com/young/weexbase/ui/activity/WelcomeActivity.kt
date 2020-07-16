package com.young.weexbase.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.young.weexbase.BuildConfig
import com.young.weexbase.R
import com.young.weexbase.constants.*
import com.young.weexbase.manager.UpdateManager
import com.young.weexbase.utils.Eyes
import com.young.weexbase.weex.util.KeyStore
import org.apache.weex.utils.WXLogUtils
import kotlinx.android.synthetic.main.dialog_permission_need.view.*
import kotlinx.coroutines.*
import org.xutils.common.util.LogUtil
import permissions.dispatcher.*

@RuntimePermissions
class WelcomeActivity : AppCompatActivity(), CoroutineScope by MainScope() {


    companion object {
        //欢迎页等待时间
        private var TIME_WAITTING: Long = 2000
    }

    /**
     * 第一次申请权限并且通过
     */
    @NeedsPermission(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE)
    fun necessaryPermission() {
        toNextPage()
    }


    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }


    /**
     * 第一次拒绝并没有勾选不在询问之后请求权限
     */
    @OnShowRationale(Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE)
    fun requestPermissionAgain(request: PermissionRequest) {
        showCustomDialog(R.layout.dialog_permission_need) { view, dialog ->
            view.nextBtn.setOnClickListener {
                dialog.dismiss()
                request.proceed()
            }
        }
    }

    /**
     * 申请权限被拒绝
     */
    @OnPermissionDenied(Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE)
    fun alertAfterDenied() {
        Toast.makeText(this, getString(R.string.some_permission_unuseful), Toast.LENGTH_LONG).show()
        WXLogUtils.d("permission", "OnPermissionDenied")
        toNextPage()
    }

    /**
     * 申请权限被拒绝，并且勾选不在询问
     */
    @OnNeverAskAgain(Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE)
    fun alertAfterNeverAsk() {
        WXLogUtils.d("permission", "OnNeverAskAgain")
        Toast.makeText(this, "定位或存储权限缺失将部分无法使用", Toast.LENGTH_LONG).show()
        toNextPage()
    }


    private fun showCustomDialog(@LayoutRes dialogRes: Int, initView: (View, Dialog) -> Unit) {
        val dialog = Dialog(this, R.style.DialogTheme)
        val parent = LayoutInflater.from(this).inflate(dialogRes, null)
        initView(parent, dialog)
        dialog.setContentView(parent)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.show()
    }


    private fun toNextPage() {
        if (FROM_SD) {
            runCatching {
                launch {
                    UpdateManager.checkUpdated(this@WelcomeActivity)
                    delay(TIME_WAITTING)
                    goMainPage()
                }
            }.onFailure {
                goMainPage()
            }
        } else {
            goDebugPage()
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome2)
        Eyes.transparencyBar(this)
        Eyes.setLightStatusBar(this, true)
        FROM_SD = !BuildConfig.DEBUG
        LogUtil.d("onCreate  $JS_CACHE_DIR")
        launch {
            delay(300)
            necessaryPermissionWithPermissionCheck()
        }
    }


    private fun goMainPage() {
        //计算总时间，此页面至少延迟2秒
        launch {
            KeyStore.getInstance(this@WelcomeActivity).put(DOMAIN_NAME, JS_CACHE_DIR)
            //进入主页
            var intent = Intent(this@WelcomeActivity, WeexActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun goDebugPage() {
        launch {
            delay(TIME_WAITTING)
            //进入主页
            var intent = Intent(this@WelcomeActivity, DebugActivity::class.java)
            startActivity(intent)
            finish()
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}