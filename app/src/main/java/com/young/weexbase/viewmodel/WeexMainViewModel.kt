package com.young.weexbase.viewmodel

import android.content.ContentValues.TAG
import android.os.Environment
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.young.weexbase.R
import com.young.weexbase.ex.getStrValue
import com.young.weexbase.manager.WeexViewProxy
import com.young.weexbase.model.UpLoadBean
import com.young.weexbase.weex.application.WeexApplication
import com.young.weexbase.weex.dialog.DownProgressDialog
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.xutils.common.util.LogUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*

class WeexMainViewModel : BaseViewModel() {
    private val context by lazy { WeexViewProxy.instance().mWXSDKInstance?.context }
    val downApkTag by lazy { MutableLiveData<UpLoadBean>() }
    private val progressBar by lazy {
        DownProgressDialog(context).apply {
            setMax(100)
        }
    }

    fun downApk(map: Map<String, Any?>?) {
        if (null == map) {
            return
        }
        //参数获取
        progressBar.show()
        val downUrl = map.getStrValue("downUrl")
        val version = map.getStrValue("versionName")
        val force = map.getStrValue("isForce").equals("true")
        val path = "/BerrySchool/upgradeApk/" + (WeexApplication.application?.getString(R.string.app_name) + "_新版本_v_" + version) + ".apk"
        tokenApi.downloadFileUrl(downUrl).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                LogUtil.e(t.message)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                response.body()?.let {
                    launch {
                        val filePath = writeResponseBodyToDisk(it, path)
                        LogUtil.i("下载成功,文件路径: $filePath")
                        downApkTag.value = UpLoadBean(filePath, force)

                    }
                }
            }
        })
    }


    private fun writeResponseBodyToDisk(body: ResponseBody, filePath: String): String {
        val file = File(Environment.getExternalStorageDirectory(), filePath)
        Log.e(TAG, "writeResponseBodyToDisk() file=" + file.path)
        if (file.exists()) {
            file.delete()
        }
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        try {
            val fileReader = ByteArray(4096)
            val fileSize = body.contentLength()
            var fileSizeDownloaded: Long = 0
            inputStream = body.byteStream()
            outputStream = FileOutputStream(file)
            while (true) {
                val read: Int = inputStream.read(fileReader)
                if (read == -1) {
                    break
                }
                outputStream.write(fileReader, 0, read)
                fileSizeDownloaded += read.toLong()
                val progress = (100 * fileSizeDownloaded / fileSize).toInt()
                progressBar.setProgress(progress)
                Log.d(TAG, "file download: $fileSizeDownloaded of $fileSize")
            }
            outputStream?.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
        return file.absolutePath
    }


    fun installApk() {}
    fun cropImg() {}
}