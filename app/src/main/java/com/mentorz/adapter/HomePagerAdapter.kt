package com.mentorz.adapter

import android.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.ViewGroup
import com.mentorz.fragments.BaseFragment
import com.mentorz.fragments.notification.NotificationFragment
import com.mentorz.match.MatchFragment
import com.mentorz.me.UserProfileFragment
import com.mentorz.messages.MessageFragment
import com.mentorz.stories.StoriesFragment

/**
 * Created by craterzone on 19/09/17.
 */
class HomePagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): android.support.v4.app.Fragment {
        return fragments.get(position)
    }

    override fun getCount(): Int {
        return fragments.size
    }

    private val fragments = ArrayList<BaseFragment>()

    init {

        fragments.clear()
        fragments.add(StoriesFragment.Companion.newInstance() as BaseFragment)
        fragments.add(MatchFragment.Companion.newInstance() as BaseFragment)
        fragments.add(MessageFragment.Companion.newInstance() as BaseFragment)
        fragments.add(NotificationFragment.Companion.newInstance() as BaseFragment)
        fragments.add(UserProfileFragment.Companion.newInstance() as BaseFragment)

    }

}