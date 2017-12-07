package com.mentorz.block

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.mentorz.R
import com.mentorz.extensions.hideProgressBar
import com.mentorz.extensions.showProgressBar
import com.mentorz.extensions.showSnackBar
import com.mentorz.listener.OnLoadMoreListener
import com.mentorz.manager.WrapContentLinearLayoutManager
import com.mentorz.match.UserListItem
import com.mentorz.sinchvideo.BaseActivity
import kotlinx.android.synthetic.main.activity_blocked_users.*

class BlockedUsersActivity : BaseActivity(), View.OnClickListener, BlockUserView, BlockedUserDelegateAdapter.onViewSelectedListener {

    override fun networkError() {
        super.networkError()
    }
    override fun onProfileImageSuccess() {
        runOnUiThread {
            adapter?.notifyDataSetChanged()
        }
    }

    override fun unBlockUser(userListItem: UserListItem) {
        this.userListItem=userListItem
        presenter.unBlockUser(userListItem.userId!!)
    }

    override fun blockedUserNotFound() {
        runOnUiThread {
           adapter?.removeLoaderFromBottom()
        }

    }
    override fun onUnBlockUserSuccess() {
        runOnUiThread {
            adapter?.removeUser(userListItem!!)
        }
    }

    override fun onUnBlockUserFail() {
        runOnUiThread {
            showSnackBar(rootView,getString(R.string.something_went_wrong_please_try_later))
        }
    }

    override fun getBlockedUserFail() {
        runOnUiThread {
            showSnackBar(rootView,getString(R.string.something_went_wrong_please_try_later))
        }
    }

    override fun showProgress() {
        showProgressBar(progressBar)
    }

    override fun hideProgress() {
        hideProgressBar(progressBar)
    }

    override fun setBlockedUserAdapter(blockedUserList: MutableList<UserListItem>) {
        runOnUiThread {
            if (pageNo == 0) {
                swipeToRefresh.isRefreshing = false
                adapter?.clearAndUser(blockedUserList)
            } else {
                adapter?.addUsers(blockedUserList)

            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            imgBack.id -> finish()

        }
    }

    var presenter =BlockUserPresenterImpl(this)
    var pageNo=0
    var adapter:BlockedUserAdapter?=null
    var userListItem:UserListItem?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blocked_users)
        imgBack.setOnClickListener(this)
        txtTitle.text = getString(R.string.blocked_users)

        recyclerView.apply {
            setHasFixedSize(true)
            val linearLayout = WrapContentLinearLayoutManager(this@BlockedUsersActivity)
            layoutManager = linearLayout
        }
        adapter = BlockedUserAdapter( this)
        recyclerView.adapter = adapter
        adapter?.registerAdapterDataObserver(emptyMessage)
        presenter.getBlockedUsers(pageNo,false)
        adapter!!.setOnLoadMoreListener(recyclerView,object : OnLoadMoreListener {
            override fun onLoadMore() {
                adapter!!.addLoaderAtBottom()
                presenter.getBlockedUsers(++pageNo,false)
            }
        })
        swipeToRefresh.setOnRefreshListener {
            swipeToRefresh.isRefreshing = true
            pageNo = 0
            presenter.getBlockedUsers(pageNo,true)
        }
        swipeToRefresh.setColorSchemeColors(Color.RED, Color.BLUE, Color.CYAN)
    }
}
