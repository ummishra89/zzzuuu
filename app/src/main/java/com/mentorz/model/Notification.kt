package com.mentorz.model

/**
 * Created by craterzone on 06/09/17.
 */
data class Notification(var message: String?, var timeStamp: Long?, var userId: Long?, var postId: Long?, var pushType: String?,var userName: String?){
    var profilePicture: String? = null
    var isRead: Boolean = false
}
