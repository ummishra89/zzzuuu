package com.mentorz.activities

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import com.mentorz.R
import com.mentorz.extensions.hideKeyBoard
import com.mentorz.extensions.hideProgressBar
import com.mentorz.extensions.showProgressBar
import com.mentorz.extensions.showSnackBar
import com.mentorz.feedback.FeedbackPresenterImpl
import com.mentorz.feedback.FeedbackView
import com.mentorz.sinchvideo.BaseActivity
import kotlinx.android.synthetic.main.activity_feedback.*
import kotlinx.android.synthetic.main.toolbar_with_title.*
import org.jetbrains.anko.doAsync


class FeedbackActivity : BaseActivity(), FeedbackView, View.OnClickListener, View.OnTouchListener {

    override fun networkError() {
        super.networkError()
    }
    override fun onSessionExpired() {
        runOnUiThread {

        }
    }

    override fun onEmptyDescription() {
        showSnackBar(rootView, getString(R.string.please_write_feedback))
    }

    override fun onRatingNotSelected() {
        showSnackBar(rootView, getString(R.string.please_rate_app_first))
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.sendFeedback -> {
                presenter.sendFeedback(scaleCount, edtFeedback.text.toString())
            }
            R.id.imgBack -> {
                finish()
            }
            R.id.t1 -> {
                scaleCount = 1
                setRating(0)
            }
            R.id.t2 -> {
                scaleCount = 2
                setRating(1)
            }
            R.id.t3 -> {
                scaleCount = 3
                setRating(2)
            }
            R.id.t4 -> {
                scaleCount = 4
                setRating(3)
            }
            R.id.t5 -> {
                scaleCount = 5
                setRating(4)
            }
            R.id.t6 -> {
                scaleCount = 6
                setRating(5)
            }
            R.id.t7 -> {
                scaleCount = 7
                setRating(6)
            }
            R.id.t8 -> {
                scaleCount = 8
                setRating(7)
            }
            R.id.t9 -> {
                scaleCount = 9
                setRating(8)
            }
            R.id.t10 -> {
                scaleCount = 10
                setRating(9)
            }

        }
    }

    override fun onFeedbackSuccess() {
        runOnUiThread {
            showSnackBar(rootView, getString(R.string.feedback_submitted_successfully))
            doAsync {
                Thread.sleep(1500)
                runOnUiThread {
                    finish()
                }
            }
        }

    }

    override fun onFeedbackFail() {
        runOnUiThread {
            showSnackBar(rootView, getString(R.string.something_went_wrong_please_try_later))
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

    val presenter = FeedbackPresenterImpl(this)
    var items: Array<TextView>? = null
    var scaleCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
        items = arrayOf(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10)
        setSupportActionBar(toolbar)
        toolbar_title.text = getString(R.string.Feedback)
        sendFeedback.setOnClickListener(this)
        imgBack.setOnClickListener(this)
        t1.setOnClickListener(this)
        t2.setOnClickListener(this)
        t3.setOnClickListener(this)
        t4.setOnClickListener(this)
        t5.setOnClickListener(this)
        t6.setOnClickListener(this)
        t7.setOnClickListener(this)
        t8.setOnClickListener(this)
        t9.setOnClickListener(this)
        t10.setOnClickListener(this)
        rootView.setOnTouchListener(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun setRating(position: Int) {
        for (i in 0..9) {
            items!![i].isSelected = position == i
        }
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        hideKeyBoard()
        return true
    }

}
