package com.mentorz.adapter

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.RelativeSizeSpan
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.mentorz.R
import com.mentorz.constants.PushType
import com.mentorz.extensions.inflate
import com.mentorz.model.Notification
import com.mentorz.utils.CircleTransform
import com.mentorz.utils.DateUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.notification_item.view.*

/**
 * Created by craterzone on 06/09/17.
 */
class NotificationListAdapter(var notificationList: MutableList<Notification>?, val listener: NotificationListener) : RecyclerView.Adapter<NotificationListAdapter.ViewHolder>() {
    override fun getItemCount(): Int {
        if (notificationList != null) {
            return notificationList!!.size
        } else {
            return 0
        }
    }

    fun setList(notificationList: MutableList<Notification>?) {
        this.notificationList = notificationList
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder!!.bind(notificationList!![position])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(parent!!.inflate(R.layout.notification_item), listener)
    }

    class ViewHolder(itemView: View, val listener: NotificationListener) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("ResourceAsColor")
        fun bind(item: Notification) = with(itemView) {

            val text: SpannableString = SpannableString(item.message);
            text.setSpan(RelativeSizeSpan(1.5f), 0, item.userName!!.toString().trim().length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if(item.isRead){
                unread_notification.visibility = View.GONE
            }else{
                unread_notification.visibility = View.VISIBLE
            }
            rootView.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    item.isRead = true
                    listener.onNotificationClick(item.pushType, item.userId, item.postId, item.userName,adapterPosition)
                }

            })
            txtNotification.text = text
            txtNotificationTime.text = DateUtils.getDurationFromDate(item.timeStamp!!)
            if (item.pushType.equals(PushType.LIKED_POST)) {
                img_thumbnail.scaleType = ImageView.ScaleType.CENTER
                img_thumbnail.setImageResource(R.drawable.selected_like)
            } else if (item.pushType.equals(PushType.COMMENTED_ON_POST)) {
                img_thumbnail.scaleType = ImageView.ScaleType.CENTER
                img_thumbnail.setImageResource(R.drawable.comment)
            } else if (item.pushType.equals(PushType.SHARE)) {
                img_thumbnail.scaleType = ImageView.ScaleType.CENTER
                img_thumbnail.setImageResource(R.drawable.share)
            } else {
                if (!TextUtils.isEmpty(item.profilePicture)) {
                    img_thumbnail.scaleType = ImageView.ScaleType.CENTER_CROP
                    Picasso.with(context).load(item.profilePicture).transform(CircleTransform()).placeholder(R.drawable.default_avatar).resize(200, 200).centerCrop()
                            .into(img_thumbnail)
                }
            }
        }
    }

    interface NotificationListener {
        fun onNotificationClick(pushType: String?, userId: Long?, postId: Long?, userName: String?, adapterPosition: Int) {}

    }

    fun  removeNotificationByUserId(userId: Long) {
        notificationList = notificationList?.filter { it.userId != userId } as MutableList<Notification>

        notifyDataSetChanged()
    }
}