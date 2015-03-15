package acuth.awac;

import android.app.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

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

    private NavDrawerFragment mNavDrawerFragment;
    private Handler mHandler;

    private Stack mStack;
    private Map<String,String> mSessionData;

    protected String getAppUrl() {
        return getResources().getString(R.string.app_url);
    }

    private void unlockNavDrawer() {
        // show menu icon
        System.out.println("Awac.unlockNavDrawer()");
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white);
        mNavDrawerFragment.lock(false);
    }

    private void lockNavDrawer() {
        System.out.println("Awac.lockNavDrawer()");
        // hide menu icon
        getActionBar().setHomeButtonEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(false);
        mNavDrawerFragment.lock(true);
    }

    private void setActionBarTitle(String title) {
        getActionBar().setTitle(title);
    }

    public boolean handleMessage(Message msg) {

        if (msg.arg1 == UNLOCK_NAV_DRAWER) {
            System.out.println("Awac.handleMessage(UNLOCK_NAV_DRAWER)");
            unlockNavDrawer();
            return true;
        }

        if (msg.arg1 == LOCK_NAV_DRAWER) {
            System.out.println("Awac.handleMessage(LOCK_NAV_DRAWER)");
            lockNavDrawer();
            return true;
        }

        if (msg.arg1 == ADD_NAV_DRAWER_ITEM) {
            System.out.println("Awac.handleMessage(ADD_NAV_DRAWER_ITEM)");
            mNavDrawerFragment.addItem((ActionItem) msg.obj);
            return true;
        }

        if (msg.arg1 == SET_TITLE) {
            System.out.println("Awac.handleMessage(SET_TITLE)");
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
        mSessionData = new HashMap<String,String>();

        mNavDrawerFragment = (NavDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavDrawerFragment.init(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        /*addNavDrawerItem(new ActionItem("Adrian","adrian"));
        addNavDrawerItem(new ActionItem("Adrian [clear]","clear_stack"));
        addNavDrawerItem(new ActionItem("GitHub","github"));
        addNavDrawerItem(new ActionItem("mxData","mxdata"));
        addNavDrawerItem(new ActionItem("Fun Question","show_dialog_1"));
        addNavDrawerItem(new ActionItem("Exit App","show_exit_dialog"));*/

        startApp(getAppUrl());
    }

    private void displayFrame(Frame frame,boolean isNew) {
        int slideIn = isNew ? R.anim.slide_in_from_right : R.anim.slide_in_from_left;
        int slideOut = isNew ? R.anim.slide_out_to_left : R.anim.slide_out_to_right;

        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(slideIn,slideOut)
                .replace(R.id.container, frame.mWebViewFrag)
                .commit();

        if (!isNew) {
            setTitle(frame.getTitle());
            setNavDrawerLocked(frame.isNavDrawerLocked());
            invalidateOptionsMenu();
        }
        //frame.displayed();
    }

    private void popFrame(boolean ok, String json) {
        String pageTag = mStack.peek().mTag;
        Frame frame = mStack.pop();
        frame.mWebViewFrag.onPageClose(pageTag,ok,json);
        displayFrame(frame, false);
    }

    private void pushFrame(Frame frame) {
        mStack.push(frame);
        displayFrame(frame, true);
    }

    public void showDialog(String msg,String ok,String cancel) {
        AlertFragment dialog = new AlertFragment();
        dialog.init(msg,ok,cancel,"dialog1","true","false");
        dialog.show(getFragmentManager(),"dialog");
    }

    public void onDialogResult(boolean yes) {
        mStack.peek().mWebViewFrag.onDialogResult(yes);
    }

    void setTitle(String title) {
        System.out.println("Awac.setTitle("+title+")");
        Message msg = mHandler.obtainMessage();
        msg.arg1 = SET_TITLE;
        msg.obj = title;
        mHandler.sendMessage(msg);
    }

    void setNavDrawerLocked(boolean locked) {
        System.out.println("Awac.setNavDrawerLocked(" + locked + ")");
        Message msg = mHandler.obtainMessage();
        msg.arg1 = (locked) ? LOCK_NAV_DRAWER : UNLOCK_NAV_DRAWER;
        mHandler.sendMessage(msg);
    }

    void addNavDrawerItem(ActionItem item) {
        System.out.println("Awac.addNavDrawerItem(" + item + ")");
        Message msg = mHandler.obtainMessage();
        msg.arg1 = ADD_NAV_DRAWER_ITEM;
        msg.obj = item;
        mHandler.sendMessage(msg);
    }

    void startApp(String url) {
        mStack.clear();
        pushFrame(new Frame(this, "app", url));
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

    void endPage(String json) {
        if (mStack.depth() == 0) {
            finish();
            return;
        }
        popFrame(true, json);
    }

    void openPage(String tag,String url) {
        Frame curFrame = mStack.peek();
        try {
            URL baseUrl = new URL(curFrame.mUrl);
            URL resolvedUrl = new URL(baseUrl, url);
            url = resolvedUrl.toExternalForm();
        } catch (Exception ex) {}
        pushFrame(new Frame(this, tag, url));
    }

    void onAction(String action) {
        mStack.peek().mWebViewFrag.onAction(action);
    }

    @Override
    public void onBackPressed() {
        System.out.println("Awac[Activity].onBackPressed()");

        // if this frame has a callback registered then fire it and do no more
        if (mStack.peek().mWebViewFrag.onBackPressed()) {
            return;
        }

        // pop current frame off the stack
        System.out.println(" - pop frame off stack");


        if (mStack.depth() > 0) {
            // display the new current frame
            popFrame(false, null);
            return;
        }


        // if the stack is now empty then finish
        mStack.clear();
        System.out.println(" - stack is now empty so finish");
        finish();
    }



    //private void clearActionBar() {
     //   mActionBarItems.clear();
     //   invalidateOptionsMenu();
    //}

    //private void clearOptionsMenu() {
     //   mOptionsMenuItems.clear();
     //   invalidateOptionsMenu();
    //}

   /* private void addActionBarItem(String item) {
        if (!mActionBarItems.contains(item)) {
            System.out.println("addActionBarItem("+item+")");
            mActionBarItems.add(item);
            invalidateOptionsMenu();
        }
    }

    private void addOptionsMenuItem(String item) {
        if (!mOptionsMenuItems.contains(item)) {
            System.out.println("addOptionsMenuItem("+item+")");
            mOptionsMenuItems.add(item);
            invalidateOptionsMenu();
        }
    }*/

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        System.out.println("Awac.onPrepareOptionsMenu()");
        Frame frame = mStack.peek();

        setActionBarTitle(frame.getTitle());
        if (frame.isNavDrawerLocked()) lockNavDrawer(); else unlockNavDrawer();

        //System.out.println("onPrepareOptionsMenu()");
        //System.out.println(" - # menu items [before] = "+menu.size());
        if (frame.mOptionsMenuItems.size() == 0 && frame.mActionBarItems.size() == 0) {
            super.onPrepareOptionsMenu(menu);
            return false;
        }

        menu.clear();

        for (ActionItem item : frame.mActionBarItems) {
            MenuItem menuItem = menu.add(item.mLabel);
            menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        }

        for (ActionItem item : frame.mOptionsMenuItems) {
            menu.add(item.mLabel);
        }

        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        System.out.println("onOptionsItemSelected("+item+")");
        int id = item.getItemId();
        if (id == android.R.id.home) {
            System.out.println(" - it was the home button");
            mNavDrawerFragment.toggle();
            return true;
        }

        Frame frame = mStack.peek();
        String label = item.toString();
        String action = frame.getActionFromLabel(label);
        if (action != null) {
            onAction(action);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
