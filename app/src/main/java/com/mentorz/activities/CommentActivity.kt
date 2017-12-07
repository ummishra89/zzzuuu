package com.mentorz.activities

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.text.*
import android.view.View
import com.mentorz.R
import com.mentorz.adapter.CommentDelegateAdapter
import com.mentorz.adapter.CommentListAdapter
import com.mentorz.extensions.hideKeyBoard
import com.mentorz.extensions.hideProgressBar
import com.mentorz.extensions.showProgressBar
import com.mentorz.extensions.showSnackBar
import com.mentorz.listener.OnLoadMoreListener
import com.mentorz.manager.WrapContentLinearLayoutManager
import com.mentorz.model.CommentListItem
import com.mentorz.sinchvideo.BaseActivity
import com.mentorz.stories.PostPresenterImpl
import com.mentorz.stories.PostView
import kotlinx.android.synthetic.main.activity_comment.*
import android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import com.mentorz.MentorzApplication
import com.mentorz.database.DbManager
import com.mentorz.model.ProfileImage
import com.mentorz.model.ProfileImageResponse
import com.mentorz.requester.ProfileImageRequester
import com.mentorz.requester.SignedUrlRequester
import com.mentorz.retrofit.listeners.ProfileImageListener
import com.mentorz.retrofit.listeners.SignedUrlListener
import com.mentorz.uploadfile.FileType
import com.mentorz.utils.CircleTransform
import com.mentorz.utils.Constant
import com.mentorz.utils.EncodingDecoding
import com.squareup.picasso.Picasso
import org.jetbrains.anko.doAsync
import java.util.logging.Logger


class CommentActivity : BaseActivity(), View.OnClickListener, PostView, CommentDelegateAdapter.onViewSelectedListener, ProfileImageListener, SignedUrlListener {
    override fun onNetworkFail() {

    }

    override fun signedUrlSuccess(fileType: String, url: String, model: Any?) {
        super.signedUrlSuccess(fileType, url, model)
    }

    override fun signedUrlSuccess(url: String, model: Any?) {
        runOnUiThread {
            Picasso.with(this).load(url).placeholder(R.drawable.default_avatar).resize(200, 200).centerCrop()
                    .into(img_thumbnail)
        }
    }

    override fun profileImageSuccess(profileImageResponse: ProfileImageResponse, userId: Long?) {
        val model = ProfileImage()
        model.userId = userId
        SignedUrlRequester(FileType.FILE, this, model, profileImageResponse.hresId).execute()
    }

    override fun profileImageFail() {

    }

    override fun networkError() {
        super.networkError()
    }

    override fun onSessionExpired() {
        super.onSessionExpired()
    }

    override fun dataSetChanged() {
        runOnUiThread {
            recyclerView.adapter?.notifyDataSetChanged()
        }
    }

    override fun noCommentFound() {
        runOnUiThread {
            adapter?.removeLoaderFromBottom()
        }
    }

    private var commentListItem: CommentListItem? = null

    override fun onPostCommentSuccess(commentListItem: CommentListItem) {
        runOnUiThread {
            adapter?.addComment(commentListItem)
            recyclerView.scrollToPosition(adapter?.itemCount!!.minus(1))
        }
    }

    override fun onPostCommentFail() {
        runOnUiThread {
            showSnackBar(rootView, getString(R.string.something_went_wrong_please_try_later))
        }
    }

    override fun showProgress() {
        showProgressBar(progressBar)
    }

    override fun hideProgress() {
        hideProgressBar(progressBar)
    }

    override fun onDeleteComment(commentListItem: CommentListItem) {
        this.commentListItem = commentListItem
        presenter.deleteComment(postId!!, commentListItem)

    }

    override fun onDeleteCommentSuccess() {
        runOnUiThread {
            (recyclerView.adapter as CommentListAdapter).removeComment(commentListItem!!)
        }
    }

    override fun setCommentAdapter(commentList: MutableList<CommentListItem>) {
        runOnUiThread {
            (recyclerView.adapter as CommentListAdapter).addComment(commentList)
        }
    }

