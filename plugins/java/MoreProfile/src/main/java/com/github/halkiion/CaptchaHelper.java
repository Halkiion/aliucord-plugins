package com.github.halkiion.plugins;

import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

public class CaptchaHelper {

    public static void showCaptchaDialog(
            Context context,
            String sitekey,
            CaptchaCallback onSolved) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);

        FrameLayout container = new FrameLayout(context);

        WebView webView = new WebView(context);
        webView.getSettings().setJavaScriptEnabled(true);

        String html = "<html><head>"
                + "<meta name='viewport' content='width=device-width, initial-scale=1.0, user-scalable=no'/>"
                + "<style>"
                + "body { margin:0; padding:0; background:transparent; overflow:hidden; }"
                + ".h-captcha { margin:0 auto; overflow:hidden; max-width:100vw; }"
                + "</style>"
                + "</head><body>"
                + "<script src='https://hcaptcha.com/1/api.js?onload=onHcaptchaLoad' async defer></script>"
                + "<div id='cap' class='h-captcha' data-sitekey='" + sitekey + "' data-callback='onSolved'></div>"
                + "<script>"
                + "function onSolved(token){Android.onCaptchaSolved(token);}"
                + "function onHcaptchaLoad(){}"
                + "</script>"
                + "</body></html>";

        webView.addJavascriptInterface(new Object() {
            @android.webkit.JavascriptInterface
            public void onCaptchaSolved(String token) {
                dialog.dismiss();
                if (onSolved != null)
                    onSolved.onSolved(token);
            }
        }, "Android");

        webView.setWebViewClient(new WebViewClient());
        webView.setBackgroundColor(0x00000000);

        container.addView(webView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        dialog.setContentView(container);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(android.view.Gravity.CENTER);
        }

        dialog.show();
        webView.loadDataWithBaseURL("https://hcaptcha.com", html, "text/html", "utf-8", null);
    }

    public interface CaptchaCallback {
        void onSolved(String captchaToken);
    }
}