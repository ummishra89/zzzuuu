package com.mentorz.requester

import com.mentorz.listener.AbusePostListener
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.stories.PostListItem
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by aMAN GUPTA on 20/7/17.
 */
class AbusePostRequester(postListItem: PostListItem, abuseType: String, private var presenter: Any?, private var listener: AbusePostListener?) : BaseRequester() {
    private var postListItem: PostListItem? = postListItem
    private var abuseType: String? = abuseType

    fun execute() {
        doAsync {
            //run in background
            val mentorzApiResponse: MentorzApiResponse? = HttpController.abusePost(postListItem?.postId!!, abuseType!!)
            if (!isSessionExpired(listener!!, mentorzApiResponse!!)) {
                callBack(mentorzApiResponse)
            }
        }
    }

    private fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_NO_CONTENT) {
            listener?.onAbusePostSuccess()
        }
        else if(mentorzApiResponse?.statusCode==0) {
            listener?.onNetworkFail()
        }
        else {
            listener?.onAbusePostFail()
        }
    }


}