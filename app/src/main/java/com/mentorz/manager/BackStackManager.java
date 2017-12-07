package com.mentorz.manager;

import android.support.v4.app.Fragment;

import com.mentorz.fragments.notification.NotificationFragment;
import com.mentorz.match.MatchFragment;
import com.mentorz.messages.MessageFragment;
import com.mentorz.me.UserProfileFragment;
import com.mentorz.stories.StoriesFragment;
import com.mentorz.utils.Constant;

import java.util.HashMap;
import java.util.Stack;

/**
 * Created by Umesh on 5/5/16.
 */
public class BackStackManager {

    private static BackStackManager _instance;
    private HashMap<String, Stack<Fragment>> backStack;
    private String currentTab;

    private BackStackManager() {
        backStack = new HashMap<>();
    }

    public static BackStackManager getInstance() {
        if (_instance == null) {
            _instance = new BackStackManager();
        }
        return _instance;
    }

    public String getCurrentTab() {
        return currentTab;
    }

    public void setCurrentTab(String currentTab) {
        this.currentTab = currentTab;
    }

    public HashMap<String, Stack<Fragment>> getBackStack() {
        return backStack;
    }

    public void clear() {
        _instance = null;
    }

    public void initBackStack() {
        Stack<Fragment> storyStack = new Stack<>();
        Stack<Fragment> matchStack = new Stack<>();
        Stack<Fragment> messageStack = new Stack<>();
        Stack<Fragment> notificationStack = new Stack<>();
        Stack<Fragment> meStack = new Stack<>();

        backStack.put(Constant.INSTANCE.getSTORIES(), storyStack);
        backStack.put(Constant.INSTANCE.getMATCH(), matchStack);
        backStack.put(Constant.INSTANCE.getMESSAGE(), messageStack);
        backStack.put(Constant.INSTANCE.getNOTIFICATION(), notificationStack);
        backStack.put(Constant.INSTANCE.getME(), meStack);

        backStack.get(Constant.INSTANCE.getSTORIES()).push(StoriesFragment.Companion.newInstance());
        backStack.get(Constant.INSTANCE.getMATCH()).push(MatchFragment.Companion.newInstance());
        backStack.get(Constant.INSTANCE.getMESSAGE()).push(MessageFragment.Companion.newInstance());
        backStack.get(Constant.INSTANCE.getNOTIFICATION()).push(NotificationFragment.Companion.newInstance());
        backStack.get(Constant.INSTANCE.getME()).push(UserProfileFragment.Companion.newInstance());
    }
}
