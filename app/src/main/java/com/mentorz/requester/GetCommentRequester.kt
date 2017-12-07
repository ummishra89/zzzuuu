package com.mentorz.requester

import com.mentorz.MentorzApplication
import com.mentorz.listener.CommentListener
import com.mentorz.model.CommentResponse
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.utils.EncodingDecoding
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by aMAN GUPTA on 20/7/17.
 */
class GetCommentRequester(val userId : Long, pageNo: Int, postId: Long, private var presenter: Any?, private var listener: CommentListener?) : BaseRequester() {
    private var pageNo: Int? = pageNo
    private var postId: Long? = postId

    fun execute() {
        doAsync {
            //run in background
            val mentorzApiResponse: MentorzApiResponse? = HttpController.getComment(postId!!, pageNo!!)
            if (!isSessionExpired(listener!!, mentorzApiResponse!!)) {
                callBack(mentorzApiResponse)
            }
        }
    }

    private fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        var commentResponse: CommentResponse? = null
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_OK) {
            commentResponse = MentorzApplication.instance?.gson?.fromJson<CommentResponse>(EncodingDecoding.decodeString(mentorzApiResponse.responseBody as String), CommentResponse::class.java)
            if (commentResponse != null) {
                listener?.commentResponseSuccess(commentResponse)
            }

        } else if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_NO_CONTENT) {
            listener?.noCommentFound()
        } else if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_PRECON_FAILED) {
            MentorzApplication.instance?.blockUser(userId)
        }
        else if(mentorzApiResponse?.statusCode==0) {
            listener?.onNetworkFail()
        }
        else {
            listener?.commentResponseFail()
        }
    }


}