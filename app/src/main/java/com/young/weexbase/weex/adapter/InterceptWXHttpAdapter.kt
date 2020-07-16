package com.young.weexbase.weex.adapter

import androidx.annotation.Nullable
import com.taobao.weex.urlconnection.ByteArrayRequestEntity
import com.taobao.weex.urlconnection.SimpleRequestEntity
import com.taobao.weex.urlconnection.WeexURLConnectionManager
import org.apache.weex.adapter.DefaultWXHttpAdapter

import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection


class InterceptWXHttpAdapter : DefaultWXHttpAdapter() {
    override fun getEventReporterDelegate(): IEventReporterDelegate {
        return object : IEventReporterDelegate {
            var manager = WeexURLConnectionManager(null)
            override fun preConnect(connection: HttpURLConnection?, @Nullable body: String?) {
                var requestEntity: SimpleRequestEntity? = null
                if (body != null) {
                    requestEntity = ByteArrayRequestEntity(body.toByteArray())
                }
                try {
                    manager.preConnect(connection, requestEntity)
                } catch (throwable: Throwable) {
                    manager.httpExchangeFailed(IOException("Exception on preConnect", throwable))
                }
            }

            override fun postConnect() {
                try {
                    manager.postConnect()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun interpretResponseStream(@Nullable inputStream: InputStream?): InputStream {
                return manager.interpretResponseStream(inputStream)
            }

            override fun httpExchangeFailed(e: IOException?) {
                manager.httpExchangeFailed(e)
            }
        }
    }
}