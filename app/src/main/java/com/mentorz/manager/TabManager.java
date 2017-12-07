package com.mentorz.manager;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.mentorz.listener.OnTabChangeListener;

import java.util.HashMap;
import java.util.Stack;

/**
 * Created by Umesh on 5/5/16.
 */
public class TabManager implements OnTabChangeListener {


    private final FragmentActivity mActivity;
    private final int mContainerId;

    private final HashMap<String, TabInfo> mTabs = new HashMap<>();

    private TabInfo mLastTab;

    public Fragment getmLastTabFragment() {
        return mLastTab.fragment;
    }

    public TabManager(Context context, int mContainerId) {
        this.mActivity = (FragmentActivity) context;
        this.mContainerId = mContainerId;
    }

    public void addTab(String tag, Class<?> cls, Bundle args) {
        TabInfo info = new TabInfo(tag, cls, args);
        info.fragment = mActivity.getSupportFragmentManager().findFragmentByTag(tag);
        if (info.fragment != null && !info.fragment.isDetached()) {
            FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
            ft.detach(info.fragment);
            ft.commit();
        }
        mTabs.put(tag, info);
    }



    @Override
    public void onTabChanged(String tabId) {
        TabInfo newTab = mTabs.get(tabId);
        HashMap<String, Stack<Fragment>> backStack = BackStackManager.getInstance().getBackStack();
        if (mLastTab != newTab) {
            FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
           /* if (mLastTab != null) {
                if (mLastTab.fragment != null) {
                    ft.detach(mLastTab.fragment);
                }
            }*/
            if (newTab != null) {
                if (newTab.fragment == null) {
                    if (!backStack.get(tabId).isEmpty()) {
                        Fragment fragment = backStack.get(tabId).pop();
                        backStack.get(tabId).push(fragment);
                        newTab.fragment = fragment;
                        ft.addToBackStack(null);
                        ft.replace(mContainerId, fragment,tabId);
//                        newTab.fragment = backStack.get(tabId).pop();
                    }
                } else {
                    if (!backStack.get(tabId).isEmpty()) {
                        Fragment fragment = backStack.get(tabId).pop();
                        backStack.get(tabId).push(fragment);
                        newTab.fragment = fragment;
                        ft.addToBackStack(null);
                        ft.replace(mContainerId, fragment,tabId);
//                        newTab.fragment = backStack.get(tabId).pop();
                    }
                }
            }
            mLastTab = newTab;
            ft.commit();
            mActivity.getSupportFragmentManager().executePendingTransactions();
        }
    }

    static final class TabInfo {
        private final String tag;
        private final Class<?> clss;
        private final Bundle args;
        private Fragment fragment;

        TabInfo(String _tag, Class<?> _class, Bundle _args) {
            tag = _tag;
            clss = _class;
            args = _args;
        }
    }
}
