package com.young.weexbase.model.apiAbout

import com.young.weexbase.model.CoroutineCallback
import com.young.weexbase.model.toStringNotNull
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Request
import okhttp3.Response
import org.xutils.common.util.LogUtil
import retrofit2.Call
import java.nio.charset.Charset

fun Response.log() {
    val source = this.body()?.source()
    source?.request(Long.MAX_VALUE)
    val buffer = source?.buffer()
    val readString = buffer?.clone()?.readString(Charset.forName("UTF-8"))
    LogUtil.d(TAG_HTTP +"  "+readString.toStringNotNull() + "  log >>>>>")

}

fun Request.log(): String {
    val copy = this?.newBuilder()?.build()
    val buffer = okio.Buffer()
    copy?.body()?.writeTo(buffer)
    return buffer.readUtf8()
}

/**
 * 半封装
 */
suspend fun <T> Call<T>.createCallBack() = suspendCancellableCoroutine<T> {
    this.enqueue(CoroutineCallback<T>(it))

}
