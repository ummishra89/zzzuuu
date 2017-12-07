package com.mentorz.listener

/**
 * Created by umesh on 04/08/17.
 */
interface FollowListener : SessionExpiredListener ,NetworkErrorListener{
    fun checkFollowingSuccess(model:Any?,isFollowing:Boolean)
    fun onFollowSuccess(model:Any?)
    fun onFollowFail()
    fun checkFollowingFail()

}