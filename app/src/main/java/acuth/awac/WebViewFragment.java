package acuth.awac;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adrian on 28/02/15.
 */
public class WebViewFragment extends Fragment implements Handler.Callback, SwipeRefreshLayout.OnRefreshListener {
    private static final String LOCAL_STORE = "acuth.awac.LocalStore";

    private static final int START_PAGE = 2;
    private static final int END_PAGE = 3;
    private static final int OPEN_PAGE = 4;
    private static final int START_REFRESH = 5;
    private static final int END_REFRESH = 6;
    private static final int ENABLE_REFRESH = 7;
    private static final int NEW_APP = 8;
    private static final int NEXT_PAGE = 9;
    private static final int PREV_PAGE = 10;

    private static int cIndex = 0;
    private static List<WebViewFragment> cFragments = new ArrayList<>();

    public static WebViewFragment get(Frame frame, String url, boolean reload) {
        WebViewFragment f;
        if (cFragments.isEmpty()) {
            f = new WebViewFragment();
            f.mIndex = cIndex++;
            f.log("- new");
        }
        else {
            f = cFragments.get(0);
            cFragments.remove(f);
            f.log("- reuse");
        }
        f.init(frame,url,reload);
        return f;
    }

    public static void put(WebViewFragment f) {
        f.log("- retire");
        //cFragments.add(f);
    }


    private boolean mDebug = true;
    public int mIndex;
    private View mRootView;
    private String mUrl;
    private boolean mReload;
    private boolean mUrlLoaded;
    private Frame mFrame;
    private SwipeRefreshLayout mSwipeLayout = null;
    WebView mWebView = null;
    private Handler mHandler = null;
    private String mVarName = null;
    boolean mPageStarted = false;
    private boolean mGotOnBackPressedCB = false;
    private boolean mGotOnRefreshCB = false;
    private boolean mGotOnActionCB = false;
    private boolean mGotOnPageCloseCB = false;
    private long mInitTime = -1L;

    public WebViewFragment() {
    }

    private void log(String msg) {
        String timeStr = (mInitTime > 0L) ? " ["+(System.currentTimeMillis()- mInitTime)+"ms]" : "";
        System.out.println("!!!!!!!! WebViewFragment["+mIndex+"] "+msg+timeStr);
    }

    private void init(Frame frame,String url,boolean reload) {
        log("init(url="+url+")");

        mFrame = frame;
        mUrl = url;
        mReload = reload;
        mUrlLoaded = false;
        mPageStarted = false;
        mVarName = null;
        mGotOnBackPressedCB = false;
        mGotOnRefreshCB = false;
        mGotOnActionCB = false;
        mGotOnPageCloseCB = false;

        mInitTime = System.currentTimeMillis();
    }

    /*public void loadUrl() {
        if (mUrlLoaded) return;
        String url = mUrl;
        if ((url.startsWith("http://") || url.startsWith("https://")) && mReload) {
            url += (url.indexOf("?") == -1 ? "?" : "&") + "rnd="+Math.random();
        }
        mWebView.loadUrl(url);
        mUrlLoaded = true;
        log("- load url "+mUrl);
    }*/

    void invalidate() {
        mWebView.invalidate();
        mSwipeLayout.invalidate();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        log("onAttach()");
    }

    @Override
    public void onDetach() {
        log("onDetach()");
        mRootView = null;
        super.onDetach();
    }

    /*
    @Override
    public void onStart() {
        //log(".onStart()");
        super.onStart();
    }

    @Override
    public void onStop() {
        //log(".onStop()");
        super.onStop();
    }*/

    @Override
    public void onDestroyView() {
        log("onDestroyView()");
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        log("onCreateView()");

        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.web_view_fragment, container, false);
            mWebView = (WebView) mRootView.findViewById(R.id.web_view);

