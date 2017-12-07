package com.mentorz.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.mentorz.MentorzApplication
import com.mentorz.R
import com.mentorz.customviews.ReportAbuseDialog
import com.mentorz.database.DbManager
import com.mentorz.extensions.hideKeyBoard
import com.mentorz.extensions.showProgressBar
import com.mentorz.listener.AbusePostListener
import com.mentorz.listener.FollowListener
import com.mentorz.listener.LikePostListener
import com.mentorz.listener.SharePostListener
import com.mentorz.model.*
import com.mentorz.requester.*
import com.mentorz.retrofit.listeners.PostByPostIdListener
import com.mentorz.retrofit.listeners.SignedUrlListener
import com.mentorz.sinchvideo.BaseActivity
import com.mentorz.stories.PostListItem
import com.mentorz.tag.TagListener
import com.mentorz.uploadfile.FileType
import com.mentorz.utils.Constant
import kotlinx.android.synthetic.main.activity_post.*
import org.jetbrains.anko.doAsync

/**
 * Created by craterzone on 05/09/17.
 */
class PostActivity : BaseActivity(), View.OnClickListener, PostByPostIdListener, SignedUrlListener, FollowListener, TagListener, LikePostListener, SharePostListener, ReportAbuseDialog.ReportAbuseDialogListener, AbusePostListener {
    override fun onAbusePostSuccess() {
        runOnUiThread {

            finish()
        }
    }

    override fun onAbusePostFail() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAbuseCancelClickListener() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onInAppropriateContentClickListener() {
        runOnUiThread {

            reportAbuseDialog?.dismiss()
            showProgressBar(progressBar)
        }
        AbusePostRequester(postItem!!, AbuseType.INAPPROPRIATE_CONTENT, this, this).execute()
    }

    override fun onSpamClickListener() {
        runOnUiThread {

            showProgressBar(progressBar)
        }
        AbusePostRequester(postItem!!, AbuseType.SPAM, this, this).execute()
    }

    override fun onSharePostSuccess(postListItem: PostListItem) {
        runOnUiThread {

            sendBroadcastForUpdatePost(postListItem)
        }
    }

    override fun onSharePostFail() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLikePostSuccess(postListItem: PostListItem) {
        runOnUiThread {

            sendBroadcastForUpdatePost(postListItem)
        }
    }

    override fun onLikePostFail() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onClickViewMore(word: String) {
        runOnUiThread {

            description.text = postItem?.content?.description
        }
    }

    override fun onFollowFail() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun checkFollowingFail() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun checkFollowingSuccess(model: Any?, isFollowing: Boolean) {
//        if (isFollowing){
//
//        }
    }

    override fun onFollowSuccess(model: Any?) {
        runOnUiThread {

            when (model) {
                is PostListItem -> {
                    val intent = Intent()
                    intent.action = Constant.ACTION_FOLLOW
                    intent.putExtra("user_id", model.userId)
                    intent.putExtra("is_following", model.isFollowing)
                    sendBroadcast(intent)
                }
                is ProfileData -> {
                    val intent = Intent()
                    intent.action = Constant.ACTION_FOLLOW
                    intent.putExtra("user_id", model.userId)
                    intent.putExtra("is_following", model.isFollowing)
                    sendBroadcast(intent)
                }
            }
        }

    }

    override fun onNetworkFail() {
        runOnUiThread {

            networkError()
        }
    }

    override fun signedUrlSuccess(url: String, model: Any?) {
        runOnUiThread {

            when (model) {
                is ProfileImage? -> {
                    postItem?.profileImage = url
                    com.squareup.picasso.Picasso.with(this).load(postItem?.profileImage).placeholder(com.mentorz.R.drawable.default_avatar).resize(200, 200).centerCrop()
                            .into(profileImage)
                }

                is PostContent -> {
                    postItem?.contentImage = url
                    com.squareup.picasso.Picasso.with(this).load(postItem?.contentImage).placeholder(R.drawable.default_post_image)
                            .into(imgPost)
                }
                is PostVideo -> {
                    postItem?.contentVideo = url

                }
            }
        }
    }

