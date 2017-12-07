package com.mentorz.stories.adapter

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
import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.android.synthetic.main.layout_image_preview.view.*
import kotlinx.android.synthetic.main.post_list_item.view.*

class PostDelegateAdapter(adapter: PostListAdapter, val viewActions: onViewSelectedListener) : ViewTypeDelegateAdapter {

    interface onViewSelectedListener {
        fun onCommentClick(postListItem: PostListItem)
        fun onLikeClick(postListItem: PostListItem)
        fun onShareClick(postListItem: PostListItem)
        fun onProfileClick(postListItem: PostListItem)
        fun onFollowClick(postListItem: PostListItem)
        fun onUnFollowClick(postListItem: PostListItem)
        fun onPostClick(postListItem: PostListItem)
        fun onAbuseClick(position: Int, postListItem: PostListItem)
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


        override fun onClickViewMore(word: String) {
            itemView.description.text = posItem?.content?.description
        }


        fun setLikeCount(context: Context, item: PostListItem) {
            itemView.txtLikeCount.text =
                    if (item.likeCount!!.toLong() > 1) {
                        item.likeCount.toString() + " " + context.getString(R.string.likes)
                    } else {
                        item.likeCount.toString() + " " + context.getString(R.string.like)
                    }
        }

        fun bind(position: Int, item: PostListItem) = with(itemView) {
            //Picasso.with(itemView.context).load(item.thumbnail).into(img_thumbnail)
            // img_thumbnail.loadImg(item.thumbnail)
            posItem = item
            description.movementMethod = LinkMovementMethod.getInstance()

            followFollowing.setOnClickListener {
                if (!item.isFollowing!!) {
                    viewActions.onFollowClick(item)
                }else{
                    viewActions.onUnFollowClick(item)
                }
            }

            name.text = item.name

            imgLike.isSelected = item.liked!!
            txtPostTime.text = DateUtils.getDurationFromDate(item.shareTime!!)

            description.text = item.content!!.description

            if (item.content.description?.length!! > Constant.MAX_CHAR_LENGTH) {
                description.text = TagUtil.getSpannableString(this@NewsViewHolder, item.content.description?.substring(0, Constant.MAX_CHAR_LENGTH) + Constant.READ_MORE_TEXT)
            } else {
                description.text = TagUtil.getSpannableString(this@NewsViewHolder, item.content.description!!)

            }
            txtPostTime.text = DateUtils.getDurationFromDate(item.shareTime)
            layoutReport.isSelected = false
            report.isSelected = false
            rating.rating = item.rating
            if (!TextUtils.isEmpty(item.contentImage)){
                com.squareup.picasso.Picasso.with(context).load(item.contentImage).placeholder(R.drawable.default_post_image)
                        .into(imgPost)
            }
            profileImage.loadThumbnailUrl(context,item.profileImage)
//            imgPost.loadPostUrl(context,item.contentImage)

            if (item.content.mediaType.equals("VIDEO")) {
                imgVideo.visibility = View.VISIBLE

            } else {
                imgVideo.visibility = View.GONE
            }

            imgPost.layoutParams.height = imgPost.layoutParams.width

            imgPost.setOnClickListener {
                if(item.content.mediaType.equals("VIDEO")) {
                    viewActions.onVideoClick(item.contentVideo)
                } else{
                    DialogUtils.showImagePreview(context,item.contentImage).show()
                }
                viewActions.onPostClick(item)
            }

            if (item.isFollowing!!) {
                followFollowing.visibility = View.VISIBLE
                followFollowing.setBackgroundResource(R.drawable.red_background)
                followFollowing.setTextColor(context.resources.getColor(R.color.white))
                followFollowing.text = context.getString(R.string.following)

            } else {
                if (item.userId != MentorzApplication.instance?.prefs?.getUserId()) {
                    followFollowing.visibility = View.VISIBLE
                    followFollowing.setTextColor(context.resources.getColor(R.color.colorAccent))
                    followFollowing.setBackgroundResource(R.drawable.red_corner_background)
                    followFollowing.text = context.getString(R.string.follow)
                } else {
                    followFollowing.visibility = View.GONE
                }
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
            layoutReport.setOnClickListener {
                layoutReport.isSelected = true
                report.isSelected = true
                viewActions.onAbuseClick(position, item)
            }
            rPost.setOnClickListener {
                viewActions.onPostClick(item)

            }
            name.setOnClickListener{
                viewActions.onProfileClick(item)
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
              //  viewActions.onFollowClick(item)
            }
        }
    }
}