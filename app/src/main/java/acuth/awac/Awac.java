package acuth.awac;

import android.app.Activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by adrian on 01/03/15.
 */
public class Awac extends Activity implements Handler.Callback {
    private static final int UNLOCK_NAV_DRAWER = 0;
    private static final int LOCK_NAV_DRAWER = 1;
    private static final int ADD_NAV_DRAWER_ITEM = 2;
    private static final int SET_TITLE = 3;
    private static final int INVALIDATE_WEB_VIEW_FRAG = 4;

    private NavDrawerFragment mNavDrawerFragment;
    private Handler mHandler;

    private boolean mDebug = false;
    private Stack mStack;
    private Map<String,String> mSessionData;
    private boolean mReloadPages = true;

    protected String getAppUrl() {
        Uri uri = getIntent().getData();
        String url = uri == null ? null : uri.getQueryParameter("url");
        if (url != null) {
            System.out.println("getAppUrl() returning URL from launch invocation, url="+url);
            return url;
        }
        return getResources().getString(R.string.app_url);
    }

    private void unlockNavDrawer() {
        if (mDebug) System.out.println("Awac.unlockNavDrawer()");
        // show menu icon
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white);
        mNavDrawerFragment.lock(false);
    }

    private void lockNavDrawer() {
        if (mDebug) System.out.println("Awac.lockNavDrawer()");
        // hide menu icon
        getActionBar().setHomeButtonEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(false);
        mNavDrawerFragment.lock(true);
    }

    private void setActionBarTitle(String title) {
        getActionBar().setTitle(title);
    }

    public boolean handleMessage(Message msg) {

        if (msg.arg1 == INVALIDATE_WEB_VIEW_FRAG) {
            System.out.println("Awac.handleMessage(INVALIDATE_WEB_VIEW_FRAG)");
            mStack.peek().mWebViewFrag.invalidate();
            return true;
        }

        if (msg.arg1 == UNLOCK_NAV_DRAWER) {
            if (mDebug) System.out.println("Awac.handleMessage(UNLOCK_NAV_DRAWER)");
            unlockNavDrawer();
            return true;
        }

        if (msg.arg1 == LOCK_NAV_DRAWER) {
            if (mDebug) System.out.println("Awac.handleMessage(LOCK_NAV_DRAWER)");
            lockNavDrawer();
            return true;
        }

        if (msg.arg1 == ADD_NAV_DRAWER_ITEM) {
            if (mDebug) System.out.println("Awac.handleMessage(ADD_NAV_DRAWER_ITEM)");
            mNavDrawerFragment.addItem((ActionItem) msg.obj);
            return true;
        }

        if (msg.arg1 == SET_TITLE) {
            if (mDebug) System.out.println("Awac.handleMessage(SET_TITLE)");
            setActionBarTitle((String) msg.obj);
            return true;
        }

        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("AwacActivity.onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_awac);

        mHandler = new Handler(this);

        mStack = new Stack();
        mSessionData = new HashMap<>();

        mNavDrawerFragment = (NavDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavDrawerFragment.init(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        startApp(getAppUrl());
    }

    enum FrameTransition {
        OPEN,CLOSE,PREV,NEXT
    }


    private void log(String msg) {
        System.out.println("!!!! Awac"+msg);
    }

    // the currently visible fragment
    private Fragment currFrag = null;
    private FrameTransition currTrans = null;

    private void makeFrameCurrent(FragmentTransaction trans,Frame frame) {
        trans.commit();
        invalidateOptionsMenu();
        //log(" - make frame current [" + (System.currentTimeMillis() - t0) + "ms]");
        currFrag = frame.mWebViewFrag;
    }


    private void displayFrame(Frame oldFrame,Frame frame,FrameTransition ft) {
        log(".displayFrame(ft="+ft+")");
        if (oldFrame == null) {
            log(" - display WebFrameFragment["+frame.mWebViewFrag.mIndex+"]");
        }
        else {
            log(" - replacing WebFrameFragment ["+oldFrame.mWebViewFrag.mIndex+"] with ["+frame.mWebViewFrag.mIndex+"]");
        }

        if (ft != FrameTransition.CLOSE) {
            // add the [hidden] fragment and wait for the web view to send a start-page message which will call revealFrame()
            log(" - adding hidden frame and waiting for start-page message ["+(System.currentTimeMillis()-t0)+"ms]");
            getFragmentManager().beginTransaction().hide(frame.mWebViewFrag).add(R.id.container,frame.mWebViewFrag).commit();
            currTrans = ft;
            return;
        }

        // if this is a close transition then all the fragments are already in place so just do it
        FragmentTransaction trans = getFragmentManager().beginTransaction();
        trans.setCustomAnimations(R.anim.fade_in, R.anim.storm_out);
        trans.hide(currFrag);
        trans.show(frame.mWebViewFrag);
        trans.remove(currFrag);

        // the status of the title, nav-drawer and action bar is already known to the frame so set them and then provoke a redraw
        setTitle(frame.getTitle());
        setNavDrawerLocked(frame.isNavDrawerLocked());
        makeFrameCurrent(trans,frame);
    }

    /**
     * The web page has sent a start-page message so the fragment is ready and we can reveal it
     */
    void revealFrame(Frame frame) {
        log(".revealFrame() [" + (System.currentTimeMillis() - t0) + "ms]");

        FragmentTransaction trans = getFragmentManager().beginTransaction();
        if (currTrans != null) {
            switch (currTrans) {
                case OPEN:
                    trans.setCustomAnimations(R.anim.storm_in, R.anim.fade_out);
                    break;
                case NEXT:
                    trans.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                    break;
                case PREV:
                    trans.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                    break;
            }
        }
        if (currFrag != null) trans.hide(currFrag);
        trans.show(frame.mWebViewFrag);
        if (currFrag != null && (currTrans == FrameTransition.NEXT || currTrans == FrameTransition.PREV)) trans.remove(currFrag);

        // the page has started so it has configured the title, nav-drawer and action bar so we can provoke a redraw
        makeFrameCurrent(trans,frame);
    }

    private void popFrame(boolean ok, String json) {
        Frame oldFrame = mStack.peek();
        String pageTag = oldFrame.mTag;
        Frame frame = mStack.pop();
        frame.mWebViewFrag.onPageClose(pageTag,ok,json);
        displayFrame(oldFrame, frame, FrameTransition.CLOSE);
        WebViewFragment.put(oldFrame.mWebViewFrag);
    }

    private void pushFrame(Frame frame) {
        Frame oldFrame = mStack.peek();
        mStack.push(frame);
        displayFrame(oldFrame, frame, FrameTransition.OPEN);
    }

    private void replaceFrame(Frame frame,boolean next) {
        Frame oldFrame = mStack.peek();
        mStack.pop();
        mStack.push(frame);
        displayFrame(oldFrame,frame, next ? FrameTransition.NEXT : FrameTransition.PREV);
        WebViewFragment.put(oldFrame.mWebViewFrag);
    }

    public void showDialog(String msg,String ok,String cancel) {
        AlertFragment dialog = new AlertFragment();
        dialog.init(msg,ok,cancel,"dialog1","true","false");
        dialog.show(getFragmentManager(),"dialog");
    }

    public void showAlert(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void onDialogResult(boolean yes) {
        mStack.peek().mWebViewFrag.onDialogResult(yes);
    }

    void setTitle(String title) {
        if (mDebug) System.out.println("Awac.setTitle("+title+")");
        Message msg = mHandler.obtainMessage();
        msg.arg1 = SET_TITLE;
        msg.obj = title;
        mHandler.sendMessage(msg);
    }

    void setNavDrawerLocked(boolean locked) {
        if (mDebug) System.out.println("Awac.setNavDrawerLocked(" + locked + ")");
        Message msg = mHandler.obtainMessage();
        msg.arg1 = (locked) ? LOCK_NAV_DRAWER : UNLOCK_NAV_DRAWER;
        mHandler.sendMessage(msg);
    }

    void addNavDrawerItem(ActionItem item) {
        if (mDebug) System.out.println("Awac.addNavDrawerItem(" + item + ")");
        Message msg = mHandler.obtainMessage();
        msg.arg1 = ADD_NAV_DRAWER_ITEM;
        msg.obj = item;
        mHandler.sendMessage(msg);
    }



    int getStackDepth() {
        return mStack.depth();
    }

    String getSession(String name) {
        return mSessionData.get(name);
    }

    void setSession(String name,String value) {
        mSessionData.put(name,value);
    }



    private String resolveUrl(String url) {
        try {
            Frame curFrame = mStack.peek();
            URL baseUrl = new URL(curFrame.mUrl);
            URL resolvedUrl = new URL(baseUrl, url);
            url = resolvedUrl.toExternalForm();
        } catch (Exception ex) {}
        return url;
    }

    private long t0 = -1L;

    void endPage(boolean ok,String value) {
        if (mStack.depth() == 0) {
            finish();
            return;
        }
        t0 = System.currentTimeMillis();
        popFrame(ok, value);
    }

    void openPage(String tag,String url,String value) {
        t0 = System.currentTimeMillis();
        Frame f = new Frame(this, tag, resolveUrl(url), mReloadPages, value);
        pushFrame(f);
    }

    void replacePage(String tag,String url,String value,boolean next) {
        t0 = System.currentTimeMillis();
        Frame f = new Frame(this, tag, resolveUrl(url), mReloadPages, value);
        replaceFrame(f,next);
    }

    void startApp(String url) {
        mStack.clear();
        t0 = System.currentTimeMillis();
        Frame f = new Frame(this, "app", url, true, null);
        mNavDrawerFragment.clearItems();
        pushFrame(f);
    }

    void onAction(String action) {
        mStack.peek().mWebViewFrag.onAction(action);
    }

    @Override
    public void onBackPressed() {
        if (mDebug) System.out.println("Awac[Activity].onBackPressed()");

        // if this frame has a callback registered then fire it and do no more
        if (mStack.peek().mWebViewFrag.onBackPressed()) {
            return;
        }

        endPage(false,null);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mDebug) System.out.println("Awac.onPrepareOptionsMenu()");
        Frame frame = mStack.peek();

        setActionBarTitle(frame.getTitle());
        if (frame.isNavDrawerLocked()) lockNavDrawer(); else unlockNavDrawer();

        if (frame.mOptionsMenuItems.size() > 0 || frame.mActionBarItems.size() > 0) {
            menu.clear();

            for (ActionItem item : frame.mActionBarItems) {
                MenuItem menuItem = menu.add(item.mLabel);
                menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
            }

            for (ActionItem item : frame.mOptionsMenuItems) {
                menu.add(item.mLabel);
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDebug) System.out.println("onOptionsItemSelected("+item+")");

        if (item.getItemId() == android.R.id.home) {
            mNavDrawerFragment.toggle();
            return true;
        }

        String action = mStack.peek().getActionFromLabel(item.toString());
        if (action != null) {
            onAction(action);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
