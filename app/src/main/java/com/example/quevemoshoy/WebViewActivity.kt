package com.example.quevemoshoy

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.quevemoshoy.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var binding: ActivityWebViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        webView = findViewById(R.id.webview)
        webView.webViewClient = WebViewClient()
        val url = intent.getStringExtra("url")
        if (url != null) {
            webView.loadUrl(url)
        }

        setListeners()
    }

    private fun setListeners() {
        binding.ibBack.setOnClickListener {
            finish()
        }

    }
}