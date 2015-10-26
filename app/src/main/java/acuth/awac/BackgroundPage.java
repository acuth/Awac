package acuth.awac;

import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * Created by adrian on 10/07/15.
 */
public class BackgroundPage {
    public class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onConsoleMessage(ConsoleMessage cm) {
            String msg = cm.message() + " -- " + cm.sourceId() + " (" + cm.lineNumber() + ")";
            mAwac.mLogger.log(msg);
            return true;
        }
    }

    final Awac mAwac;
    final WebView mWebView;

    boolean mDebug = true;
    boolean mPageStarted = false;
    boolean mGotOnMessageCB = false;
    String mVarName = null;

    public BackgroundPage(Awac awac, String url) {
        mAwac = awac;
        mWebView = (WebView) awac.findViewById(R.id.background_web_view);
        mWebView.setWebChromeClient(new MyWebChromeClient());
        mWebView.addJavascriptInterface(this, "_awac_");
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(url);
    }

    private void executeJS(String js) {
        if (mPageStarted) {
            if (mDebug) System.out.println("background: executeJS(" + js + ")");
            if (mVarName.equals("x")) {
                System.out.println("background: Awac var name not set");
            } else {
                mWebView.evaluateJavascript("javascript:" + js, null);
            }
        }
    }

    public void sendMessage(int msgId, String value) {
        if (mDebug) System.out.println("background: sendMessage(" + msgId + "," + value + ")");
        if (mGotOnMessageCB && mPageStarted) {
            executeJS(mVarName + ".fireMessage(" + msgId + ",\"" + value + "\");");
        }
    }

    @JavascriptInterface
    public void setVarName(String name) {
        if (mDebug) System.out.println("background: setVarName(" + name + ")");
        mVarName = name;
    }

    @JavascriptInterface
    public void gotOnMessageCB() {
        if (mDebug) System.out.println("background: gotOnMessageCB()");
        mGotOnMessageCB = true;
    }

    @JavascriptInterface
    public void replyMessage(int msgId, String value) {
        if (mDebug) System.out.println("background: replyMessage(" + msgId + "," + value + ")");
        mAwac.sendBackgroundResponse(msgId, value);
    }

    @JavascriptInterface
    public void startPage() {
        if (mDebug) System.out.println("background: startPage()");
        mPageStarted = true;
    }
}
