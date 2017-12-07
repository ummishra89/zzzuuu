package com.mentorz.match.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

interface ViewTypeDelegateAdapter {

    fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder

    fun onBindViewHolder(position: Int, holder: RecyclerView.ViewHolder, item: ViewType)
}