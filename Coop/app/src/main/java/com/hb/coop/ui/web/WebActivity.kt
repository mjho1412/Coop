package com.hb.coop.ui.web

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.widget.Toolbar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import butterknife.BindView
import butterknife.OnClick
import com.hb.coop.R
import com.hb.lib.mvp.impl.lce.HBMvpLceActivity
import com.hb.lib.utils.ui.ThemeUtils
import timber.log.Timber

class WebActivity : HBMvpLceActivity<SwipeRefreshLayout, String, WebPresenter>(), WebContract.View,
    SwipeRefreshLayout.OnRefreshListener {

    override fun getResLayoutId(): Int = R.layout.activity_webview

    @BindView(R.id.webview)
    lateinit var mWebView: WebView

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupWebView()
        loadData(false)

        setSupportActionBar(toolbar)


        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)

            supportActionBar!!.title = intent?.extras?.getString(WebContract.TITLE_TAG)
        }

        mLceViewHolder.contentView.setOnRefreshListener(this)

        MAX_TOUCH = ThemeUtils.dpToPx(this, 40)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onRefresh() {
        loadData(true)
    }

    private var startX = 0.0f
    private var startY = 0.0f
    private var isClicked = false

    var MAX_TOUCH: Int = 0

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    override fun setupWebView() {
        val webSettings = mWebView.settings
        webSettings.setAppCacheEnabled(false)
        webSettings.domStorageEnabled = true

        webSettings.javaScriptEnabled = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        mWebView.webViewClient = WebViewClient()
        mWebView.webChromeClient = WebChromeClient()

        mWebView.setOnTouchListener { v, e ->
            val action = e.action
            val x = e.x
            val y = e.y
            if (action == MotionEvent.ACTION_DOWN) {
                startX = x
                startY = y
                isClicked = true
            } else if (action == MotionEvent.ACTION_MOVE) {
                val dx = Math.abs(startX - x)
                val dy = Math.abs(startY - y)
                if (dx > MAX_TOUCH || dy > MAX_TOUCH) {
                    isClicked = false
                } else {

                }

            } else if (action == MotionEvent.ACTION_UP) {
                if (isClicked) {
                    val hr: WebView.HitTestResult = (v as WebView).hitTestResult
                    Timber.d("Extra =  ${hr.extra} ---- Type =  ${hr.type}")
                    val extra = hr.extra
                    val type = hr.type
                    when (type) {
                        WebView.HitTestResult.IMAGE_TYPE -> {
                            val data = "0|$extra"
//                            Navigator.startImageView(activity, data)
                        }
                        WebView.HitTestResult.SRC_ANCHOR_TYPE -> {
                            mBackForwardView.visibility = View.VISIBLE
                        }
                    }
                }
            }

            false
        }
    }

    override fun getErrorMessage(e: Throwable, pullToRefresh: Boolean): String {
        return "Lỗi Hệ Thống"
    }

    override fun setData(data: String) {
        val headers = HashMap<String, String>()
        headers["Authorization"] = "Bearer " + mPresenter.dm.getToken()
        mWebView.loadUrl(data, headers)
    }

    override fun loadData(pullToRefresh: Boolean) {
        mPresenter.loadData(pullToRefresh)
    }

    override fun showContent() {
        super.showContent()
        mLceViewHolder.contentView.isRefreshing = false
    }

    override fun showError(e: Throwable, pullToRefresh: Boolean) {
        super.showError(e, pullToRefresh)
        mLceViewHolder.contentView.isRefreshing = false
    }

    @BindView(R.id.view_back_forward_container)
    lateinit var mBackForwardView: View

//    @BindView(R.id.button_left_web)
//    lateinit var mLeftButton: AppCompatImageView
//    @BindView(R.id.button_right_web)
//    lateinit var mRightButton: AppCompatImageView

    private val mHandler = Handler()

    @OnClick(R.id.button_left_web)
    fun onBackInWeb() {
        if (mWebView.canGoBack()) {
            mHandler.post {
                mWebView.goBack()
                if (!mWebView.canGoBack()) {
                    mBackForwardView.visibility = View.GONE
                }
            }
        }
    }

    @OnClick(R.id.button_right_web)
    fun onForwardInWeb() {
        if (mWebView.canGoForward()) {
            mHandler.post {
                mWebView.goForward()
            }
        }
    }
}