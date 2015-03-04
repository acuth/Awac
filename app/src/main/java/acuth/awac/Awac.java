package acuth.awac;

import android.app.Activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adrian on 01/03/15.
 */
public class Awac extends Activity {
    private NavDrawerFragment mNavDrawerFragment;

    private List<Frame> mFrameStack = new ArrayList<Frame>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("AwacActivity.onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_awac);

        mNavDrawerFragment = (NavDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavDrawerFragment.init(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        addNavDrawerItem(new ActionItem("Adrian","adrian"));
        addNavDrawerItem(new ActionItem("Adrian [clear]","clear_stack"));
        addNavDrawerItem(new ActionItem("GitHub","github"));
        addNavDrawerItem(new ActionItem("mxData","mxdata"));
        addNavDrawerItem(new ActionItem("Fun Question","show_dialog_1"));
        addNavDrawerItem(new ActionItem("Exit App","show_exit_dialog"));

        onAction("adrian");
    }


    private void displayFrame(Frame frame,boolean isNew) {
        int slideIn = isNew ? R.anim.slide_in_from_right : R.anim.slide_in_from_left;
        int slideOut = isNew ? R.anim.slide_out_to_left : R.anim.slide_out_to_right;
        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(slideIn,slideOut)
                .replace(R.id.container, frame.mWebViewFrag)
                .commit();
        getActionBar().setTitle(frame.mTitle);
        invalidateOptionsMenu();
    }

    boolean onAction(String action) {
        System.out.println("onAction("+action+")");

        if ("adrian".equals(action) || "github".equals(action) || "mxdata".equals(action)) {
            Frame frame = null;
            if ("adrian".equals(action)) {
                frame = new Frame("Adrian","adrian","http://adrian.gjm.info/static/tmp/index.html");
                frame.addActionBarItem(new ActionItem("Exit","show_exit_dialog"));
                frame.addActionBarItem(new ActionItem("Q","show_dialog_1"));
                frame.addOptionsMenuItem(new ActionItem("GitHub","github"));
                frame.addOptionsMenuItem(new ActionItem("mxData","mxdata"));
            } else if ("github".equals(action)) {
                frame = new Frame("GitHub","github","http://acuth.github.io/test/highline.html");
                frame.addActionBarItem(new ActionItem("Adrian","adrian"));
                frame.addActionBarItem(new ActionItem("mxData","mxdata"));
            } else if ("mxdata".equals(action)) {
                frame = new Frame("mxData","mxdata","http://www.mxdata.co.uk/");
            }

            mFrameStack.add(frame);
            displayFrame(frame,true);

            return true;
        }

        if ("show_dialog_1".equals(action) || "show_exit_dialog".equals(action)) {
            AlertFragment dialog = new AlertFragment();
            if ("show_dialog_1".equals(action)) {
                dialog.init("How are things going?","OK","Fine","dialog1","true","false");
            }
            if ("show_exit_dialog".equals(action)) {
                dialog.init("Do you wish to exit the app?","YES","NO","exit","yes","no");
            }
            dialog.show(getFragmentManager(),action);
            return true;
        }

        if ("exit:yes".equals(action)) {
            finish();
            return true;
        }

        if ("clear_stack".equals(action)) {
            Frame frame = new Frame("Adrian","adrian","http://adrian.gjm.info/static/tmp/index.html");
            mFrameStack.clear();
            mFrameStack.add(frame);
            displayFrame(frame,true);
        }

        return false;
    }

    @Override
    public void onBackPressed()
    {
        mFrameStack.remove(mFrameStack.size()-1);
        if (mFrameStack.size() == 0) {
            super.onBackPressed();
            return;
        }

        Frame frame = mFrameStack.get(mFrameStack.size()-1);
        displayFrame(frame,false);
    }

    private void addNavDrawerItem(ActionItem item) {
        System.out.println("addNavDrawerItem("+item+")");
        mNavDrawerFragment.addItem(item);
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
        Frame frame = mFrameStack.get(mFrameStack.size()-1);
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
        Frame frame = mFrameStack.get(mFrameStack.size()-1);
        String label = item.toString();
        String action = frame.getActionFromLabel(label);
        if (action != null && onAction(action)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
