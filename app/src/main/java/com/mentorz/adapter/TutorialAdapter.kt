package com.mentorz.adapter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.mentorz.fragments.tutorial.TutorialFragment
import com.mentorz.utils.Constant

/**
 * Created by aMAN GUPTA on 21/7/17.
 */
class TutorialAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    internal val PAGE_COUNT = 6

    override fun getCount(): Int {
        return PAGE_COUNT
    }

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> {
                val bundle = Bundle()
                bundle.putInt(Constant.FRAGMENT_POSITION, 0)
                val fragment = TutorialFragment()
                fragment.arguments = bundle
                return fragment
            }
            1 -> {
                val bundle = Bundle()
                bundle.putInt(Constant.FRAGMENT_POSITION, 1)
                val fragment = TutorialFragment()
                fragment.arguments = bundle
                return fragment
            }
            2 -> {
                val bundle = Bundle()
                bundle.putInt(Constant.FRAGMENT_POSITION, 2)
                val fragment = TutorialFragment()
                fragment.arguments = bundle
                return fragment
            }
            3 -> {
                val bundle = Bundle()
                bundle.putInt(Constant.FRAGMENT_POSITION, 3)
                val fragment = TutorialFragment()
                fragment.arguments = bundle
                return fragment
            }
            4 -> {
                val bundle = Bundle()
                bundle.putInt(Constant.FRAGMENT_POSITION, 4)
                val fragment = TutorialFragment()
                fragment.arguments = bundle
                return fragment
            }
            5 -> {
                val bundle = Bundle()
                bundle.putInt(Constant.FRAGMENT_POSITION, 5)
                val fragment = TutorialFragment()
                fragment.arguments = bundle
                return fragment
            }
        }
        return null
    }

}

