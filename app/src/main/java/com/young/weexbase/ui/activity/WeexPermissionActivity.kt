package com.young.weexbase.ui.activity


import android.Manifest
import android.os.Build
import android.widget.Toast
import org.apache.weex.utils.WXLogUtils
import permissions.dispatcher.*
import kotlin.jvm.functions.Function0


/**
 * 权限管理注册,使用时调用
 */
@RuntimePermissions
abstract class WeexPermissionActivity : WeexBaseActivity() {


    @NeedsPermission(Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO)
    fun pickImage(doWork: Function0<Unit>) {
        doWork()
    }

    @NeedsPermission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
    fun pickVideo(doWork: Function0<Unit>) {
        doWork()
    }

    /**
     * 第一次拒绝并没有勾选不在询问之后请求权限
     */
    @OnShowRationale(Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO)
    fun requestPermissionAgain(request: PermissionRequest) {
        Toast.makeText(this, "缺少权限将无法使用该功能", Toast.LENGTH_LONG).show()
        request.proceed()
    }

    /**
     * 申请权限被拒绝
     */
    @OnPermissionDenied(Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO)
    fun alertAfterDenied() {
        Toast.makeText(this, "缺少权限将无法使用该功能", Toast.LENGTH_LONG).show()
        WXLogUtils.d("permission", "OnPermissionDenied")
    }

    /**
     * 申请权限被拒绝，并且勾选不在询问
     */
    @OnNeverAskAgain(Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO)
    fun alertAfterNeverAsk() {
        WXLogUtils.d("permission", "OnNeverAskAgain")
        Toast.makeText(this, "获取权限失败，请到设置页面打开相关权限", Toast.LENGTH_LONG).show()
    }


    /**
     * 是否具有安装apk权限
     */
    fun hadInstallApkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            true
        } else {
            packageManager.canRequestPackageInstalls()
        }
    }
}
