package com.example.cartemplate2.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.cartemplate2.R

class AboutUsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)
        val webView = findViewById<WebView>(R.id.webView)
        // Enable JavaScript (if needed)
        webView.settings.javaScriptEnabled = true

        // Set the user agent to mimic a desktop browser
        webView.settings.userAgentString =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36"

        // Load the URL
        val url = "https://apitest.borzodelivery.com/in/track/PGILP2YZN571IN"
        webView.loadUrl(url)

        // Handle navigation within the WebView
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }
        }
    }
}