    var pageNo: Int = 0
    override fun onClick(p0: View?) {
        when (p0?.id) {
            imgBack.id -> {
                val intent = Intent()
                intent.putExtra("comment_count", adapter?.getRows()!!.size)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            txtPost.id -> {
                val comment: String = edtComment.text.trim().toString()
                if (comment.isNotEmpty()) {
                    presenter.postComment(userId!!, comment, postId!!)
                    edtComment.text.clear()
                    hideKeyBoard()
                }

            }
        }
    }

    var receiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            when (intent?.action) {
                Constant.ACTION_BLOCK -> {
                    adapter?.removeCommentsByUserID(intent.getLongExtra("user_id", 0))
                }
            }
        }

    }


    var postId: Long? = 0
    var userId: Long? = 0
    var userName: String? = null
    var presenter = PostPresenterImpl(this)
    var adapter: CommentListAdapter? = null
    var pushType: String? = null
    val dbManager = DbManager.getInstance(MentorzApplication.applicationContext())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)
        postId = intent.getLongExtra("post_id", 0)
        userId = intent.getLongExtra("user_id", 0)
        userName = intent.getStringExtra("user_name")
        pushType = intent.getStringExtra("push_type")
        val intentFilter = IntentFilter()
        intentFilter.addAction(Constant.ACTION_BLOCK)
        registerReceiver(receiver, intentFilter)
        if (TextUtils.isEmpty(userName)) {
            notificationLayout.visibility = View.GONE
        } else {
            ProfileImageRequester(userId, this).execute()
            notificationLayout.visibility = View.VISIBLE
            doAsync {
                dbManager.setReadNotification(userId!!, postId!!, pushType)
                //  dbManager.setReadNotification(userId!!, postId!!,pushType)
            }
            val spannableString = SpannableString(EncodingDecoding.decodeString(userName!!) + " commented on your Post")
            val span1 = object : ClickableSpan() {
                override fun updateDrawState(ds: TextPaint?) {
                    ds?.color = Color.CYAN;    // you can use custom color
                    ds?.isUnderlineText = false;
                }

                override fun onClick(textView: View) {
                    val intent = Intent(this@CommentActivity, MentorProfileActivity::class.java)
                    intent.putExtra("user_id", userId!!)
                    startActivity(intent)
                }
            }

            val span2 = object : ClickableSpan() {
                override fun updateDrawState(ds: TextPaint?) {
                    ds?.color = Color.CYAN;    // you can use custom color
                    ds?.isUnderlineText = false;
                }

                override fun onClick(textView: View) {
                    val intent = Intent(this@CommentActivity, PostActivity::class.java)
                    intent.putExtra("post_id", postId!!)
                    startActivity(intent)
                }
            }

            spannableString.setSpan(span1, 0, EncodingDecoding.decodeString(userName!!).length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(span2, spannableString.length - "Post".length, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(RelativeSizeSpan(1.5f), 0, EncodingDecoding.decodeString(userName!!).length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(RelativeSizeSpan(1.5f), spannableString.length - "Post".length, spannableString.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            spannableString.setSpan(ForegroundColorSpan(Color.BLUE), 0, EncodingDecoding.decodeString(userName!!).length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(ForegroundColorSpan(Color.BLUE), spannableString.length - "Post".length, spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            text.text = spannableString
            text.movementMethod = LinkMovementMethod.getInstance()
        }
        setSupportActionBar(toolbar)
        imgBack.setOnClickListener(this)
        txtPost.setOnClickListener(this)

        recyclerView.apply {
            setHasFixedSize(true)
            val linearLayout = WrapContentLinearLayoutManager(this@CommentActivity)
            layoutManager = linearLayout

        }
        adapter = CommentListAdapter(this)
        adapter?.registerAdapterDataObserver(emptyMessage)
        recyclerView.adapter = adapter
        presenter.getComment(userId!!, postId!!, pageNo)
        adapter!!.setOnLoadMoreListener(recyclerView, object : OnLoadMoreListener {
            override fun onLoadMore() {
                adapter!!.addLoaderAtBottom()
                presenter.getComment(userId!!, postId!!, ++pageNo)
            }
        })
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("comment_count", adapter?.getRows()!!.size)
        setResult(Activity.RESULT_OK, intent)
        finish()

    }
}
