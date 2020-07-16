package com.young.weexbase.utils

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.FileProvider
import org.xutils.common.util.LogUtil
import java.io.File

object ImageUtils {
    /**
     * 裁剪图片
     * @param imgPath
     */
    fun openCrop(imgPath: String, type: Int, activity: Activity, requstCode: Int,outPathGet:(String)->Unit) {
        val intent = Intent("com.android.camera.action.CROP")
        // 源文件
        var uri: Uri? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(activity, "${activity.packageName}.fileprovider", File(imgPath))
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            uri = Uri.fromFile(File(imgPath))
        }
        intent.setDataAndType(uri, "image/*")
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true")
        // 不返回数据
        intent.putExtra("return-data", false)
        //剪切的文件放在外部缓存中
        val outFile = File(activity.externalCacheDir.toString() + "/CropImage" + System.currentTimeMillis() + ".jpg")
        LogUtil.d("uploadImg ${outFile.absolutePath}")
        /*    if (!outFile.exists()){
                outFile.mkdir()
            }*/
        // 指定照片保存路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outFile))
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
        outPathGet(outFile.absolutePath)
        if (type == 1) {
            // aspectX aspectY 是宽高的比例
            intent.putExtra("aspectX", 1)
            intent.putExtra("aspectY", 1)
        }
        if (type == 2) {
            // aspectX aspectY 是宽高的比例
            intent.putExtra("aspectX", 16)
            intent.putExtra("aspectY", 9)
        }
        intent.putExtra("scale", true)
        intent.putExtra("noFaceDetection", true)
        //添加这一句表示对目标应用临时授权该Uri所代表的文件
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        //进入裁剪页面
        activity.startActivityForResult(intent, requstCode)
    }
}