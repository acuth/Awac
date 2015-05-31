package acuth.awac;


import android.app.Activity;
import android.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adrian on 01/03/15.
 */
public class NavDrawerFragment extends Fragment {
    private Awac mAwac;

    private boolean mDebug = false;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;
    private List<ActionItem> mNavDrawerItems = new ArrayList<>();
    private ArrayAdapter<String> mAdapter;

    private void updateAdapter() {
        String[] sa = new String[mNavDrawerItems.size()];
        int k = 0;
        for (ActionItem item : mNavDrawerItems) {
            sa[k++] = item.mLabel;
        }
        mAdapter = new ArrayAdapter<>(getActivity().getActionBar().getThemedContext(),R.layout.nav_drawer_listview_item,sa);
        mDrawerListView.setAdapter(mAdapter);
    }

    private String getActionFromLabel(String label) {
        for (ActionItem item : mNavDrawerItems) {
            if (item.mLabel.equals(label)) {
                return item.mAction;
            }
        }
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mDrawerListView = (ListView) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        updateAdapter();
        return mDrawerListView;
    }

    public void lock(boolean locked) {
        if (mDebug) System.out.println("NavDrawerFragment.lock("+locked+")");
        mDrawerLayout.setDrawerLockMode(locked?DrawerLayout.LOCK_MODE_LOCKED_CLOSED:DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    public void addItem(ActionItem item) {
        if (item != null && !mNavDrawerItems.contains(item)) {
            if (mDebug) System.out.println("NavDrawerFragment.addItem("+item+")");
            mNavDrawerItems.add(item);
            updateAdapter();
        }
    }

    public void clearItems() {
        if (mDebug) System.out.println("NavDrawerFragment.clearItems()");
        mNavDrawerItems.clear();
        updateAdapter();
    }

    public void init(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        lock(true);
    }

    private void selectItem(int position) {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mAwac != null) {
            String label = mAdapter.getItem(position);
            String action = getActionFromLabel(label);
            if (action != null) mAwac.onAction(action);
        }
    }

    void toggle() {
        if (mDrawerLayout.isDrawerOpen(mFragmentContainerView)) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        else {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mAwac = (Awac) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Expected Awac activity.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAwac = null;
    }
}
