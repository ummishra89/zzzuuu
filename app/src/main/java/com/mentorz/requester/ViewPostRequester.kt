package com.mentorz.requester

import com.mentorz.MentorzApplication
import com.mentorz.listener.ViewPostListener
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.stories.PostListItem
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by aMAN GUPTA on 20/7/17.
 */
class ViewPostRequester(postListItem: PostListItem, private var presenter: Any?, private var listener: ViewPostListener?) : BaseRequester() {
    private var postListItem: PostListItem? = postListItem

    fun execute() {
        doAsync {
            //run in background
            val mentorzApiResponse: MentorzApiResponse? = HttpController.viewPost(postListItem!!.postId!!)
            if (!isSessionExpired(listener!!, mentorzApiResponse!!)) {
                callBack(mentorzApiResponse)
            }
        }
    }

    private fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_NO_CONTENT) {
            postListItem!!.viewCount = postListItem!!.viewCount!!.plus(1)
            listener?.onViewPostSuccess(postListItem!!)
            return
        }else if(mentorzApiResponse?.statusCode==HttpURLConnection.HTTP_PRECON_FAILED) {
            MentorzApplication.instance?.blockUser(postListItem?.userId)
        }
        else if(mentorzApiResponse?.statusCode==0) {
            listener?.onNetworkFail()
        }
        else{
            listener?.onViewPostFail()
        }
    }


}