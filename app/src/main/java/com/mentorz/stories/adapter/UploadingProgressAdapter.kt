package com.mentorz.stories.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.mentorz.R
import com.mentorz.extensions.inflate
import com.mentorz.match.adapter.ViewType
import com.mentorz.match.adapter.ViewTypeDelegateAdapter
import com.mentorz.model.UploadItem
import com.mentorz.uploadfile.FileUploadRequester
import kotlinx.android.synthetic.main.uploading_post_layout.view.*

class UploadingProgressAdapter(adapter: PostListAdapter, val viewActions: onViewSelectedListener?) : ViewTypeDelegateAdapter {

    interface onViewSelectedListener {

        fun onCancelUpload()
    }
    var adapter = adapter

    override fun onCreateViewHolder(parent: ViewGroup) = LoadingViewHolder(parent)


    override fun onBindViewHolder(position: Int, holder: RecyclerView.ViewHolder, item: ViewType) {
        holder as LoadingViewHolder
        holder.bind(position,item as UploadItem)
    }

       inner class LoadingViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            parent.inflate(R.layout.uploading_post_layout)) {
         fun bind(position: Int, item: UploadItem) = with(itemView) {
           //  progress.progress=item.progress.toInt()
           //  progress.progress = item.progress.toFloat()
           //  txtProgress.text =""
           //  txtProgress.text="${progress.progress}%"
             progress.visibility = View.VISIBLE
             cancel.setOnClickListener {
                 viewActions?.onCancelUpload()


             }

         }
   }


}