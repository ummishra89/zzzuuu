package com.mentorz.userdetails.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.ViewGroup
import com.mentorz.R
import com.mentorz.activities.MentorProfileActivity
import com.mentorz.extensions.inflate
import com.mentorz.extensions.loadThumbnailUrl
import com.mentorz.match.UserListItem
import com.mentorz.match.adapter.ViewType
import com.mentorz.match.adapter.ViewTypeDelegateAdapter
import com.mentorz.model.Rating
import kotlinx.android.synthetic.main.mentee_list_item.view.*


class MenteeDelegateAdapter(context: Context, adapter: MenteeListAdapter, val viewActions: onViewSelectedListener) : ViewTypeDelegateAdapter {

    interface onViewSelectedListener {

    }
    var adapter = adapter
    var context =context

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return NewsViewHolder(parent)
    }

    override fun onBindViewHolder(position: Int, holder: RecyclerView.ViewHolder, item: ViewType) {
        holder as NewsViewHolder
        holder.bind(position, item as UserListItem)

    }

    inner class NewsViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            parent.inflate(R.layout.mentee_list_item)) {

        var item: UserListItem? = null

        fun bind(position: Int, item: UserListItem) = with(itemView) {
            txtName.text=item.userProfile?.name
            txtBasicInfo.text= item.userProfile?.basicInfo
            rating.rating=item.rating
            mainContent.setOnClickListener {
                val intent = Intent(context, MentorProfileActivity::class.java)
                intent.putExtra("user_id", item.userId)
                val rating = Rating()
                rating.rating = item.rating
                item.userProfile?.rating = rating
                Log.d("adapter", "rating:" + item.userProfile?.rating?.rating)
                intent.putExtra("profile_data", item.userProfile)
                context.startActivity(intent)
            }
            if (item.userProfile!!.lresId != null) {
                img_thumbnail.loadThumbnailUrl(context, item.userProfile?.lresId)
            }


        }
    }
}