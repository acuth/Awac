package acuth.awac;

import android.graphics.Color;
import org.json.JSONObject;

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

    private ActionItem mHomeItem = null;
    private int mPrimaryColor = -1;
    private int mTextPrimaryColor = -1;
    private int mPrimaryDarkColor = -1;
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

    private int getColor(JSONObject json, String name) {
        int color = -1;
        try {
            String s = json.getString(name);
            if (s != null) {
                color = Color.parseColor(s);
            }
        } catch (Exception ex) {
        }
        return color;
    }

    void setColors(JSONObject json) {
        if (mDebug) System.out.println("Frame.setColors(" + json + ")");
        mPrimaryColor = getColor(json, "primary");
        mTextPrimaryColor = getColor(json, "text_primary");
        mPrimaryDarkColor = getColor(json, "primary_dark");
    }

    int getPrimaryColor() {
        return mPrimaryColor == -1 ? mAwac.mPrimaryColor : mPrimaryColor;
    }

    int getTextPrimaryColor() {
        return mTextPrimaryColor == -1 ? mAwac.mTextPrimaryColor : mTextPrimaryColor;
    }

    int getPrimaryDarkColor() {
        return mPrimaryDarkColor == -1 ? mAwac.mPrimaryDarkColor : mPrimaryDarkColor;
    }


    boolean isNavDrawerLocked() {
        return mNavDrawerLocked;
    }

    boolean hasHomeItem() {
        System.out.println("Frame.hasHomeItem() mHomeItem=" + mHomeItem);
        return mHomeItem != null;
    }

    String getHomeItemIcon() {
        return mHomeItem.mIcon;
    }

    String getHomeItemAction() {
        return mHomeItem.mAction;
    }

    void setHomeItem(ActionItem item) {
        System.out.println("Frame.setHomeItem(" + item + ")");
        mHomeItem = item;
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
