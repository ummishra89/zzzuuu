package com.mentorz.me

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.ViewGroup
import com.mentorz.MentorzApplication
import com.mentorz.R
import com.mentorz.extensions.inflate
import com.mentorz.extensions.loadPostUrl
import com.mentorz.extensions.loadThumbnailUrl
import com.mentorz.match.adapter.ViewType
import com.mentorz.match.adapter.ViewTypeDelegateAdapter
import com.mentorz.stories.PostListItem
import com.mentorz.tag.TagListener
import com.mentorz.tag.TagUtil
import com.mentorz.utils.Constant
import com.mentorz.utils.DateUtils
import com.mentorz.utils.DialogUtils
import kotlinx.android.synthetic.main.post_list_item.view.*

class MentorPostDelegateAdapter(adapter: MentorPostListAdapter, val viewActions: onViewSelectedListener) : ViewTypeDelegateAdapter {

    interface onViewSelectedListener {
        fun onCommentClick(postListItem: PostListItem)
        fun onLikeClick(postListItem: PostListItem)
        fun onShareClick(postListItem: PostListItem)
        fun onProfileClick(postListItem: PostListItem)
        fun onFollowClick(postListItem: PostListItem)
        fun onUnFollowClick(postListItem: PostListItem)
        fun onPostClick(postListItem: PostListItem)
        fun onAbuseClick(postListItem: PostListItem)
        fun onVideoClick(url: String)
    }

    var adapter = adapter

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return NewsViewHolder(parent)
    }

    override fun onBindViewHolder(position: Int, holder: RecyclerView.ViewHolder, item: ViewType) {
        holder as NewsViewHolder
        holder.bind(position, item as PostListItem)

    }

    inner class NewsViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            parent.inflate(R.layout.post_list_item)), TagListener {

        var posItem: PostListItem? = null

        fun setLikeCount(context: Context, item: PostListItem) {
            itemView.txtLikeCount.text =
                    if (item.likeCount!!.toLong() > 1) {
                        item.likeCount.toString() + " " + context.getString(R.string.likes)
                    } else {
                        item.likeCount.toString() + " " + context.getString(R.string.like)
                    }
        }

        override fun onClickViewMore(word: String) {
            itemView.description.text = posItem?.content?.description
        }

        fun bind(position: Int, item: PostListItem) = with(itemView) {
            posItem = item
            followFollowing.setOnClickListener {
                if (!item.isFollowing!!) {
                    viewActions.onFollowClick(item)
                }else{
                    viewActions.onUnFollowClick(item)
                }


            }
            description.movementMethod = LinkMovementMethod.getInstance()
            name.text = item.name
            imgLike.isSelected = item.liked!!
            if (item.content!!.description?.length!! > Constant.MAX_CHAR_LENGTH) {
                description.text = TagUtil.getSpannableString(this@NewsViewHolder, item.content.description?.substring(0, Constant.MAX_CHAR_LENGTH) + Constant.READ_MORE_TEXT)
            } else {
                description.text = TagUtil.getSpannableString(this@NewsViewHolder, item.content.description!!)

            }
            txtPostTime.text = DateUtils.getDurationFromDate(item.shareTime!!)

            rating.rating = item.rating

            if (item.userId == MentorzApplication.instance?.prefs?.getUserId()){
                profileImage.loadThumbnailUrl(context,MentorzApplication.instance?.prefs?.getProfilePictureLres())
            }else {
                profileImage.loadThumbnailUrl(context, item.profileImage)
            }
            if (!TextUtils.isEmpty(item.contentImage))
                com.squareup.picasso.Picasso.with(context).load(item.contentImage).placeholder(R.drawable.default_post_image)
                        .into(imgPost)
            if (item.content.mediaType.equals("VIDEO")) {
                imgVideo.visibility = View.VISIBLE
                imgPost.setOnClickListener { viewActions.onVideoClick(item.contentVideo) }
            } else {
                imgPost.setOnClickListener { DialogUtils.showImagePreview(context,item.contentImage).show()}
                imgVideo.visibility = View.GONE
            }

            if (item.userId != MentorzApplication.instance?.prefs?.getUserId()) {
                followFollowing.visibility = View.VISIBLE

                if (item.isFollowing!!) {
                    followFollowing.visibility = View.VISIBLE
                    followFollowing.setBackgroundResource(R.drawable.red_background)
                    followFollowing.setTextColor(context.resources.getColor(R.color.white))
                    followFollowing.text = context.getString(R.string.following)

                } else {
                    followFollowing.setTextColor(context.resources.getColor(R.color.colorAccent))
                    followFollowing.setBackgroundResource(R.drawable.red_corner_background)
                    followFollowing.text = context.getString(R.string.follow)
                }
            } else {
                followFollowing.visibility = View.GONE
            }

            if (item.userId != MentorzApplication.instance?.prefs?.getUserId()) {
                layoutReport.visibility = View.VISIBLE
            } else {
                layoutReport.visibility = View.GONE
            }
            setLikeCount(context, item)
            txtCommentCount.text =
                    if (item.commentCount!!.toLong() > 1) {
                        item.commentCount.toString() + " " + context.getString(R.string.comments)
                    } else {
                        item.commentCount.toString() + " " + context.getString(R.string.comment)
                    }
            txtViewsCount.text =
                    if (item.viewCount!!.toLong() > 1) {
                        item.viewCount.toString() + " " + context.getString(R.string.views)
                    } else {
                        item.viewCount.toString() + " " + context.getString(R.string.view)
                    }
            txtShareCount.text =
                    if (item.shareCount!!.toLong() > 1) {
                        item.shareCount.toString() + " " + context.getString(R.string.shares)
                    } else {
                        item.shareCount.toString() + " " + context.getString(R.string.share)
                    }

            rLike.setOnClickListener {
                if (!item.liked!!) {
                    item.liked = true
                    item.likeCount = item.likeCount!!.plus(1)
                    setLikeCount(context, item)
                    viewActions.onLikeClick(item)
                }
            }
            rComment.setOnClickListener {
                viewActions.onCommentClick(item)

            }
            rShare.setOnClickListener {
                viewActions.onShareClick(item)
            }
            report.setOnClickListener {
                viewActions.onAbuseClick(item)
            }
            rPost.setOnClickListener {

                viewActions.onPostClick(item)

            }
            profileImage.setOnClickListener {
                viewActions.onProfileClick(item)

            }
            followFollowing.setOnClickListener {
                if (!item.isFollowing!!) {
                    viewActions.onFollowClick(item)
                }else{
                    viewActions.onUnFollowClick(item)
                }

            }
        }
    }
}