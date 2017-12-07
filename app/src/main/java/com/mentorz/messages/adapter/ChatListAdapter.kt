package com.mentorz.messages.adapter

import android.content.Context
import android.net.Uri
import android.support.v4.util.SparseArrayCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.mentorz.MentorzApplication
import com.mentorz.R
import com.mentorz.adapter.BaseAdapter
import com.mentorz.extensions.inflate
import com.mentorz.extensions.loadThumbnailUrl
import com.mentorz.match.adapter.AdapterConstants
import com.mentorz.pubnub.Aps
import com.mentorz.pubnub.PnAPNS
import com.mentorz.pubnub.StreamChatPacket
import com.mentorz.pubnub.StreamMessage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.chat_user_list_item.view.*


class ChatListAdapter( context: Context,senderLres:String?) : BaseAdapter<ChatListAdapter, RecyclerView.ViewHolder>() {
    private var items: MutableList<StreamMessage> = mutableListOf()
    private val MESSAGE_TYPE_SENT = 0
    private val MESSAGE_TYPE_RECEIVE = 1
    private var senderLres =senderLres
    var context =context

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        if ((items[position] as StreamChatPacket).senderId == MentorzApplication.instance?.prefs?.getUserId()){
            return MESSAGE_TYPE_SENT
        }
        return MESSAGE_TYPE_RECEIVE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == MESSAGE_TYPE_SENT){
            return SendMessageViewHolder(parent)
        }
        return ReceiveMessageViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == MESSAGE_TYPE_SENT){
            (holder as SendMessageViewHolder).bind(position, items[position] as StreamChatPacket)
        }else{
            (holder as ReceiveMessageViewHolder).bind(position, items[position] as StreamChatPacket)
        }

    }

//    fun addMessage(news: MutableList<StreamMessage>) {
//        val initPosition = items.size - 1
//        items.addAll(news)
//        notifyItemRangeChanged(initPosition, items.size   /* plus loading item */)
//    }

    fun addMessage(message: StreamMessage) {
        items.add(message)
        notifyItemInserted(items.size-1)
    }

    fun addImageMessage(message: StreamMessage) {
        items.add(message)
        notifyItemInserted(items.size-1)
    }

    fun addSendMessage(message: StreamMessage) {
        items.add(message)
        notifyItemInserted(items.size-1)
    }

    fun addReceiveMessage(message: StreamMessage) {
        items.add(message)
        notifyItemInserted(items.size-1)
    }

    fun notifyList(list :MutableList<StreamChatPacket>) {
        items.clear()
        items.addAll(list)
        notifyItemInserted(items.size-1)
    }

    override fun getRows(): List<Any?>? {
        return items
                .map { it }
    }

//    class ChatListDelegateAdapter(context: Context, adapter: ChatListAdapter)  {
//        interface onViewSelectedListener {
//
//        }
//        var adapter = adapter
//        var context =context
//
//         fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
//            return MessageViewHolder(parent)
//        }
//
//         fun onBindViewHolder(position: Int, holder: RecyclerView.ViewHolder, item: StreamMessage) {
//            holder as MessageViewHolder
//            holder.bind(position, item as StreamChatPacket)
//
//        }
//    }

    inner class SendMessageViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            parent.inflate(R.layout.chat_view_item)) {

        fun bind(position: Int, item: StreamChatPacket) = with(itemView) {
            if(item.pn_gcm?.data?.uri!=null){
                txtName.visibility = View.GONE
                iv_image.visibility = View.VISIBLE
                iv_image.setImageURI(Uri.parse(item.pn_gcm?.data?.uri))
            }else{
                txtName.visibility = View.VISIBLE
                iv_image.visibility = View.GONE
                txtName.text=item.body
            }

            img_thumbnail.loadThumbnailUrl(context,MentorzApplication.instance?.prefs?.getProfilePictureLres())
        }
    }
    inner class ReceiveMessageViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            parent.inflate(R.layout.chat_view_item_receive)) {

        fun bind(position: Int, item: StreamChatPacket) = with(itemView) {
            txtName.text=item.body
          if(item.pn_apns?.aps?.senderLres!=null){
              img_thumbnail.loadThumbnailUrl(context, item.pn_apns?.aps?.senderLres)
          }else{
              img_thumbnail.loadThumbnailUrl(context,senderLres)
          }

        }
    }

}


