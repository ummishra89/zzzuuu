package com.mentorz.match.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.mentorz.R
import com.mentorz.extensions.inflate

class LoadingDelegateAdapter : ViewTypeDelegateAdapter {

    override fun onCreateViewHolder(parent: ViewGroup) = LoadingViewHolder(parent)

    override fun onBindViewHolder(position: Int, holder: RecyclerView.ViewHolder, item: ViewType) {
    }

    class LoadingViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            parent.inflate(R.layout.item_loading)) {
    }
}