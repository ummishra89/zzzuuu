package com.mentorz.extensions

import android.content.Context
import android.text.TextUtils
import android.widget.ImageView
import com.mentorz.R
import com.squareup.picasso.Picasso

/**
 * Created by umesh on 04/08/17.
 */

fun ImageView.loadThumbnailUrl(context: Context, url: String?) {
    if(!TextUtils.isEmpty(url)) {
        Picasso.with(context).load(url).resize(200, 200).centerCrop().placeholder(R.drawable.default_avatar).into(this)
    }else{
        this.setImageResource(R.drawable.default_avatar)
    }

}
fun ImageView.loadPostUrl(context: Context, url: String?) {
    if(!TextUtils.isEmpty(url)) {
        Picasso.with(context).load(url).into(this)
    }

}
