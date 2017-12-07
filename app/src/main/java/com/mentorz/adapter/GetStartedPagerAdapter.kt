package com.mentorz.adapter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.mentorz.fragments.getstarted.GetStartedFragment
import com.mentorz.utils.Constant


/**
 * Created by aMAN GUPTA on 5/31/2017.
 */

class GetStartedPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    internal val PAGE_COUNT = 3

    override fun getCount(): Int {
        return PAGE_COUNT
    }

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> {
                val bundle = Bundle()
                bundle.putInt(Constant.FRAGMENT_POSITION, 0)
                val fragment = GetStartedFragment()
                fragment.arguments = bundle
                return fragment
            }
            1 -> {
                val bundle = Bundle()
                bundle.putInt(Constant.FRAGMENT_POSITION, 1)
                val fragment = GetStartedFragment()
                fragment.arguments = bundle
                return fragment
            }
            2 -> {
                val bundle = Bundle()
                bundle.putInt(Constant.FRAGMENT_POSITION, 2)
                val fragment = GetStartedFragment()
                fragment.arguments = bundle
                return fragment
            }
        }
        return null
    }

}