            mHandler = new Handler(this);
            mSwipeLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swipe_layout);
            mSwipeLayout.setOnRefreshListener(this);
            mSwipeLayout.setEnabled(false);

            mWebView.addJavascriptInterface(this, "_awac_");
            mWebView.getSettings().setJavaScriptEnabled(true);

            log("- built web view [height:"+mWebView.getContentHeight()+"]");
        }

        if (!mUrlLoaded) {
            String url = mUrl;
            if ((url.startsWith("http://") || url.startsWith("https://")) && mReload) {
                url += (!url.contains("?") ? "?" : "&") + "rnd=" + Math.random();
            }
            mWebView.loadUrl(url);
            mUrlLoaded = true;

            log("- loaded url");
        }

        return mRootView;
    }


    public boolean handleMessage(Message msg) {
        if (msg.arg1 == NEW_APP) {
            log("handleMessage(NEW_APP)");
            String url = (String) msg.obj;
            mFrame.mAwac.startApp(url);
            return true;
        }

        if (msg.arg1 == START_PAGE) {
            log("handleMessage(START_PAGE)");
            mFrame.mAwac.revealFrame(mFrame);
            System.out.println("[measured-height:"+mWebView.getMeasuredHeight()+"]");
            mInitTime = -1L;
            return true;
        }

        if (msg.arg1 == END_PAGE) {
            log("handleMessage(END_PAGE)");
            String value = (String) msg.obj;
            mFrame.mAwac.endPage(true,value);
            return true;
        }

        if (msg.arg1 == OPEN_PAGE) {
            log("handleMessage(OPEN_PAGE)");
            String[] args = (String[]) msg.obj;
            mFrame.mAwac.openPage(args[0],args[1],args[2]);
            return true;
        }

        if (msg.arg1 == NEXT_PAGE) {
            log("handleMessage(NEXT_PAGE)");
            String[] args = (String[]) msg.obj;
            mFrame.mAwac.replacePage(args[0],args[1],args[2],true);
            return true;
        }

        if (msg.arg1 == PREV_PAGE) {
            log("handleMessage(PREV_PAGE)");
            String[] args = (String[]) msg.obj;
            mFrame.mAwac.replacePage(args[0],args[1],args[2],false);
            return true;
        }

        if (msg.arg1 == START_REFRESH) {

            System.out.println("[content-width:"+mWebView.getContentHeight()+"]");
            System.out.println("[dims:"+mWebView.getWidth()+"x"+mWebView.getHeight()+"]");
            System.out.println("[scale:"+mWebView.getScaleX()+"x"+mWebView.getScaleY()+"]");
            System.out.println("[measured-dims:"+mWebView.getMeasuredWidth()+"x"+mWebView.getMeasuredHeight()+"]");




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
        if (mDebug) System.out.println("onRefresh()");
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
        if (mDebug) System.out.println("onDialogResult("+yes+")");
        executeJS(mVarName + ".fireDialogResult("+(yes?"true":"false")+");");
    }

    /**
     * Called by Awac on behalf of action bar/options menu/nav drawer
     */
    public void onAction(String action) {
        if (mDebug) System.out.println("onAction("+action+")");
        if (mGotOnActionCB) {
            executeJS(mVarName + ".fireAction(\""+action+"\");");
        }
    }

    /**
     * Called by Awac when child page is closed
     */
    public void onPageClose(String childTag,boolean ok,String value) {
        if (mDebug) System.out.println("onPageClose("+childTag+","+ok+","+value+")");
        if (mGotOnPageCloseCB) {
            executeJS(mVarName + ".firePageClose(\""+childTag+"\","+(ok?"true":"false")+",\""+value+"\");");
        }
    }

    private void executeJS(String js) {
        if (mPageStarted) {
            System.out.println("executeJS(" + js + ")");
            if (mVarName.equals("x")) {
                System.out.println("Awac var name not set");
                mFrame.mAwac.showAlert("!!!! Awac var name not set !!!!");
            }
            else {
                mWebView.evaluateJavascript("javascript:" + js, null);
            }
        }
    }

    @JavascriptInterface
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{WebViewFragment url:");
        sb.append(mUrl);
        if (mGotOnBackPressedCB) sb.append(" gotOnBackPressedCB");
        sb.append('}');
        return sb.toString();
    }

    /**
     * Supply the name of the variable used to hold this in JS
     */
    @JavascriptInterface
    public void setVarName(String name) {
        if (mDebug) System.out.println("setVarName(" + name + ")");
        mVarName = name;
    }

    @JavascriptInterface
    public int getStackDepth() {
        int depth = mFrame.mAwac.getStackDepth();
        if (mDebug) System.out.println("getStackDepth()="+depth);
        return depth;
    }

    @JavascriptInterface
    public void setTitle(String title) {
        if (mDebug) System.out.println("WebViewFragment.setTitle(" + title + ")");
        mFrame.setTitle(title);
    }

    @JavascriptInterface
    public void unlockNavDrawer() {
        if (mDebug) System.out.println("WebViewFragment.unlockNavDrawer()");
        mFrame.setNavDrawerLocked(false);
    }

    @JavascriptInterface
    public void showDialog(String msg,String ok,String cancel) {
        if (mDebug) System.out.println("showDialog("+msg+","+ok+","+cancel+")");
        mFrame.mAwac.showDialog(msg,ok,cancel);
    }

    @JavascriptInterface
    public void alert(String msg) {
        System.out.println("[measured-dims:"+mWebView.getMeasuredWidth()+"x"+mWebView.getMeasuredHeight()+"]");
        if (mDebug) System.out.println("alert("+msg+")");
        mFrame.mAwac.showAlert(msg);
    }

    @JavascriptInterface
    public String getDims() {
        int width = mWebView.getMeasuredWidth();
        int height = mWebView.getMeasuredHeight();
        if (mDebug) System.out.println("getDims()="+width+"x"+height);
        return "{\"width\":"+width+",\"height\":"+height+"}";
    }

    /**
     * Indicate that the page is now ready
     */
    @JavascriptInterface
    public void startPage() {
        log("startPage() [JS]");
        mPageStarted = true;
        Message msg = mHandler.obtainMessage();
        msg.arg1 = START_PAGE;
        mHandler.sendMessage(msg);
    }

    /**
     * Indicate that we want to close this page and pop it off the stack
     */
    @JavascriptInterface
    public void endPage(String value) {
        log("endPage("+value+") [JS]");
        Message msg = mHandler.obtainMessage();
        msg.arg1 = END_PAGE;
        msg.obj = value;
        mHandler.sendMessage(msg);
    }

    @JavascriptInterface
    public void addNavDrawerItem(String json) {
        if (mDebug) System.out.println("addNavDrawerItem("+json+")");
        mFrame.mAwac.addNavDrawerItem(ActionItem.fromJson(json));
    }

    @JavascriptInterface
    public void addOptionsMenuItem(String json) {
        if (mDebug) System.out.println("addOptionsMenuItem("+json+")");
        mFrame.addOptionsMenuItem(ActionItem.fromJson(json));
    }

    @JavascriptInterface
    public void addActionBarItem(String json) {
        if (mDebug) System.out.println("addActionBarItem("+json+")");
        mFrame.addActionBarItem(ActionItem.fromJson(json));
    }

    /**
     * Indicate that we want to open a new page and push it on the stack
     */
    @JavascriptInterface
    public void openPage(String tag,String url,String value) {
        log("openPage("+tag+","+url+","+value+") [JS]");
        Message msg = mHandler.obtainMessage();
        msg.arg1 = OPEN_PAGE;
        String[] args = {tag,url,value};
        msg.obj = args;
        mHandler.sendMessage(msg);
    }

    /**
     * Indicate that we want to replace the page on the top of the stack
     */
    @JavascriptInterface
    public void replacePage(String tag,String url,String value,boolean next) {
        log("replacePage("+tag+","+url+","+value+","+next+") [JS]");
        Message msg = mHandler.obtainMessage();
        msg.arg1 = next ? NEXT_PAGE : PREV_PAGE;
        msg.obj = new String[]{tag, url, value};
        mHandler.sendMessage(msg);
    }

    @JavascriptInterface
    public void newApp(String url) {
        log("newApp("+url+") [JS]");
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
        if (mDebug) System.out.println("set(" + name + "," + value + ")");
        mFrame.mAwac.setSession(name, value);
    }

    @JavascriptInterface
    public String get(String name) {
        String value = mFrame.mAwac.getSession(name);
        if (mDebug) System.out.println("get(" + name + ")=" + value);
        return value;
    }

    @JavascriptInterface
    public String getInitParam() {
        String value = mFrame.mValue;
        if (mDebug) System.out.println("getInitParam()=" + value);
        return value;
    }

    @JavascriptInterface
    public String getPageTag() {
        String value = mFrame.mTag;
        if (mDebug) System.out.println("getPageTag()=" + value);
        return value;
    }

    @JavascriptInterface
    public void gotOnBackPressedCB() {
        if (mDebug) System.out.println("gotOnBackPressedCB()");
        mGotOnBackPressedCB = true;
    }

    @JavascriptInterface
    public void gotOnActionCB() {
        if (mDebug) System.out.println("gotOnActionCB()");
        mGotOnActionCB = true;
    }

    @JavascriptInterface
    public void gotOnPageCloseCB() {
        if (mDebug) System.out.println("gotOnPageCloseCB()");
        mGotOnPageCloseCB = true;
    }

    @JavascriptInterface
    public void gotOnRefreshCB() {
        if (mDebug) System.out.println("gotOnRefreshCB()");
        if (!mGotOnRefreshCB) {
            mGotOnRefreshCB = true;
            Message msg = mHandler.obtainMessage();
            msg.arg1 = ENABLE_REFRESH;
            mHandler.sendMessage(msg);
        }
    }

    @JavascriptInterface
    public void startRefresh() {
        if (mDebug) System.out.println("startRefresh()");
        Message msg = mHandler.obtainMessage();
        msg.arg1 = START_REFRESH;
        mHandler.sendMessage(msg);
    }

    @JavascriptInterface
    public void endRefresh() {
        if (mDebug) System.out.println("endRefresh()");
        Message msg = mHandler.obtainMessage();
        msg.arg1 = END_REFRESH;
        mHandler.sendMessage(msg);
    }
}
