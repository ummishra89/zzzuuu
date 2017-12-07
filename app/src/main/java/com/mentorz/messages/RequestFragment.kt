package com.mentorz.messages


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
import com.mentorz.MentorzApplication
import com.mentorz.R
import com.mentorz.database.DbManager
import com.mentorz.extensions.hideProgressBar
import com.mentorz.extensions.showProgressBar
import com.mentorz.extensions.showSnackBar
import com.mentorz.fragments.BaseFragment
import com.mentorz.listener.MentorRequestsListener
import com.mentorz.listener.OnLoadMoreListener
import com.mentorz.manager.WrapContentLinearLayoutManager
import com.mentorz.requester.GetMentorRequestRequester
import com.mentorz.utils.Constant
import kotlinx.android.synthetic.main.fragment_request.*
import org.jetbrains.anko.doAsync

/**
 * A simple [Fragment] subclass.
 */
class RequestFragment : BaseFragment(), MessageView, PendingRequestDelegateAdapter.onViewSelectedListener, MentorRequestsListener {

    override fun networkError() {
        super.networkError()
    }

    override fun onRejectRequestSuccess() {
        activity?.runOnUiThread {
            adapter?.removeRequest(requestListItem!!)

        }
    }

    override fun onRejectRequestFail() {
        activity?.runOnUiThread {
            activity?.showSnackBar(rootView, getString(R.string.something_went_wrong_please_try_later))
        }
    }

    override fun onAcceptRequestSuccess() {
        activity?.runOnUiThread {
            adapter?.removeRequest(requestListItem!!)
            val intent1 = Intent()
            intent1.action =Constant.ACTION_RECEIVED_REQUEST_ACCEPT
            context.sendBroadcast(intent1)
        }
    }

    override fun onAcceptRequestFail() {
        activity?.runOnUiThread {
            activity?.showSnackBar(rootView, getString(R.string.something_went_wrong_please_try_later))
        }
    }

    override fun onRejectRequest(requestListItem: RequestListItem) {
        this.requestListItem = requestListItem
        presenter.rejectRequest(requestListItem)
    }

    override fun onAcceptRequest(requestListItem: RequestListItem) {
        this.requestListItem = requestListItem
        presenter.acceptRequest(requestListItem)
    }

    override fun onProfileImageSuccess() {
        activity?.runOnUiThread {
            adapter?.notifyDataSetChanged()
        }
    }

    override fun onCancelRequestSuccess() {
        activity?.runOnUiThread {
            adapter?.removeRequest(requestListItem!!)
        }
    }

    override fun onCancelRequestFail() {
        activity?.runOnUiThread {
            activity.showSnackBar(rootView, getString(R.string.something_went_wrong_please_try_later))
        }
    }

    override fun onCancelRequest(requestListItem: RequestListItem) {
        this.requestListItem = requestListItem
        presenter.cancelRequest(requestListItem)
    }

    override fun setPendingRequestAdapter(pendingRequestList: MutableList<RequestListItem>) {
        activity?.runOnUiThread {
            if (pageNo == 0) {
                swipeToRefresh?.isRefreshing = false
                adapter?.clearAndAddRequest(pendingRequestList)
            } else {
                adapter?.addRequest(pendingRequestList)
            }
        }
    }

    override fun onSessionExpired() {
    }

    override fun showProgress() {
        activity?.showProgressBar(progressBar)
    }

    override fun hideProgress() {
        activity?.hideProgressBar(progressBar)

    }

    override fun onPendingRequestFail() {
    }

    override fun pendingRequestNotFound() {
        activity?.runOnUiThread {
            swipeToRefresh?.isRefreshing = false
            adapter?.removeLoaderFromBottom()
        }
    }

    var presenter = MessagePresenterImpl(this)
    var pageNo = 0
    var adapter: PendingRequestAdapter? = null
    var requestListItem: RequestListItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_request, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        GetMentorRequestRequester(0, this).execute()
        recyclerView.apply {
            setHasFixedSize(true)
            val linearLayout = WrapContentLinearLayoutManager(activity)
            layoutManager = linearLayout
        }
        adapter = PendingRequestAdapter(this)
        recyclerView.adapter = adapter
        adapter?.registerAdapterDataObserver(emptyMessage)
        adapter?.setOnLoadMoreListener(recyclerView, object : OnLoadMoreListener {
            override fun onLoadMore() {
                adapter!!.addLoaderAtBottom()
                presenter.getPendingRequest(++pageNo, false)
            }
        })
        presenter.getPendingRequest(pageNo, false)
        swipeToRefresh.setOnRefreshListener {
            swipeToRefresh.isRefreshing = true
            pageNo = 0
            presenter.getPendingRequest(pageNo, true)
        }
        swipeToRefresh.setColorSchemeColors(Color.RED, Color.BLUE, Color.CYAN)

        val intentFilter =  IntentFilter()
        intentFilter.addAction(Constant.ACTION_BLOCK)
        intentFilter.addAction(Constant.ACTION_REQUEST_RECEIVED)
        intentFilter.addAction(Constant.ACTION_REQUEST_SENT)
        intentFilter.addAction(Constant.ACTION_REQUEST_ACCEPT)
        activity?.registerReceiver(receiver,intentFilter)
    }

    var dbManager = DbManager.getInstance(MentorzApplication.applicationContext())

    fun refreshList() {
        swipeToRefresh?.isRefreshing = true
        doAsync {
            dbManager.setReadAllRequestNotification()
        }
        presenter.getPendingRequest(pageNo, true)
    }

    var receiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            when (intent?.action) {
                Constant.ACTION_BLOCK -> {

                    adapter?.removeOnBasisOfUserId(intent.getLongExtra("user_id", 0))
                    pageNo = 0
                    presenter.getPendingRequest(pageNo, true)
                }
                Constant.ACTION_REQUEST_RECEIVED -> {
                    pageNo = 0
                    presenter.getPendingRequest(pageNo, true)
                }
                Constant.ACTION_REQUEST_SENT->{
                    pageNo = 0
                    presenter.getPendingRequest(pageNo, true)

                }
                Constant.ACTION_REQUEST_ACCEPT->{
                    pageNo = 0
                    presenter.getPendingRequest(pageNo, true)

                }

            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        activity.unregisterReceiver(receiver)
    }
}