package com.mentorz.listener


interface UnFollowListener : SessionExpiredListener, NetworkErrorListener {
    fun checkUnFollowingSuccess(model:Any?,isFollowing:Boolean)
    fun onUnFollowSuccess(model:Any?)
    fun onUnFollowFail()
    fun checkUnFollowingFail()

}