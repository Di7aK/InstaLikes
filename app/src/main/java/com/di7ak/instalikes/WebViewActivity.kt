package com.di7ak.instalikes

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.webkit.*
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewActivity : AppCompatActivity(), MyWebViewClient.WebViewClientListener {
    companion object {
        const val EXTRA_USER_AGENT = "user_agent"
        const val EXTRA_COOKIE = "cookie"
        const val EXTRA_URL = "cookie"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        loadUrlInWebView(intent.getStringExtra(EXTRA_URL)!!, intent.getStringExtra(EXTRA_USER_AGENT)!!, intent.getStringExtra(
            EXTRA_COOKIE)!!)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadUrlInWebView(url: String, userAgent: String, cookieString: String) {
        webView.settings.apply {
            builtInZoomControls = false
            javaScriptEnabled = true
            useWideViewPort = true
            loadWithOverviewMode = true
            setSupportMultipleWindows(false)
        }
        CookieManager.getInstance().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setAcceptThirdPartyCookies(webView, true)
                removeAllCookies {  }
            }
        }

        webView.apply {
            isVerticalScrollBarEnabled = true
            isHorizontalScrollBarEnabled = true
            webViewClient = MyWebViewClient(this@WebViewActivity, this@WebViewActivity)
            loadUrl(
                url,
                mutableMapOf(
                    "Cookie" to cookieString,
                    "User-Agent" to userAgent
                )
            )
        }
    }

    override fun onStartLoad(url: String?) {

    }

    override fun onLoaded() {

    }

    override fun onError() {

    }

}

class MyWebViewClient(private val context: Context, private val listener: WebViewClientListener) : WebViewClient() {

    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
        super.onReceivedError(view, request, error)

        Handler(Looper.getMainLooper()).post {
            listener.onError()
        }
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)

        Handler(Looper.getMainLooper()).post {
            listener.onStartLoad(url)
        }
    }

    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        if (URLUtil.isNetworkUrl(url)) {
            return false
        }

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
        return true
    }

    override fun onPageCommitVisible(view: WebView?, url: String?) {
        super.onPageCommitVisible(view, url)

        Handler(Looper.getMainLooper()).post {
            listener.onLoaded()
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest): Boolean {
        val url = request.url.toString()
        if (URLUtil.isNetworkUrl(url)) {
            return false
        }

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
        return true
    }

    interface WebViewClientListener {
        fun onStartLoad(url: String?)

        fun onLoaded()

        fun onError()
    }
}
