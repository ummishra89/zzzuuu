package com.mentorz.listener

import com.mentorz.model.CommentListItem
import com.mentorz.model.CommentResponse

/**
 * Created by umesh on 30/07/17.
 */
interface CommentListener : SessionExpiredListener ,NetworkErrorListener{
    fun commentOnPostSuccess(commentListItem: CommentListItem)
    fun commentOnPostFail()
    fun commentResponseSuccess(commentResponse: CommentResponse)
    fun noCommentFound()
    fun commentResponseFail()
    fun deleteCommentSuccess()
    fun deleteCommentFail()
}