    override fun onPostByPostIdSuccess(item: PostListItem) {
        runOnUiThread {
            postItem = item
            if (item.content != null && item.content.mediaType != null) {
                if (item.content.mediaType.equals("VIDEO")) {
                    val model = PostVideo()
                    model.postId = item.postId
                    SignedUrlRequester(FileType.FILE, this, model, item.content.hresId).execute()
                }
                val model = PostContent()
                model.postId = item.postId
                SignedUrlRequester(FileType.FILE, this, model, item.content.lresId).execute()
            }
            description.movementMethod = android.text.method.LinkMovementMethod.getInstance()

            followFollowing.setOnClickListener {
                FollowRequester(item, item.userId!!, this, this).execute()
            }
            name.text = item.name
            imgLike.isSelected = item.liked!!
            txtPostTime.text = com.mentorz.utils.DateUtils.getDurationFromDate(item.shareTime!!)

            description.text = item.content!!.description

            if (item.content.description?.length!! > com.mentorz.utils.Constant.MAX_CHAR_LENGTH) {
                description.text = com.mentorz.tag.TagUtil.getSpannableString(this, item.content.description?.substring(0, com.mentorz.utils.Constant.MAX_CHAR_LENGTH) + com.mentorz.utils.Constant.READ_MORE_TEXT)
            } else {
                description.text = com.mentorz.tag.TagUtil.getSpannableString(this, item.content.description!!)

            }
            txtPostTime.text = com.mentorz.utils.DateUtils.getDurationFromDate(item.shareTime)
            layoutReport.isSelected = false
            report.isSelected = false
            rating.rating = item.rating
            if (!android.text.TextUtils.isEmpty(item.profileImage)) {
                val model = ProfileImage()
                SignedUrlRequester(FileType.FILE, this, model, item.content.hresId).execute()
            }
            if (!android.text.TextUtils.isEmpty(item.contentImage)) {
                com.squareup.picasso.Picasso.with(this).load(item.contentImage).into(imgPost)
            }
            if (item.content.mediaType.equals("VIDEO")) {
                imgVideo.visibility = android.view.View.VISIBLE

            } else {
                imgVideo.visibility = android.view.View.GONE
            }
            imgPost.setOnClickListener {
                if (item.content.mediaType.equals("VIDEO")) {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(Uri.parse(postItem!!.contentVideo), "video/mp4")
                    startActivity(intent)
                } else {
                    com.mentorz.utils.DialogUtils.showImagePreview(this, item.contentImage).show()
                }
//            viewActions.onPostClick(item)
            }

            if (item.isFollowing!!) {
                followFollowing.visibility = android.view.View.VISIBLE
                followFollowing.setBackgroundResource(com.mentorz.R.drawable.red_background)
                followFollowing.setTextColor(resources.getColor(com.mentorz.R.color.white))
                followFollowing.text = getString(com.mentorz.R.string.following)

            } else {
                if (item.userId != com.mentorz.MentorzApplication.instance?.prefs?.getUserId()) {
                    followFollowing.visibility = android.view.View.VISIBLE
                    followFollowing.setTextColor(resources.getColor(com.mentorz.R.color.colorAccent))
                    followFollowing.setBackgroundResource(com.mentorz.R.drawable.red_corner_background)
                    followFollowing.text = getString(com.mentorz.R.string.follow)
                } else {
                    followFollowing.visibility = android.view.View.GONE
                }
            }

            if (item.userId != com.mentorz.MentorzApplication.instance?.prefs?.getUserId()) {
                layoutReport.visibility = android.view.View.VISIBLE
            } else {
                layoutReport.visibility = android.view.View.GONE
            }
            setLikeCount(this, item)
            txtCommentCount.text =
                    if (item.commentCount!!.toLong() > 1) {
                        item.commentCount.toString() + " " + getString(com.mentorz.R.string.comments)
                    } else {
                        item.commentCount.toString() + " " + getString(com.mentorz.R.string.comment)
                    }
            txtViewsCount.text =
                    if (item.viewCount!!.toLong() > 1) {
                        item.viewCount.toString() + " " + getString(com.mentorz.R.string.views)
                    } else {
                        item.viewCount.toString() + " " + getString(com.mentorz.R.string.view)
                    }
            txtShareCount.text =
                    if (item.shareCount!!.toLong() > 1) {
                        item.shareCount.toString() + " " + getString(com.mentorz.R.string.shares)
                    } else {
                        item.shareCount.toString() + " " + getString(com.mentorz.R.string.share)
                    }

            rLike.setOnClickListener {
                if (!item.liked!!) {
                    item.liked = true
                    item.likeCount = item.likeCount!!.plus(1)
                    setLikeCount(this, item)
                    LikeRequester(postItem!!, this, this).execute()

                }
            }
            rComment.setOnClickListener {
                this.postItem = postItem
                val intent = Intent(this, CommentActivity::class.java)
                intent.putExtra("user_id",postItem?.userId)
                intent.putExtra("post_id", postItem?.postId)
                startActivityForResult(intent, COMMENT_REQUEST_CODE)
            }
            rShare.setOnClickListener {
                this.postItem = postItem
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                if (postItem?.content?.mediaType.equals("VIDEO", true)) {
                    sendIntent.putExtra(Intent.EXTRA_TEXT, postItem?.content?.description + " " + postItem?.contentVideo)
                } else {
                    sendIntent.putExtra(Intent.EXTRA_TEXT, postItem?.content?.description + " " + postItem?.contentImage)
                }
                sendIntent.type = "text/plain"
                startActivityForResult(Intent.createChooser(sendIntent, "Share"), SHARE_REQUEST_CODE)
            }
            layoutReport.setOnClickListener {
                layoutReport.isSelected = true
                report.isSelected = true
                reportAbuseDialog = ReportAbuseDialog(this, this)
                reportAbuseDialog?.show()
            }
//        rPost.setOnClickListener {
//            viewActions.onPostClick(item)
//
//        }
            profileImage.setOnClickListener {
                val intent = Intent(this, MentorProfileActivity::class.java)
                intent.putExtra("user_id", postItem!!.userId)
                startActivity(intent)

            }
        }

    }

