package com.young.weexbase.model.apiAbout

import com.young.weexbase.BuildConfig.BASE_URL
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiHelper {



    /**
     * 无token
     */
    fun createTokenApi(): WithTokenApi {
        val builder = createClient(LogInterceptor())
        return init(builder)
    }

    /**
     * 默认的带基本配置带BaseURL
     *
     */
     inline fun <reified T> init(builder: OkHttpClient.Builder, mutableUrl: String? = null): T {
        builder.readTimeout(15, TimeUnit.SECONDS)
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
        val retrofit = Retrofit.Builder().baseUrl(mutableUrl ?: BASE_URL)
                .addConverterFactory(NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .client(builder.build())
                .build()
        return retrofit.create(T::class.java)
    }

    fun createClient(vararg interceptors: Interceptor): OkHttpClient.Builder {
        return httpsClient().apply {
            //dns(httpDns)
            cache(null)
            interceptors.forEach {
                this.addInterceptor(it)
            }
        }
    }

    /**
     * Https 信任所有证书
     */
    fun httpsClient(): OkHttpClient.Builder {
        val builder = OkHttpClient.Builder()
      /*  builder.hostnameVerifier { _, _ -> true }
        var trustAllCerts = arrayOf<TrustManager>(x509TrustManager)

        try {
            //构造自己的SSLContext
            val sc = SSLContext.getInstance("TLS")
            sc.init(null, trustAllCerts, java.security.SecureRandom())
            builder.sslSocketFactory(sc.socketFactory)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        builder.protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .build()*/

        return builder
    }

   /* private val x509TrustManager by lazy {
        object : X509TrustManager {
            @Throws(java.security.cert.CertificateException::class)
            override fun checkClientTrusted(
                    x509Certificates: Array<X509Certificate>,
                    s: String) {
            }

            @Throws(java.security.cert.CertificateException::class)
            override fun checkServerTrusted(
                    x509Certificates: Array<X509Certificate>,
                    s: String) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        }
    }*/

}