package com.mentorz.requester

import com.google.gson.annotations.SerializedName
import com.mentorz.MentorzApplication
import com.mentorz.listener.CommentListener
import com.mentorz.model.CommentListItem
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.utils.EncodingDecoding
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by aMAN GUPTA on 20/7/17.
 */
class CommentRequester(val userId : Long?,comment: String, postId: Long, private var presenter: Any?, private var listener: CommentListener?) : BaseRequester() {
    private var postId: Long? = postId
    private var comment: String? = comment

    fun execute() {
        doAsync {
            //run in background
            comment = EncodingDecoding.encodeString(comment!!)
            val mentorzApiResponse: MentorzApiResponse? = HttpController.postComment(postId!!, CommentRequest(comment))
            if (!isSessionExpired(listener!!, mentorzApiResponse!!)) {
                callBack(mentorzApiResponse)
            }
        }
    }

    private fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        var commentListItem: CommentListItem? = null
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_OK) {
            commentListItem = MentorzApplication.instance?.gson?.fromJson<CommentListItem>(EncodingDecoding.decodeString(mentorzApiResponse.responseBody as String), CommentListItem::class.java)
            if (commentListItem != null) {
                listener?.commentOnPostSuccess(commentListItem)
            } else {
                listener?.commentOnPostFail()

            }
        } else if(mentorzApiResponse?.statusCode==HttpURLConnection.HTTP_PRECON_FAILED){
            MentorzApplication.instance?.blockUser(userId)
        }
        else if(mentorzApiResponse?.statusCode==0) {
            listener?.onNetworkFail()
        }
        else {
            listener?.commentOnPostFail()
        }
    }

    inner class CommentRequest(@field:SerializedName("comment")
                               var comment: String? = null)


}