    override fun onPostByPostIdFail() {
        networkError()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            imgBack.id -> {
                finish()
            }
        }
    }

    var reportAbuseDialog: ReportAbuseDialog? = null
    var postId: Long? = 0
    var userId: Long? = 0
    var pushType: String? = null
    var dbManager = DbManager.getInstance(MentorzApplication.applicationContext())

    var postItem: PostListItem? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        postId = intent.getLongExtra("post_id", 0)
        userId = intent.getLongExtra("user_id", 0)
        pushType = intent.getStringExtra("push_type")
        doAsync {
            dbManager.setReadNotification(userId!!, postId!!,pushType)
        }
        setSupportActionBar(toolbar)
        imgBack.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        PostByPostIdRequester(postId, this, this).execute()
    }

    fun setLikeCount(context: Context, item: PostListItem) {
        txtLikeCount.text =
                if (item.likeCount!!.toLong() > 1) {
                    item.likeCount.toString() + " " + context.getString(R.string.likes)
                } else {
                    item.likeCount.toString() + " " + context.getString(R.string.like)
                }
    }

    private fun sendBroadcastForUpdatePost(postListItem: PostListItem) {
        val intent = Intent()
        intent.action = Constant.ACTION_UPDATE_POST
        intent.putExtra("post_item", postListItem)
        sendBroadcast(intent)
    }

    private val SHARE_REQUEST_CODE = 100

    private val COMMENT_REQUEST_CODE = 106
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            SHARE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK && postItem != null) {
                    SharePostRequester(postItem!!, this, this).execute()
                }
            }
            COMMENT_REQUEST_CODE -> {

                if (resultCode == Activity.RESULT_OK && postItem != null) {
                    postItem?.commentCount = data?.getIntExtra("comment_count", 0)?.toLong()
                    val count : Long? = data?.getIntExtra("comment_count", 0)?.toLong()
                    if (count != null) {
                        txtCommentCount.text =
                                if (count > 1) {
                                    count.toString() + " " + getString(com.mentorz.R.string.comments)
                                } else {
                                    count.toString() + " " + getString(com.mentorz.R.string.comment)
                                }
                    }
                    sendBroadcastForUpdatePost(postItem!!)
                }
            }


        }
    }

}