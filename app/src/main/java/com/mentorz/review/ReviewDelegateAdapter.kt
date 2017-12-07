package com.mentorz.review

import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.ViewGroup
import com.mentorz.MentorzApplication
import com.mentorz.R
import com.mentorz.extensions.inflate
import com.mentorz.match.UserListItem
import com.mentorz.match.adapter.ViewType
import com.mentorz.match.adapter.ViewTypeDelegateAdapter
import com.mentorz.utils.DateUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.review_list_item.view.*

class ReviewDelegateAdapter(adapter: ReviewListAdapter, val viewActions: onViewSelectedListener) : ViewTypeDelegateAdapter {

    interface onViewSelectedListener {

    }

    var adapter = adapter

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return NewsViewHolder(parent)
    }

    override fun onBindViewHolder(position: Int, holder: RecyclerView.ViewHolder, item: ViewType) {
        holder as NewsViewHolder
        holder.bind(position, item as UserListItem)

    }

    inner class NewsViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            parent.inflate(R.layout.review_list_item)) {

        fun bind(position: Int, item: UserListItem) = with(itemView) {
            //Picasso.with(itemView.context).load(item.thumbnail).into(img_thumbnail)
            // img_thumbnail.loadImg(item.thumbnail)
            txtName.text = item.userProfile?.name
            txtReview.text=item.userProfile?.rating?.review
            //  basic_info.text = item.userProfile?.basicInfo
            rating.rating = item.rating
            txtTimeStamp.text = DateUtils.getDurationFromDate(item.userProfile?.rating?.time!!)

            if (item.userId == MentorzApplication.instance?.prefs?.getUserId()) {
                if (!TextUtils.isEmpty(MentorzApplication.instance?.prefs?.getProfilePictureLres()))
                Picasso.with(context).load(MentorzApplication.instance?.prefs?.getProfilePictureLres()).placeholder(R.drawable.default_avatar).resize(200, 200).centerCrop()
                        .into(img_thumbnail)
            } else if (!TextUtils.isEmpty(item.userProfile?.lresId)) {
                if (!TextUtils.isEmpty(item.userProfile?.lresId))
                Picasso.with(context).load(item.userProfile?.lresId).placeholder(R.drawable.default_avatar).resize(200, 200).centerCrop()
                        .into(img_thumbnail)
            }

        }
    }
}