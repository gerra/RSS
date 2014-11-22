package ru.android.german.lesson7;

import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by german on 01.11.14.
 */
public class MyWebViewClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return false;
    }
}
