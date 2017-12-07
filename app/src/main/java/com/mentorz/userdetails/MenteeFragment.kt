package com.mentorz.userdetails


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mentorz.R
import com.mentorz.extensions.hideProgressBar
import com.mentorz.extensions.showProgressBar
import com.mentorz.fragments.BaseFragment
import com.mentorz.listener.OnLoadMoreListener
import com.mentorz.manager.WrapContentLinearLayoutManager
import com.mentorz.match.UserListItem
import com.mentorz.userdetails.adapter.MenteeDelegateAdapter
import com.mentorz.userdetails.adapter.MenteeListAdapter
import com.mentorz.utils.Constant
import kotlinx.android.synthetic.main.fragment_mentee.*

/**
 * A simple [Fragment] subclass.
 */
class MenteeFragment : BaseFragment(), UserDetailsView, MenteeDelegateAdapter.onViewSelectedListener {

    override fun networkError() {
        super.networkError()
    }
    override fun updateAdapter() {
        activity?.runOnUiThread {
            adapter?.notifyDataSetChanged()
        }
    }

    override fun dataSetChanged() {
        activity?.runOnUiThread {
            adapter?.notifyDataSetChanged()
        }
    }


    override fun showProgress() {
        activity?.showProgressBar(progressBar)
    }

    override fun noUserFound() {
        activity?.runOnUiThread {
            swipeToRefresh.isRefreshing = false
            adapter?.removeLoaderFromBottom()

        }
    }

    override fun setMenteeAdapter(userList: MutableList<UserListItem>) {
        activity?.runOnUiThread {
            if (pageNo == 0) {
                swipeToRefresh.isRefreshing = false
                adapter?.clearAndMentors(userList)
            } else {
                adapter?.addMentors(userList)

            }
        }
    }
    override fun hideProgress() {
        activity?.hideProgressBar(progressBar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intentFilter =  IntentFilter()
        intentFilter.addAction(Constant.ACTION_BLOCK)
        activity?.registerReceiver(receiver,intentFilter)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mentee, container, false)
    }

    var presenter = UserDetailsPresenterImpl(this)
    var adapter: MenteeListAdapter?=null
    var friendId:Long=0
    var pageNo:Int=0
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        friendId  =activity.intent.getLongExtra("friend_id",0)
        recyclerView.apply {
            setHasFixedSize(true)
            val linearLayout = WrapContentLinearLayoutManager(activity)
            layoutManager = linearLayout
        }
        adapter = MenteeListAdapter(activity,this)
        recyclerView.adapter = adapter
        adapter?.registerAdapterDataObserver(txtEmptyMessage)
        adapter!!.setOnLoadMoreListener(recyclerView,object : OnLoadMoreListener {
            override fun onLoadMore() {
                adapter!!.addLoaderAtBottom()
                presenter.getMentee(friendId,++pageNo,false)
            }
        })
        presenter.getMentee(friendId,0,false)
        swipeToRefresh.setOnRefreshListener {
            swipeToRefresh.isRefreshing = true
            pageNo = 0
            presenter.getMentee(friendId,pageNo,true)
        }
        swipeToRefresh.setColorSchemeColors(Color.RED, Color.BLUE, Color.CYAN)

    }

    fun refreshList(){
        activity?.runOnUiThread {
            pageNo = 0
            presenter.getMentee(friendId,pageNo,true)}

    }
    var receiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, intent: Intent?) {
            when(intent?.action){

                Constant.ACTION_BLOCK ->{

                    refreshList()
                }


            }

        }

    }

}
