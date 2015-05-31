package acuth.awac;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adrian on 01/03/15.
 */
public class Frame {
    final Awac mAwac;
    final boolean mDebug = false;
    final String mTag;
    final String mUrl;
    final boolean mReload;
    final String mValue;
    final WebViewFragment mWebViewFrag;
    final List<ActionItem> mOptionsMenuItems = new ArrayList<>();
    final List<ActionItem> mActionBarItems = new ArrayList<>();

    private boolean mNavDrawerLocked = true;
    private String mTitle = null;

    Frame(Awac awac,String tag,String url,boolean reload,String value) {
        mAwac = awac;
        mTag = tag;
        mUrl = url;
        mReload = reload;
        mValue = value;
        mWebViewFrag = WebViewFragment.get(this, mUrl, mReload);
    }

    public String toString() {
        return "{Frame title:"+mTitle+" web-view:"+mWebViewFrag.toString()+"}";
    }

    void setTitle(String title) {
        if (mDebug) System.out.println("Frame.setTitle("+title+")");
        mTitle = title;
        if (mWebViewFrag.mPageStarted) mAwac.setTitle(title);
    }

    void setNavDrawerLocked(boolean locked) {
        if (mDebug) System.out.println("Frame.setNavDrawerLocked("+locked+")");
        mNavDrawerLocked = locked;
        if (mWebViewFrag.mPageStarted) mAwac.setNavDrawerLocked(locked);
    }

    String getTitle() {
        return mTitle;
    }

    boolean isNavDrawerLocked() {
        return mNavDrawerLocked;
    }

    void addActionBarItem(ActionItem item) {
        if (item != null && !mActionBarItems.contains(item)) {
            if (mDebug) System.out.println("Frame.addActionBarItem("+item+")");
            mActionBarItems.add(item);
        }
    }

    void addOptionsMenuItem(ActionItem item) {
        if (item != null && !mOptionsMenuItems.contains(item)) {
            if (mDebug) System.out.println("Frame.addOptionsMenuItem("+item+")");
            mOptionsMenuItems.add(item);
        }
    }

    String getActionFromLabel(String label) {
        for (ActionItem item : mActionBarItems) {
            if (item.mLabel.equals(label)) {
                return item.mAction;
            }
        }
        for (ActionItem item : mOptionsMenuItems) {
            if (item.mLabel.equals(label)) {
                return item.mAction;
            }
        }
        return null;
    }
}
