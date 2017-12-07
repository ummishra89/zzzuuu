package com.mentorz.retrofit

//import com.mentorz.model.LoginRequest
import android.util.Log
import com.mentorz.MentorzApplication
import com.mentorz.activities.values.ValuesPresenterImpl
import com.mentorz.interest.InterestPresenterImpl
import com.mentorz.match.BeMentorRequest
import com.mentorz.match.MyMentorRequest
import com.mentorz.model.*
import com.mentorz.requester.AddPostRequester
import com.mentorz.requester.CommentRequester
import com.mentorz.requester.FeedbackRequester
import okhttp3.ResponseBody
import retrofit2.Call

/**
 * Created by craterzone on 06/07/17.
 */
object HttpController {
    private val CONTENT_TYPE: String = "application/json"
    private val ACCEPT: String = "application/json"
  //  private val USER_AGENT: String = MentorzApplication.instance?.prefs?.getIMEI()!!
    private val USER_AGENT: String = "Mozilla/5.0 (X11; mandroid) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.91  Safari/537.36" + MentorzApplication.instance!!.prefs!!.getUdId()

    fun register(registrationRequest: RegistrationRequest?): MentorzApiResponse? {
        printModelToJSon(registrationRequest)
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.register(CONTENT_TYPE, ACCEPT, USER_AGENT, registrationRequest)
        return ConnectionUtil.execute(call)
    }

    fun socialRegister(socialRegistrationRequest: SocialRegistrationRequest?): MentorzApiResponse? {
        printModelToJSon(socialRegistrationRequest)
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.socialRegister(CONTENT_TYPE, ACCEPT, USER_AGENT, socialRegistrationRequest)
        return ConnectionUtil.execute(call)
    }

    fun login(loginRequest: LoginRequest?): MentorzApiResponse? {
        printModelToJSon(loginRequest)
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.login(CONTENT_TYPE, ACCEPT, USER_AGENT, loginRequest)
        return ConnectionUtil.execute(call)
    }

    fun socialLogin(socialLoginRequest: SocialLoginRequest?): MentorzApiResponse? {
        printModelToJSon(socialLoginRequest)
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.socialLogin(CONTENT_TYPE, ACCEPT, USER_AGENT, socialLoginRequest)
        return ConnectionUtil.execute(call)
    }

    fun forgotPassword(forgotPasswordRequest: ForgotPasswordRequest?): MentorzApiResponse? {
        printModelToJSon(forgotPasswordRequest)
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.forgotPassword(CONTENT_TYPE, ACCEPT, USER_AGENT, forgotPasswordRequest)
        return ConnectionUtil.execute(call)
    }

    fun resetPassword(resetPasswordRequest: ResetPasswordRequest?): MentorzApiResponse? {
        printModelToJSon(resetPasswordRequest)
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.resetPassword(CONTENT_TYPE, ACCEPT, USER_AGENT, resetPasswordRequest)
        return ConnectionUtil.execute(call)
    }

