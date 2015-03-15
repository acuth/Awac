package acuth.awac;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adrian on 01/03/15.
 */
public class Frame {
    final Awac mAwac;
    final String mTag;
    final String mUrl;
    final WebViewFragment mWebViewFrag;
    final List<ActionItem> mOptionsMenuItems = new ArrayList<ActionItem>();
    final List<ActionItem> mActionBarItems = new ArrayList<ActionItem>();

    private boolean mNavDrawerLocked = true;
    private String mTitle = null;

    Frame(Awac awac,String tag,String url) {
        mAwac = awac;
        mTag = tag;
        mUrl = url;
        mWebViewFrag = new WebViewFragment();
        mWebViewFrag.init(this,url);
    }

    public String toString() {
        return "{Frame title:"+mTitle+" web-view:"+mWebViewFrag.toString()+"}";
    }

    void setTitle(String title) {
        System.out.println("Frame.setTitle("+title+")");
        mTitle = title;
        if (mWebViewFrag.mPageStarted) mAwac.setTitle(title);
    }

    void setNavDrawerLocked(boolean locked) {
        System.out.println("Frame.setNavDrawerLocked("+locked+")");
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
            System.out.println("Frame.addActionBarItem("+item+")");
            mActionBarItems.add(item);
        }
    }

    void addOptionsMenuItem(ActionItem item) {
        if (item != null && !mOptionsMenuItems.contains(item)) {
            System.out.println("Frame.addOptionsMenuItem("+item+")");
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
