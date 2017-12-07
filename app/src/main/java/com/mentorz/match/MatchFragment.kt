package com.mentorz.match


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mentorz.R
import com.mentorz.fragments.BaseFragment
import kotlinx.android.synthetic.main.fragment_match.*


/**
 * A simple [Fragment] subclass.
 */
class MatchFragment : BaseFragment(), View.OnClickListener {
    override fun onClick(p0: View?) {

    }

    companion object {
        fun newInstance(): MatchFragment {
            return MatchFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_match, container, false)
    }

    var adapter: ViewPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = ViewPagerAdapter(childFragmentManager)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)

        val tabOne = LayoutInflater.from(activity).inflate(R.layout.tab_customview, null) as TextView
        tabOne.text = getString(R.string.get_a_mentor)
        tabLayout.getTabAt(0)?.customView = tabOne

        val tabTwo = LayoutInflater.from(activity).inflate(R.layout.tab_customview, null) as TextView
        tabTwo.text = getString(R.string.be_a_mentor)
        tabLayout.getTabAt(1)?.customView = tabTwo
    }

    class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getCount(): Int {
            return 2
        }

        override fun getItem(position: Int): Fragment {
            when (position) {
                0 ->
                    return GetMentorFragment()
                1 ->
                    return BeMentorFragment()
                else ->
                    return GetMentorFragment()
            }

        }

    }

    fun openGetMentorTab() {
        viewPager.currentItem = 0
    }
}