    fun getDefaultValues(): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.getDefaultValues(CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken())
        return ConnectionUtil.execute(call)
    }

    fun getUserValues(): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.getUserValues(CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(), MentorzApplication.instance?.prefs?.getUserId())
        return ConnectionUtil.execute(call)
    }

    fun getUserProfile(): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.getUserProfile(CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(), MentorzApplication.instance?.prefs?.getUserId())
        return ConnectionUtil.execute(call)
    }

    fun updateProfile(updateProfileRequest: UpdateProfileRequest?): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.updateProfile(CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(), MentorzApplication.instance?.prefs?.getUserId(), updateProfileRequest)
        return ConnectionUtil.execute(call)
    }

    fun getRootInterest(): MentorzApiResponse? {
        Log.d("HttpController", "getBoard:" + MentorzApplication.instance?.prefs?.getAuthToken())
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.getRootInterest(CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(), MentorzApplication.instance?.prefs?.getUserId())
        return ConnectionUtil.execute(call)
    }

    fun getInterestByParent(parentId: Int): MentorzApiResponse? {
        Log.d("HttpController", "token:" + MentorzApplication.instance?.prefs?.getAuthToken())

        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.getInterestByParent(CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(), MentorzApplication.instance?.prefs?.getUserId(), parentId)
        return ConnectionUtil.execute(call)
    }

    fun getInterest(): MentorzApiResponse? {
        Log.d("HttpController", "token:" + MentorzApplication.instance?.prefs?.getAuthToken())

        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.getInterest(MentorzApplication.instance?.prefs?.getUserId(), CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken())
        return ConnectionUtil.execute(call)
    }

    fun getMyMentorList(mentorRequest: MyMentorRequest, offset: Int,limit :Int): MentorzApiResponse? {
        Log.d("HttpController", "token:" + MentorzApplication.instance?.prefs?.getAuthToken())
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.getMyMentorList(MentorzApplication.instance?.prefs?.getUserId(), CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(), mentorRequest, offset,limit)
        return ConnectionUtil.execute(call)
    }
    fun getMenteeList(mentorId: Long, pageNo: Int): MentorzApiResponse? {
        Log.d("HttpController", "token:" + MentorzApplication.instance?.prefs?.getAuthToken())
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.getMenteeList(MentorzApplication.instance?.prefs?.getUserId(), CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(),mentorId, pageNo)
        return ConnectionUtil.execute(call)
    }
    fun getMentorList(mentorId: Long, pageNo: Int): MentorzApiResponse? {
        Log.d("HttpController", "token:" + MentorzApplication.instance?.prefs?.getAuthToken())
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.getMentorList(MentorzApplication.instance?.prefs?.getUserId(), CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(), mentorId,pageNo)
        return ConnectionUtil.execute(call)
    }

    fun beMentor(beMentorRequest: BeMentorRequest): MentorzApiResponse? {
        Log.d("HttpController", "token:" + MentorzApplication.instance?.prefs?.getAuthToken())
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.beMentor(MentorzApplication.instance?.prefs?.getUserId(), CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(), beMentorRequest)
        return ConnectionUtil.execute(call)
    }

    fun getMentorRequests(pageNo: Int): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.getMentorRequests(MentorzApplication.instance?.prefs?.getUserId(), CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(), pageNo)
        return ConnectionUtil.execute(call)
    }

    fun addMentee(menteeId: Long): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.addMentee(MentorzApplication.instance?.prefs?.getUserId(), CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(), menteeId)
        return ConnectionUtil.execute(call)
    }


    fun updateValues(updateValuesRequest: ValuesPresenterImpl.UpdateValuesRequest?): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.updateValues(MentorzApplication.instance?.prefs?.getUserId(), CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(), updateValuesRequest)
        return ConnectionUtil.execute(call)

    }

    fun getMentorExpertise(mentorId: Long): MentorzApiResponse? {
        Log.d("HttpController", "token:" + MentorzApplication.instance?.prefs?.getAuthToken())
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.getMentorExpertise(CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(), MentorzApplication.instance?.prefs?.getUserId(), mentorId)
        return ConnectionUtil.execute(call)
    }

    fun updateInterest(updateInterestRequest: InterestPresenterImpl.UpdateInterestRequest?): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.updateInterest(MentorzApplication.instance?.prefs?.getUserId(), CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(), updateInterestRequest)
        return ConnectionUtil.execute(call)
    }

    fun printModelToJSon(any: Any?) {
        when (any) {
            is LoginRequest -> Log.d("LoginRequest", "LoginRequest:" + MentorzApplication.instance?.gson?.toJson(any))
            is RegistrationRequest -> Log.d("RegistrationRequest", "RegistrationRequest:" + MentorzApplication.instance?.gson?.toJson(any))
        }
    }

    fun getRootExpertise(): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.getRootExpertise(MentorzApplication.instance?.prefs?.getUserId(), CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken())
        return ConnectionUtil.execute(call)
    }

    fun getExpertiseByParentId(parentId: Int): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.getExpertiseByParentId(MentorzApplication.instance?.prefs?.getUserId(), CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(), parentId)
        return ConnectionUtil.execute(call)
    }

    fun getSignedUrl(token: String?): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.getSignedUrl(token, CONTENT_TYPE, ACCEPT, USER_AGENT)
        return ConnectionUtil.execute(call)
    }

    fun getUploadSessionUrl(token: String?, contentType: String?): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.getUploadSessionUrl(contentType!!, ACCEPT, USER_AGENT, token)
        return ConnectionUtil.execute(call)
    }

    fun getUserRating(userId: Long?): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.getUserRating(userId, CONTENT_TYPE, ACCEPT, USER_AGENT)
        return ConnectionUtil.execute(call)
    }

    fun getMentorBio(mentorId: Long?): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.getMentorBio(mentorId, CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken())
        return ConnectionUtil.execute(call)
    }

    fun sendMentorRequest(sendMentorRequest: SendMentorRequest?, mentorId: Long?): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.sendMentorRequest(MentorzApplication.instance?.prefs?.getUserId(), mentorId, CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(), sendMentorRequest)
        return ConnectionUtil.execute(call)
    }

    fun getRequestStatus(mentorId: Long?): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.getRequestStatus(MentorzApplication.instance?.prefs?.getUserId(), mentorId, CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken())
        return ConnectionUtil.execute(call)
    }

    fun getBoard(pageNo: Int): MentorzApiResponse? {
        Log.d("HttpController", "token:" + MentorzApplication.instance?.prefs?.getAuthToken())
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.getBoard(MentorzApplication.instance?.prefs?.getUserId(), CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(), pageNo)
        return ConnectionUtil.execute(call)
    }

    fun abusePost(postId: Long, abuseType: String): MentorzApiResponse? {
        val abuseString = if (abuseType.equals(AbuseType.SPAM, true)) {
            AbuseType.SPAM
        } else {
            AbuseType.INAPPROPRIATE_CONTENT
        }
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.abusePost(MentorzApplication.instance?.prefs?.getUserId(), CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(), postId, abuseString)
        return ConnectionUtil.execute(call)

    }

    fun likePost(postId: Long): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.likePost(MentorzApplication.instance?.prefs?.getUserId(), CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(), postId)
        return ConnectionUtil.execute(call)

    }

    fun viewPost(postId: Long): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.viewPost(MentorzApplication.instance?.prefs?.getUserId(), CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(), postId)
        return ConnectionUtil.execute(call)

    }

    fun sharePost(postId: Long): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.sharePost(MentorzApplication.instance?.prefs?.getUserId(), CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(), postId)
        return ConnectionUtil.execute(call)

    }

    fun deleteComment(postId: Long, commentId: Long): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.deleteComment(MentorzApplication.instance?.prefs?.getUserId(), CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(), postId, commentId)
        return ConnectionUtil.execute(call)

    }

    fun getComment(postId: Long, pageNo: Int): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.getComment(MentorzApplication.instance?.prefs?.getUserId(), CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(), postId, pageNo)
        return ConnectionUtil.execute(call)
    }

    fun getReview(userId: Long, pageNo: Int): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.getReview(userId, CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(), pageNo)
        return ConnectionUtil.execute(call)
    }


    fun postComment(postId: Long, commentRequest: CommentRequester.CommentRequest): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.postComment(MentorzApplication.instance?.prefs?.getUserId(), CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(), postId, commentRequest)
        return ConnectionUtil.execute(call)
    }

    fun getMyPost(pageNo: Int): MentorzApiResponse? {
        Log.d("HttpController", "token:" + MentorzApplication.instance?.prefs?.getAuthToken())
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.getMyPost(MentorzApplication.instance?.prefs?.getUserId(), CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(), pageNo)
        return ConnectionUtil.execute(call)
    }

    fun getFriendPost(friendId: Long, pageNo: Int): MentorzApiResponse? {
        Log.d("HttpController", "token:" + MentorzApplication.instance?.prefs?.getAuthToken())
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.getFriendPost(MentorzApplication.instance?.prefs?.getUserId(), friendId, CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(), pageNo)
        return ConnectionUtil.execute(call)
    }

    fun getFriendProfile(friendId: Long?): MentorzApiResponse? {
        Log.d("HttpController", "token:" + MentorzApplication.instance?.prefs?.getAuthToken())
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.getFriendProfile(CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(), MentorzApplication.instance?.prefs?.getUserId(), friendId)
        return ConnectionUtil.execute(call)
    }

    fun checkFollowing(mentorId: Long?): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.checkFollowing(CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(), MentorzApplication.instance?.prefs?.getUserId(), mentorId)
        return ConnectionUtil.execute(call)
    }

    fun followUser(mentorId: Long): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.followUser(CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(), MentorzApplication.instance?.prefs?.getUserId(), mentorId)
        return ConnectionUtil.execute(call)
    }
    fun unFollowUser(mentorId: Long): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.unFollowUser(CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(), MentorzApplication.instance?.prefs?.getUserId(), mentorId)
        return ConnectionUtil.execute(call)
    }
    fun getFollowers(mentorId: Long, pageNo: Int): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.getFollowers(MentorzApplication.instance?.prefs?.getUserId(),CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(), mentorId, pageNo)
        return ConnectionUtil.execute(call)
    }

    fun getFollowing(mentorId: Long, pageNo: Int): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.getFollowing(MentorzApplication.instance?.prefs?.getUserId(),CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(), mentorId, pageNo)
        return ConnectionUtil.execute(call)
    }

    fun sendFeedback(feedbackRequest: FeedbackRequester.FeedbackRequest): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.sendFeedback(CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(), MentorzApplication.instance?.prefs?.getUserId(), feedbackRequest)
        return ConnectionUtil.execute(call)
    }

    fun logout(): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.logout(CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(), MentorzApplication.instance?.prefs?.getUserId())
        return ConnectionUtil.execute(call)
    }

    fun getProfileImage(userId: Long): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.getProfileImage(CONTENT_TYPE, ACCEPT, USER_AGENT, userId)
        return ConnectionUtil.execute(call)
    }
    fun getPendingRequest(pageNo:Int): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.getPendingRequest(CONTENT_TYPE, ACCEPT, USER_AGENT,MentorzApplication.instance?.prefs?.getAuthToken(),MentorzApplication.instance?.prefs?.getUserId(),pageNo)
        return ConnectionUtil.execute(call)
    }
    fun cancelPendingRequest(menteeId:Long): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.cancelPendingRequest(MentorzApplication.instance?.prefs?.getUserId(),CONTENT_TYPE, ACCEPT, USER_AGENT,MentorzApplication.instance?.prefs?.getAuthToken(),menteeId)
        return ConnectionUtil.execute(call)
    }
    fun rejectPendingRequest(menteeId: Long): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.rejectPendingRequest(MentorzApplication.instance?.prefs?.getUserId(), CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(), menteeId)
        return ConnectionUtil.execute(call)
    }
    fun getBlockedUsers(pageNo:Int): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.getBlockedUsers(CONTENT_TYPE, ACCEPT, USER_AGENT,MentorzApplication.instance?.prefs?.getAuthToken(),MentorzApplication.instance?.prefs?.getUserId(),pageNo)
        return ConnectionUtil.execute(call)
    }
    fun blockUser(mentorId: Long): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.blockUser(MentorzApplication.instance?.prefs?.getUserId(),mentorId,CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken())
        return ConnectionUtil.execute(call)
    }
    fun unBlockUser(mentorId: Long): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.unBlockUser(MentorzApplication.instance?.prefs?.getUserId(),mentorId,CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken())
        return ConnectionUtil.execute(call)
    }
    fun rateUser(mentorId: Long,rating:Rating): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.rateUser(MentorzApplication.instance?.prefs?.getUserId(),mentorId,CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(),rating)
        return ConnectionUtil.execute(call)
    }
    fun addPost(content: AddPostRequester.ContentRequest): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.addPost(MentorzApplication.instance?.prefs?.getUserId(), CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(),content)
        return ConnectionUtil.execute(call)
    }
    fun searchPostByInterest(interestList: MutableList<Int>,pageNo:Int): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.searchPostByInterest(MentorzApplication.instance?.prefs?.getUserId(), CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(),interestList,pageNo)
        return ConnectionUtil.execute(call)
    }

    fun updateFireBasePushToken(deviceInfo: DeviceInfo): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.updateFireBasePushToken(MentorzApplication.instance?.prefs?.getUserId(),CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(), deviceInfo)
        return ConnectionUtil.execute(call)
    }

    fun postByPostId(postId: Long?): MentorzApiResponse? {
        val call: Call<ResponseBody>? = MentorzApplication.instance?.mentorzApi?.postByPostId(MentorzApplication.instance?.prefs?.getUserId(), CONTENT_TYPE, ACCEPT, USER_AGENT, MentorzApplication.instance?.prefs?.getAuthToken(),postId)
        return ConnectionUtil.execute(call)
    }


}