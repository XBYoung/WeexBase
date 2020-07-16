package com.young.weexbase.ui.activity

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.text.TextUtils
import com.young.weexbase.R
import com.young.weexbase.ex.getStrValue
import com.young.weexbase.ex.parseData
import com.young.weexbase.model.UpLoadBean
import com.young.weexbase.utils.ImageUtils
import com.young.weexbase.weex.dialog.ConfirmDialog
import com.young.weexbase.weex.util.DeviceInfoUtil
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import org.xutils.common.util.LogUtil
import java.io.File
import java.util.*


/**
 * 处理所有result任务
 */
abstract class WeexResultActivity : WeexPermissionActivity() {

    companion object {
        /**页面返回 */
        const val REQUEST_CODE_BACK_PAGE = 100

        /**图片裁剪 */
        const val REQUEST_CROP = 101

        /**去设置打开安装权限 返回*/
        var REQUEST_CODE_INSTALL_APK = 102

    }

    /**裁剪图片地址*/
    var cropImagePath = ""

    /**
     * 裁剪图片
     * @param imgPath
     */
    fun openCrop(imgPath: String, type: Int) {
        ImageUtils.openCrop(imgPath, type, this, REQUEST_CROP) {
            cropImagePath = it
        }
    }

    var upLoadBean: UpLoadBean? = null

    fun installApk(upLoadBean: UpLoadBean) {
        this.upLoadBean = upLoadBean
        if (hadInstallApkPermission()) {
            DeviceInfoUtil.installApk(this, File(upLoadBean.apkPath))
            finish()
        } else {
            requestInstallApkPermission(upLoadBean.force)
        }
    }

    private fun requestInstallApkPermission(isForce: Boolean) {
        ConfirmDialog(this, "升级新版本，请赋予安装应用权限！", false, "去设置", if (isForce) "退出" else "暂不升级", DialogInterface.OnClickListener { p0, p1 ->
            if (p1 == 1) {
                val packageURI = Uri.parse("package:$packageName")
                var intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI)
                startActivityForResult(intent, REQUEST_CODE_INSTALL_APK)
            } else if (isForce) {
                finish()
            }
        }).show()
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //文件选择过滤
           // SelectMediaUtil.onActivityResult(this, requestCode, resultCode, data)
            //VideoSelectUtil.onActivityResult(this, requestCode, resultCode, data)
            when (requestCode) {
                REQUEST_CODE_INSTALL_APK -> {
                    upLoadBean?.let {
                        installApk(it)
                    }
                }

                REQUEST_CROP -> {
                    val cropData = Intent()
                    val cropPaths = ArrayList<String>()
                    cropPaths.add(cropImagePath)
                    LogUtil.d("uploadImg exist = ${File(cropImagePath).exists()}   $cropImagePath")
                   // cropData.putStringArrayListExtra(SelectMediaActivity.EXTRA_RESULT, cropPaths)
                    //文件选择过滤
                 //  SelectMediaUtil.onActivityResult(this, SelectMediaUtil.RECORDER_SELECTMEDIA_REQUEST_CODE, resultCode, cropData)
                }

                REQUEST_CODE_BACK_PAGE -> {
                    data?.getStringExtra(DATA)?.let { dataStr ->
                        var map: MutableMap<String, Any?>
                        map = try {
                            if (dataStr == null || dataStr.trim { it <= ' ' }.isEmpty() || TextUtils.equals(dataStr, "null")) {
                                mutableMapOf()
                            } else {
                                Gson().fromJson<MutableMap<String, Any?>>(dataStr, MutableMap::class.java)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            mutableMapOf()
                        }
                        val toBackPage = map.getStrValue(TO_BACK_PAGE)
                        val onlyRefreshData = map.getStrValue(ONLY_REFRESH_DATA)
                        //返回的参数
                        val param = map["param"]
                        map = if (param != null && param is LinkedTreeMap<*, *>) {
                            param as MutableMap<String, Any?>
                        } else {
                            mutableMapOf()
                        }
                        if (toBackPage != null && TextUtils.equals(toBackPage, defaultPage)) {
                            //上一页面数据
                            val pageDate: MutableMap<String, Any?> = parseData() as MutableMap<String, Any?>
                            //添加修改数据
                            pageDate.putAll(map)
                            if (TextUtils.equals("true", onlyRefreshData)) {
                                //刷新数据
                                mWXSDKInstance.refreshInstance(pageDate)
                            } else {
                                //刷新页面
                                try {
                                    weexByUrl(pageUrl, pageDate)
                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                }
                            }
                        } else {
                            try {
                                val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                                if (manager.getRunningTasks(1).size <= 1) {
                                    openNewTaskPage(this)
                                    return
                                }
                            } catch (e: SecurityException) {
                                e.printStackTrace()
                                openNewTaskPage(this)
                                return
                            }
                            //继续返回
                            setResult(Activity.RESULT_OK, data)
                            finish()
                            overridePendingTransition(R.anim.anim_alpha_in, R.anim.anim_alpha_out)
                        }
                    }
                }
            }

        }
    }

    private fun openNewTaskPage(activity: Activity) {
        val intent = Intent(activity, WeexActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        activity.startActivity(intent)
        activity.finish()
        activity.overridePendingTransition(R.anim.anim_alpha_in, R.anim.anim_alpha_out)
    }
}