package acuth.awac;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adrian on 01/03/15.
 */
public class Frame {
    final String mTitle;
    final String mTag;
    final String mUrl;
    final WebViewFragment mWebViewFrag;
    final List<ActionItem> mOptionsMenuItems = new ArrayList<ActionItem>();
    final List<ActionItem> mActionBarItems = new ArrayList<ActionItem>();

    Frame(String title,String tag,String url) {
        mTitle = title;
        mTag = tag;
        mUrl = url;
        mWebViewFrag = new WebViewFragment();
        mWebViewFrag.init(url);
    }

    void addActionBarItem(ActionItem item) {
        if (!mActionBarItems.contains(item)) {
            //System.out.println("addActionBarItem("+item+")");
            mActionBarItems.add(item);
            //invalidateOptionsMenu();
        }
    }

    void addOptionsMenuItem(ActionItem item) {
        if (!mOptionsMenuItems.contains(item)) {
            //System.out.println("addOptionsMenuItem("+item+")");
            mOptionsMenuItems.add(item);
            //invalidateOptionsMenu();
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
