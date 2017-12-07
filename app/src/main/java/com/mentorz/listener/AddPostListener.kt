package com.mentorz.listener

/**
 * Created by umesh on 30/07/17.
 */
interface AddPostListener : SessionExpiredListener,NetworkErrorListener {
    fun onAddPostSuccess()
    {

    }
    fun onAddPostFail(){

    }

    override fun onNetworkFail() {

    }

}