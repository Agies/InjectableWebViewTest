package com.agies.webviewtest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity {

    private WebView webview;
    private final static String eula = "https://retailuat1.alldata.net/victoriassecret/public/agreements/mobileappagreement.xhtml";
    private final static String css = "'body { -webkit-transform:translate3d(0px,0px,0px); font-family:Roboto, Helvetica, Arial, sans-serif; font-size:.875rem; background-color:#FFFFFF; } h1,h2,h3,h4,h5,h6 { color:#000000; } sup { font-size:.5rem; } h1,h2 { font-size:1.5rem; font-weight:400; clear:both; } a { color:#D91560; } .color-primary { color:#444444; } img.icon { float:left; max-width:11.9444444%; max-height:44px; height:auto; margin-right:5%; margin-bottom:15px; } .tableview { margin: 20px -10px 0 -10px; } .tableview .legalbody p { margin: 0; padding-bottom:35px!important; padding-top:15px!important; padding-left:4%; background-color:#ffffff; } .tableview .legalbody a { font-size:1.2em!important; font-weight:400!important; } .spacing { margin-left:4%; margin-right:4%; }'";
    private final static String javascript = "ads.pageLoaded()";
    private final static String cssInjectingJavascript = "javascript:(function() {" +
            "var css = " + css + "; " +
            "var style = document.createElement('style'); " +
            "var head = document.head || document.getElementByTagName('head')[0]; " +
            "style.type = 'text/css'; " +
            "if (style.styleSheet) { style.styleSheet.cssText = css; } " +
            "else { style.appendChild(document.createTextNode(css)); } " +
            "head.appendChild(style);" +
            "})()";
    private final static String domInjection = "javascript:(function() {" +
            "var body = document.body || document.getElementByTagName('body')[0];" +
            "var script = document.createElement('script'); " +
            "script.type = 'text/javascript';" +
            "script.appendChild(document.createTextNode('" + javascript + "')); " +
            "body.appendChild(script);" +
            "})()";
    private Button rightButton;
    private Button leftButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webview = (WebView)findViewById(R.id.activity_main_webview);
        leftButton = (Button)findViewById(R.id.left_button);
        rightButton = (Button)findViewById(R.id.right_button);

        final String baseUrl = eula.substring(0, eula.lastIndexOf('/'));
        webview.getSettings().setJavaScriptEnabled(true);
        webview.addJavascriptInterface(new JSCallback(this), "ads");
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webview.loadUrl(cssInjectingJavascript);
                webview.loadUrl(domInjection);
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, eula, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                webview.loadDataWithBaseURL(baseUrl, response, "text/html", "UTF8", "");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //GO BACK
            }
        });
        queue.add(request);
    }

    private class JSCallback {
        private final MainActivity mainActivity;

        public JSCallback(MainActivity mainActivity) {
            this.mainActivity = mainActivity;
        }
        @JavascriptInterface
        public void pageLoaded() {
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.pageLoaded();
                }
            });
        }
    }

    private void pageLoaded() {
        leftButton.setEnabled(true);
        rightButton.setEnabled(true);
    }
}
