package com.mentorz.review

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.mentorz.R
import com.mentorz.activities.BaseActivity
import com.mentorz.extensions.hideProgressBar
import com.mentorz.extensions.showProgressBar
import com.mentorz.listener.OnLoadMoreListener
import com.mentorz.manager.WrapContentLinearLayoutManager
import com.mentorz.match.UserListItem
import kotlinx.android.synthetic.main.activity_review.*

class ReviewActivity : BaseActivity(), ReviewView, ReviewDelegateAdapter.onViewSelectedListener {

    override fun networkError() {
        super.networkError()
    }
    override fun dataSetChanged() {
        runOnUiThread {
            adapter?.notifyDataSetChanged()
        }
    }

    override fun onSessionExpired() {
        runOnUiThread {

        }
    }

    override fun noReviewFound() {
        runOnUiThread {
            swipeToRefresh.isRefreshing = false
            adapter?.removeLoaderFromBottom()

        }
    }

    override fun showProgress() {
        runOnUiThread {
            showProgressBar(progressBar)
        }
    }

    override fun hideProgress() {
        runOnUiThread {
            hideProgressBar(progressBar)
        }
    }


    override fun setReviewAdapter(reviewList: MutableList<UserListItem>) {
        runOnUiThread {
            if (pageNo == 0) {
                swipeToRefresh.isRefreshing = false
                adapter?.clearAndReviews(reviewList)
            } else {
                adapter?.addReviews(reviewList)

            }
        }
    }

    override fun onRatingResponseSuccess() {
        runOnUiThread {
            (recyclerView.adapter as ReviewListAdapter).notifyDataSetChanged()
        }
    }

    var presenter = ReviewPresenterImpl(this)
    var userId: Long = 0
    var pageNo: Int = 0
    var adapter: ReviewListAdapter?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)
        setSupportActionBar(toolbar)
        imgBack.setOnClickListener {
            finish()
        }
        userId = intent.getLongExtra("user_id", 0)

        recyclerView.apply {
            setHasFixedSize(true)
            val linearLayout = WrapContentLinearLayoutManager(this@ReviewActivity)
            layoutManager = linearLayout
        }
        adapter= ReviewListAdapter( this)
        recyclerView.adapter =adapter
        adapter?.registerAdapterDataObserver(emptyMessage)
        presenter.getReviews(userId, pageNo,false)
        adapter!!.setOnLoadMoreListener(recyclerView,object : OnLoadMoreListener {
            override fun onLoadMore() {
                adapter!!.addLoaderAtBottom()
                presenter.getReviews(userId,++pageNo,false)
            }
        })
        swipeToRefresh.setOnRefreshListener {
            swipeToRefresh.isRefreshing = true
            pageNo = 0
            presenter.getReviews(userId, pageNo,true)
        }
        swipeToRefresh.setColorSchemeColors(Color.RED, Color.BLUE, Color.CYAN)

    }

}
