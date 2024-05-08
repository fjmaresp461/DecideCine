package com.example.quevemoshoy

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.quevemoshoy.databinding.ActivityWebViewBinding

/**
 * `WebViewActivity` es una actividad que muestra una página web en un WebView.
 *
 * Esta actividad recibe una URL a través de un Intent, la carga en un WebView y permite al usuario navegar por la página web.
 *
 * @property webView El WebView que se utiliza para mostrar la página web.
 * @property binding Enlace de la actividad con su vista.
 *
 * @constructor Crea una instancia de `WebViewActivity`.
 */
class WebViewActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var binding: ActivityWebViewBinding

    /**
     * Se llama cuando se crea la actividad. Inicializa la vista, el WebView y establece los oyentes.
     */
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

    /**
     * Establece los oyentes para los botones de la interfaz.
     */
    private fun setListeners() {
        binding.ibBack.setOnClickListener {
            finish()
        }

    }
}