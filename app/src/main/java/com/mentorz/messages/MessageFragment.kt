package com.mentorz.messages


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.MenuItemCompat
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.TextView
import com.mentorz.R
import com.mentorz.fragments.BaseFragment
import kotlinx.android.synthetic.main.fragment_message.*


/**
 * A simple [Fragment] subclass.
 */
class MessageFragment : BaseFragment(), View.OnClickListener {

    override fun networkError() {
        super.networkError()
    }

    override fun onClick(p0: View?) {

    }

    companion object {
        fun newInstance(): MessageFragment {
            return MessageFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        adapter = ViewPagerAdapter(childFragmentManager)

    }

    private var  searchViewItem: MenuItem? = null

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.message_menu, menu)
        searchViewItem = menu?.findItem(R.id.search)
        val searchViewAndroidActionBar = MenuItemCompat.getActionView(searchViewItem) as android.support.v7.widget.SearchView
        searchViewAndroidActionBar.queryHint = getString(R.string.search_for_people)
        updateSearchIconState()
        searchViewAndroidActionBar.setOnQueryTextListener(object : android.support.v7.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                (adapter?.getItem(0) as ChatFragment).searchPeople(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (TextUtils.isEmpty(newText)){
                    (adapter?.getItem(0) as ChatFragment).searchFound()
                }
                (adapter?.getItem(0) as ChatFragment).searchPeople(newText)
                return false
            }
        })

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message, container, false)
    }

    var adapter : ViewPagerAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)

        val tabOne = LayoutInflater.from(activity).inflate(R.layout.tab_customview, null) as TextView
        tabOne.text = getString(R.string.chats)
        tabLayout.getTabAt(0)?.customView = tabOne

        val tabTwo = LayoutInflater.from(activity).inflate(R.layout.tab_customview, null) as TextView
        tabTwo.text = getString(R.string.requests)
        tabLayout.getTabAt(1)?.customView = tabTwo

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (position == 0){
                    if (searchViewItem != null){
                        searchViewItem!!.isVisible = true
                    }
                }else{
                    if (searchViewItem != null){
                        if (searchViewItem!!.isActionViewExpanded){
                         MenuItemCompat.collapseActionView(searchViewItem)
                        }
                        searchViewItem!!.isVisible = false
                    }
                }
            }

        })


    }

   fun updateSearchIconState() {
       try{

        if (viewPager.currentItem == 0){
            if (searchViewItem != null){
                searchViewItem!!.isVisible = true
            }
        }else{
            if (searchViewItem != null){
                searchViewItem!!.isVisible = false
            }
        }}catch (e:Exception){
           Log.e("MessageFragment",""+e.toString())
       }

    }

    fun openRequests() {
        viewPager.currentItem = 1
        ((viewPager.adapter as ViewPagerAdapter).requestFragment as RequestFragment).refreshList()
    }

    class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        var chatFragment: Fragment = ChatFragment()
        var requestFragment: Fragment = RequestFragment()

        override fun getCount(): Int {
            return 2
        }

        override fun getItem(position: Int): Fragment {
            when (position) {

                0 -> {
                    return chatFragment
                }

                1 -> {
                    return requestFragment
                }

                else -> {
                    return chatFragment
                }
            }

        }
    }

    public fun updateSearchView(){}
}
