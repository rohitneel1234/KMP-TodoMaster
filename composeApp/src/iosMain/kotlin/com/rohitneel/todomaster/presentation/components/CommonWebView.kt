package com.rohitneel.todomaster.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.WebKit.WKWebView
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.WebKit.WKNavigationDelegateProtocol
import platform.WebKit.WKNavigation
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun CommonWebView(
    modifier: Modifier,
    url: String,
    onPageFinished: () -> Unit
) {
    val nsUrl = NSURL.URLWithString(url) ?: return
    val request = NSURLRequest.requestWithURL(nsUrl)
    
    UIKitView(
        modifier = modifier,
        factory = {
            WKWebView().apply {
                navigationDelegate = object : NSObject(), WKNavigationDelegateProtocol {
                    override fun webView(webView: WKWebView, didFinishNavigation: WKNavigation?) {
                        onPageFinished()
                    }
                }
                loadRequest(request)
            }
        },
        update = { webView ->
            // Update URL if needed
        }
    )
}
