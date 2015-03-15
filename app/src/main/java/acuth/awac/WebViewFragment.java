package acuth.awac;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

/**
 * Created by adrian on 28/02/15.
 */
public class WebViewFragment extends Fragment implements Handler.Callback, SwipeRefreshLayout.OnRefreshListener {
    private static final boolean DEBUG = true;
    private static final String LOCAL_STORE = "acuth.awac.LocalStore";

    private static final int START_PAGE = 2;
    private static final int END_PAGE = 3;
    private static final int OPEN_PAGE = 4;
    private static final int START_REFRESH = 5;
    private static final int END_REFRESH = 6;
    private static final int ENABLE_REFRESH = 7;
    private static final int NEW_APP = 8;

    private View mRootView;
    private String mUrl;

    private Frame mFrame;
    private SwipeRefreshLayout mSwipeLayout = null;
    private WebView mWebView = null;
    private Handler mHandler = null;
    private String mVarName = null;
    boolean mPageStarted = false;
    private boolean mGotOnBackPressedCB = false;
    private boolean mGotOnRefreshCB = false;
    private boolean mGotOnActionCB = false;
    private boolean mGotOnPageCloseCB = false;

    public WebViewFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("WebViewFragment.onCreateView()");

        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.web_view_fragment, container, false);
            System.out.println(" - mRootView=" + mRootView);

            mWebView = (WebView) mRootView.findViewById(R.id.web_view);
            System.out.println(" - mWebView=" + mWebView);

            mHandler = new Handler(this);
            mSwipeLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swipe_layout);
            mSwipeLayout.setOnRefreshListener(this);
            mSwipeLayout.setEnabled(false);

            mWebView.addJavascriptInterface(this, "_awac_");
            mWebView.getSettings().setJavaScriptEnabled(true);

            System.out.println(" - load(" + mUrl + ")");
            mWebView.loadUrl(mUrl+"?rnd="+Math.random());
        }

        return mRootView;
    }

    public void init(Frame frame,String url) {
        mFrame = frame;
        mUrl = url;
    }

    public boolean handleMessage(Message msg) {
        if (msg.arg1 == NEW_APP) {
            String url = (String) msg.obj;
            mFrame.mAwac.startApp(url);
            return true;
        }

        if (msg.arg1 == START_PAGE) {
            mFrame.mAwac.invalidateOptionsMenu();
            //mFrame.mAwac.setNavDrawerLocked(mFrame.isNavDrawerLocked());
            // make web view visible
            AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(240);
            mWebView.setVisibility(View.VISIBLE);
            mWebView.startAnimation(anim);
            return true;
        }

        if (msg.arg1 == END_PAGE) {
            String json = (String) msg.obj;
            mFrame.mAwac.endPage(json);
            return true;
        }

        if (msg.arg1 == OPEN_PAGE) {
            String[] args = (String[]) msg.obj;
            mFrame.mAwac.openPage(args[0],args[1]);
            return true;
        }

        if (msg.arg1 == START_REFRESH) {
            mSwipeLayout.setRefreshing(true);
            return true;
        }

        if (msg.arg1 == END_REFRESH) {
            mSwipeLayout.setRefreshing(false);
            return true;
        }

        if (msg.arg1 == ENABLE_REFRESH) {
            mSwipeLayout.setEnabled(true);
            return true;
        }

        return false;
    }

    /**
     * Called by SwipeRefreshLayout
     */
    public void onRefresh() {
        if (DEBUG) System.out.println("onRefresh()");
        if (mGotOnRefreshCB) {
            executeJS(mVarName+".fireRefresh();");
        }
    }

    /**
     * Called by Awac[Activity].onBackPressed()
     * Returns true if a callback has been called
     */
    boolean onBackPressed() {
        System.out.println(" - "+this);
        if (!mGotOnBackPressedCB) return false;
        executeJS(mVarName + ".fireBackPressed();");
        return true;
    }

    /**
     * Called by AlertFragment
     */
    public void onDialogResult(boolean yes) {
        if (DEBUG) System.out.println("onDialogResult("+yes+")");
        executeJS(mVarName + ".fireDialogResult("+(yes?"true":"false")+");");
    }

    /**
     * Called by Awac on behalf of action bar/options menu/nav drawer
     */
    public void onAction(String action) {
        if (DEBUG) System.out.println("onAction("+action+")");
        if (mGotOnActionCB) {
            executeJS(mVarName + ".fireAction(\""+action+"\");");
        }
    }

    /**
     * Called by Awac when child page is closed
     */
    public void onPageClose(String childTag,boolean ok,String json) {
        if (DEBUG) System.out.println("onPageClose("+childTag+","+ok+","+json+")");
        if (mGotOnPageCloseCB) {
            executeJS(mVarName + ".firePageClose(\""+childTag+"\","+(ok?"true":"false")+","+json+");");
        }
    }

    private void executeJS(String js) {
        if (mPageStarted) {
            System.out.println("executeJS("+js+")");
            mWebView.evaluateJavascript("javascript:" + js, null);
        }
    }

    @JavascriptInterface
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{WebViewFragment url:"+mUrl);
        if (mGotOnBackPressedCB) sb.append(" gotOnBackPressedCB");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Supply the name of the variable used to hold this in JS
     */
    @JavascriptInterface
    public void setVarName(String name) {
        if (DEBUG) System.out.println("setVarName(" + name + ")");
        mVarName = name;
    }

    @JavascriptInterface
    public int getStackDepth() {
        int depth = mFrame.mAwac.getStackDepth();
        if (DEBUG) System.out.println("getStackDepth()="+depth);
        return depth;
    }

    @JavascriptInterface
    public void setTitle(String title) {
        if (DEBUG) System.out.println("WebViewFragment.setTitle(" + title + ")");
        mFrame.setTitle(title);
    }

    @JavascriptInterface
    public void unlockNavDrawer() {
        if (DEBUG) System.out.println("WebViewFragment.unlockNavDrawer()");
        mFrame.setNavDrawerLocked(false);
    }

    @JavascriptInterface
    public void showDialog(String msg,String ok,String cancel) {
        if (DEBUG) System.out.println("showDialog("+msg+","+ok+","+cancel+")");
        mFrame.mAwac.showDialog(msg,ok,cancel);
    }

    /**
     * Indicate that the page is now ready
     */
    @JavascriptInterface
    public void startPage() {
        if (DEBUG) System.out.println("startPage()");
        mPageStarted = true;
        Message msg = mHandler.obtainMessage();
        msg.arg1 = START_PAGE;
        mHandler.sendMessage(msg);
    }

    /**
     * Indicate that we want to close this page and pop it off the stack
     */
    @JavascriptInterface
    public void endPage(String json) {
        if (DEBUG) System.out.println("endPage("+json+")");
        Message msg = mHandler.obtainMessage();
        msg.arg1 = END_PAGE;
        msg.obj = json;
        mHandler.sendMessage(msg);
    }

    @JavascriptInterface
    public void addNavDrawerItem(String json) {
        if (DEBUG) System.out.println("addNavDrawerItem("+json+")");
        mFrame.mAwac.addNavDrawerItem(ActionItem.fromJson(json));
    }

    @JavascriptInterface
    public void addOptionsMenuItem(String json) {
        if (DEBUG) System.out.println("addOptionsMenuItem("+json+")");
        mFrame.addOptionsMenuItem(ActionItem.fromJson(json));
    }

    @JavascriptInterface
    public void addActionBarItem(String json) {
        if (DEBUG) System.out.println("addActionBarItem("+json+")");
        mFrame.addActionBarItem(ActionItem.fromJson(json));
    }

    /**
     * Indicate that we want to open a new page and push it on the stack
     */
    @JavascriptInterface
    public void openPage(String tag,String url) {
        if (DEBUG) System.out.println("openPage("+tag+","+url+")");
        Message msg = mHandler.obtainMessage();
        msg.arg1 = OPEN_PAGE;
        String[] args = {tag,url};
        msg.obj = args;
        mHandler.sendMessage(msg);
    }

    @JavascriptInterface
    public void newApp(String url) {
        if (DEBUG) System.out.println("newApp("+url+")");
        Message msg = mHandler.obtainMessage();
        msg.arg1 = NEW_APP;
        msg.obj = url;
        mHandler.sendMessage(msg);
    }


    @JavascriptInterface
    public void store(String name, String value) {
        SharedPreferences settings = mFrame.mAwac.getSharedPreferences(LOCAL_STORE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(name,value);
        editor.commit();
    }

    @JavascriptInterface
    public String load(String name) {
        SharedPreferences settings = mFrame.mAwac.getSharedPreferences(LOCAL_STORE, 0);
        return settings.getString(name, null);
    }

    @JavascriptInterface
    public void set(String name, String value) {
        if (DEBUG) System.out.println("set(" + name + "," + value + ")");
        mFrame.mAwac.setSession(name, value);
    }

    @JavascriptInterface
    public String get(String name) {
        String value = mFrame.mAwac.getSession(name);
        if (DEBUG) System.out.println("get(" + name + ")=" + value);
        return value;
    }

    @JavascriptInterface
    public void gotOnBackPressedCB() {
        if (DEBUG) System.out.println("gotOnBackPressedCB()");
        mGotOnBackPressedCB = true;
    }

    @JavascriptInterface
    public void gotOnActionCB() {
        if (DEBUG) System.out.println("gotOnActionCB()");
        mGotOnActionCB = true;
    }

    @JavascriptInterface
    public void gotOnPageCloseCB() {
        if (DEBUG) System.out.println("gotOnPageCloseCB()");
        mGotOnPageCloseCB = true;
    }

    @JavascriptInterface
    public void gotOnRefreshCB() {
        if (DEBUG) System.out.println("gotOnRefreshCB()");
        if (!mGotOnRefreshCB) {
            mGotOnRefreshCB = true;
            Message msg = mHandler.obtainMessage();
            msg.arg1 = ENABLE_REFRESH;
            mHandler.sendMessage(msg);
        }
    }

    //@JavascriptInterface
    /*public void gotOnResultCB() {
        if (DEBUG) System.out.println("gotOnResultCB()");
        mGotOnResultCB = true;
    }*/


    @JavascriptInterface
    public void startRefresh() {
        if (DEBUG) System.out.println("startRefresh()");
        Message msg = mHandler.obtainMessage();
        msg.arg1 = START_REFRESH;
        mHandler.sendMessage(msg);
    }

    @JavascriptInterface
    public void endRefresh() {
        if (DEBUG) System.out.println("endRefresh()");
        Message msg = mHandler.obtainMessage();
        msg.arg1 = END_REFRESH;
        mHandler.sendMessage(msg);
    }
}
