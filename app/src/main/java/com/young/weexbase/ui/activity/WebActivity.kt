package com.young.weexbase.ui.activity

import android.annotation.SuppressLint
import android.net.http.SslError
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.*
import com.young.weexbase.R
import kotlinx.android.synthetic.main.activity_web2.*
import android.content.Intent
import android.net.Uri


class WebActivity : AppCompatActivity() {
    companion object {
        const val WEB_TAG = "web_tag"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web2)
        initView()

    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initView() {
        backIv.setOnClickListener {
            super.finish()
        }
        mWeb.webViewClient = MyClient()
        mWeb.settings.javaScriptEnabled = true
        var path = intent.getStringExtra(WEB_TAG)
        Log.d("DemoApp", "path = $path")
        mWeb.loadUrl(path)
    }

    override fun finish() {
        if (mWeb.canGoBack()) {
            mWeb.goBack()
            return
        } else {
            super.finish()
        }
    }

    inner class MyClient : WebViewClient() {
        override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
            handler?.proceed()
        }

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            try {
                if (!url.startsWith("http:") && !url.startsWith("https:")) {
                    val intent = Intent(Intent.ACTION_VIEW,
                            Uri.parse(url))
                    startActivity(intent)
                    return true
                }
            } catch (e: Exception) {
                return false
            }
            view.loadUrl(url)
            return true

        }
    }
}
