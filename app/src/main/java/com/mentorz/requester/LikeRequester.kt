package com.mentorz.requester

import com.mentorz.MentorzApplication
import com.mentorz.listener.LikePostListener
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.stories.PostListItem
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by aMAN GUPTA on 20/7/17.
 */
class LikeRequester(postListItem: PostListItem, private var presenter: Any?, private var listener: LikePostListener?) : BaseRequester() {
    private var postListItem: PostListItem? = postListItem

    fun execute() {
        doAsync {
            //run in background
            val mentorzApiResponse: MentorzApiResponse? = HttpController.likePost(postListItem?.postId!!)
            if (!isSessionExpired(listener!!, mentorzApiResponse!!)) {
                callBack(mentorzApiResponse)
            }
        }
    }

    private fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_NO_CONTENT) {
            listener?.onLikePostSuccess(postListItem!!)
        }
        else if(mentorzApiResponse?.statusCode==0) {
            this.postListItem?.likeCount = this.postListItem?.likeCount!!.minus(1)
            this.postListItem?.liked = false
            listener?.onNetworkFail()
        } else if (mentorzApiResponse?.statusCode== HttpURLConnection.HTTP_PRECON_FAILED){
            MentorzApplication.instance?.blockUser(postListItem?.userId)
        }
        else {
            this.postListItem?.likeCount = this.postListItem?.likeCount!!.minus(1)
            this.postListItem?.liked = false
            listener?.onLikePostFail()
        }
    }


}