package com.mentorz.requester

import com.mentorz.MentorzApplication
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.retrofit.listeners.PostByPostIdListener
import com.mentorz.stories.PostListItem
import com.mentorz.stories.PostResponse
import com.mentorz.stories.PostResponseListener
import com.mentorz.utils.EncodingDecoding
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by craterzone on 05/09/17.
 */
class PostByPostIdRequester (private var postId: Long?, private var presenter: Any?, private var listener: PostByPostIdListener?) : BaseRequester() {
    fun execute() {
        doAsync {
            //run in background
            val mentorzApiResponse: MentorzApiResponse? = HttpController.postByPostId(postId)
            if (!isSessionExpired(listener!!, mentorzApiResponse!!)) {
                callBack(mentorzApiResponse)
            }
        }
    }

    private fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        var mentorResponse: PostListItem? = null
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_OK) {
            mentorResponse = MentorzApplication.instance?.gson?.fromJson<PostListItem>(EncodingDecoding.decodeString(mentorzApiResponse.responseBody as String), PostListItem::class.java)
            if (mentorResponse != null) {
                listener?.onPostByPostIdSuccess(mentorResponse)
            }
            return
        } else if(mentorzApiResponse?.statusCode==0) {
            listener?.onNetworkFail()
        }
        else {
            listener?.onPostByPostIdFail()
        }
    }


}