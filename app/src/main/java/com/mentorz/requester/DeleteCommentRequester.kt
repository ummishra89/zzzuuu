package com.mentorz.requester

import com.mentorz.listener.CommentListener
import com.mentorz.model.CommentListItem
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by aMAN GUPTA on 20/7/17.
 */
class DeleteCommentRequester(postId: Long, commentListItem: CommentListItem, private var presenter: Any?, private var listener: CommentListener?) : BaseRequester() {
    private var postId: Long? = postId
    private var commentListItem: CommentListItem? = commentListItem

    fun execute() {
        doAsync {
            //run in background
            val mentorzApiResponse: MentorzApiResponse? = HttpController.deleteComment(postId!!, commentListItem?.commentId!!)
            if (!isSessionExpired(listener!!, mentorzApiResponse!!)) {
                callBack(mentorzApiResponse)
            }
        }
    }

    private fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_OK) {
            listener?.deleteCommentSuccess()

        }
        else if(mentorzApiResponse?.statusCode==0) {
            listener?.onNetworkFail()
        }
        else {
            listener?.deleteCommentFail()
        }
    }


}