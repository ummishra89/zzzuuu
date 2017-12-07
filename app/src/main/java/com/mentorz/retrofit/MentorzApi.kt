package com.mentorz.retrofit

//import com.mentorz.model.LoginRequest
import com.google.gson.JsonObject
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
import retrofit2.http.*
import okhttp3.MultipartBody
import retrofit2.http.POST
import retrofit2.http.Multipart



/**
 * Created by craterzone on 06/07/17.
 */
interface MentorzApi {
    @PUT("api/v1/user")
    fun register(@Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Body registrationRequest: RegistrationRequest?)
            : Call<ResponseBody>

    @PUT("api/v1/user/social")
    fun socialRegister(@Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Body socialRegistrationRequest: SocialRegistrationRequest?)
            : Call<ResponseBody>

    @POST("api/v1/user/login")
    fun login(@Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Body loginRequest: LoginRequest?)
            : Call<ResponseBody>

    @POST("api/v1/user/sociallogin")
    fun socialLogin(@Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Body socialLoginRequest: SocialLoginRequest?)
            : Call<ResponseBody>

    @POST("api/v1/user/forgot/password")
    fun forgotPassword(@Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Body forgotPasswordRequest: ForgotPasswordRequest?)
            : Call<ResponseBody>

    @POST("api/v1/user/reset/password")
    fun resetPassword(@Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Body resetPasswordRequest: ResetPasswordRequest?)
            : Call<ResponseBody>

    @GET("api/v1/user/values")
    fun getDefaultValues(@Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?)
            : Call<ResponseBody>

    @GET("api/v1/user/{user_id}/values")
    fun getUserValues(@Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?, @Path("user_id") userId: Long?)
            : Call<ResponseBody>

    @GET("api/v1/user/{user_id}/interests?pageNo=0")
    fun getRootInterest(@Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?, @Path("user_id") userId: Long?)
            : Call<ResponseBody>

    @GET("api/v1/user/{user_id}/interests")
    fun getInterestByParent(@Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?, @Path("user_id") userId: Long?, @Query("parentId") parentId: Int?)
            : Call<ResponseBody>


    @GET("api/v1/user/{user_id}/interest")
    fun getInterest(@Path("user_id") userId: Long?, @Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?)
            : Call<ResponseBody>

    @GET("api/v1/{user_id}/mentor/{user_id}/expertises")
    fun getRootExpertise(@Path("user_id") userId: Long?, @Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?)
            : Call<ResponseBody>

    //http://{{host}}/mentorz/api/v1/553/mentor/find?offset=0&limit=20
    @POST("api/v1/{user_id}/mentor/find")
    fun getMyMentorList(@Path("user_id") userId: Long?, @Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?, @Body mentorRequest: MyMentorRequest, @Query("offset") offset: Int?,@Query("limit") limit: Int?)
            : Call<ResponseBody>

    @POST("api/v1/{user_id}/mentor")
    fun beMentor(@Path("user_id") userId: Long?, @Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?, @Body beMentorRequest: BeMentorRequest)
            : Call<ResponseBody>

    @GET("api/v1/{user_id}/mentor/mentee/requests")
    fun getMentorRequests(@Path("user_id") userId: Long?, @Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?, @Query("pageNo") pageNo: Int?)
            : Call<ResponseBody>


    @POST("api/v1/{user_id}/mentor/accept/request/{mentee_id}")
    fun addMentee(@Path("user_id") userId: Long?, @Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?, @Path("mentee_id") menteeId: Long?)
            : Call<ResponseBody>

    @POST("api/v1/user/{user_id}/values")
    fun updateValues(@Path("user_id") userId: Long?, @Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?, @Body updateValuesRequest: ValuesPresenterImpl.UpdateValuesRequest?)
            : Call<ResponseBody>

    @GET("api/v1/user/{user_id}/profile")
    fun getUserProfile(@Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?, @Path("user_id") userId: Long?)
            : Call<ResponseBody>

    @POST("api/v1/user/{user_id}/update/profile")
    fun updateProfile(@Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?, @Path("user_id") userId: Long?, @Body updateProfileRequest: UpdateProfileRequest?)
            : Call<ResponseBody>

    @GET("api/v1/{user_id}/mentor/{user_id}/expertises")
    fun getExpertiseByParentId(@Path("user_id") userId: Long?, @Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?, @Query("parentId") parentId: Int?)
            : Call<ResponseBody>

    @GET("api/v1/{user_id}/mentor/{mentor_id}/expertise")
    fun getMentorExpertise(@Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?, @Path("user_id") userId: Long?, @Path("mentor_id") mentorId: Long?)
            : Call<ResponseBody>

    @POST("api/v1/user/{user_id}/interests")
    fun updateInterest(@Path("user_id") userId: Long?, @Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?, @Body updateInterestRequest: InterestPresenterImpl.UpdateInterestRequest?)
            : Call<ResponseBody>

    @GET("api/v1/user/signed/sessionuri/object/{token}")
    fun getUploadSessionUrl(@Header("contentType") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Path("token") token: String?)
            : Call<ResponseBody>

    @GET("api/v1/user/signed/geturl/object/{token}")
    fun getSignedUrl(@Path("token") token: String?, @Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String)
            : Call<ResponseBody>

    @GET("api/v1/user/{user_id}/rating")
    fun getUserRating(@Path("user_id") userId: Long?, @Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String)
            : Call<ResponseBody>

    @GET("api/v1/user/{user_id}/board")
    fun getBoard(@Path("user_id") userId: Long?, @Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?, @Query("pageNo") pageNo: Int?)
            : Call<ResponseBody>

    @POST("api/v1/{user_id}/post/{post_id}/abuse")
    fun abusePost(@Path("user_id") userId: Long?, @Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?, @Path("post_id") postId: Long?, @Query("abuse_type") abuseType: String?)
            : Call<ResponseBody>

    @POST("api/v1/{user_id}/post/{post_id}/view")
    fun viewPost(@Path("user_id") userId: Long?, @Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?, @Path("post_id") postId: Long?)
            : Call<ResponseBody>

    @POST("api/v1/{user_id}/post/{post_id}/like")
    fun likePost(@Path("user_id") userId: Long?, @Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?, @Path("post_id") postId: Long?)
            : Call<ResponseBody>

    @POST("api/v1/{user_id}/post/{post_id}/share")
    fun sharePost(@Path("user_id") userId: Long?, @Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?, @Path("post_id") postId: Long?)
            : Call<ResponseBody>

    @GET("api/v1/{user_id}/post/{post_id}/comments")
    fun getComment(@Path("user_id") userId: Long?, @Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?, @Path("post_id") postId: Long?, @Query("pageNo") pageNo: Int?)
            : Call<ResponseBody>

    @GET("api/v1/user/{user_id}/review")
    fun getReview(@Path("user_id") userId: Long?, @Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?, @Query("pageNo") pageNo: Int?)
            : Call<ResponseBody>

    @PUT("api/v1/{user_id}/post/{post_id}/comment")
    fun postComment(@Path("user_id") userId: Long?, @Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?, @Path("post_id") postId: Long?, @Body commentRequest: CommentRequester.CommentRequest)
            : Call<ResponseBody>

    @DELETE("api/v1/{user_id}/post/{post_id}/comment/{comment_id}")
    fun deleteComment(@Path("user_id") userId: Long?, @Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?, @Path("post_id") postId: Long?, @Path("comment_id") commentId: Long?)
            : Call<ResponseBody>

    @GET("api/v1/{mentor_id}/mentor/bio")
    fun getMentorBio(@Path("mentor_id") mentorId: Long?, @Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?)
            : Call<ResponseBody>

    @POST("api/v1/user/{user_id}/send/request/mentor/{mentor_id}")
    fun sendMentorRequest(@Path("user_id") userId: Long?, @Path("mentor_id") mentorId: Long?, @Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?, @Body sendMentorRequest: SendMentorRequest?)
            : Call<ResponseBody>

    @GET("api/v1/{user_id}/mentor/{mentor_id}/request/status")
    fun getRequestStatus(@Path("user_id") userId: Long?, @Path("mentor_id") mentorId: Long?, @Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?)
            : Call<ResponseBody>

    @GET("api/v1/{user_id}/post")
    fun getMyPost(@Path("user_id") userId: Long?, @Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?, @Query("pageNo") pageNo: Int?)
            : Call<ResponseBody>

    @GET("api/v1/{user_id}/post/friend/{friend_id}")
    fun getFriendPost(@Path("user_id") userId: Long?, @Path("friend_id") friendId: Long?, @Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?, @Query("pageNo") pageNo: Int?)
            : Call<ResponseBody>

    @GET("api/v1/user/{user_id}/friend/{friend_id}/profile")
    fun getFriendProfile(@Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?, @Path("user_id") userId: Long?, @Path("friend_id") friendId: Long?)
            : Call<ResponseBody>

    @GET("api/v1/user/{user_id}/following/{mentor_id}")
    fun checkFollowing(@Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?, @Path("user_id") userId: Long?, @Path("mentor_id") mentorId: Long?)
            : Call<ResponseBody>


    @POST("api/v1/user/{user_id}/follow/{mentor_id}")
    fun followUser(@Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?, @Path("user_id") userId: Long?, @Path("mentor_id") mentorId: Long?)
            : Call<ResponseBody>


    @POST("api/v1/user/{user_id}/unfollow/{mentor_id}")
    fun unFollowUser(@Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?, @Path("user_id") userId: Long?, @Path("mentor_id") mentorId: Long?)
            : Call<ResponseBody>


    @GET("api/v1/user/followers/{user_id}")
    fun getFollowers(@Path("user_id") userId: Long?,@Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?, @Query("friend_id") friendId: Long?, @Query("pageNo") pageNo: Int?)
            : Call<ResponseBody>


    @GET("api/v1/user/following/{user_id}")
    fun getFollowing(@Path("user_id") userId: Long?,@Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?, @Query("friend_id") friendId: Long?, @Query("pageNo") pageNo: Int?)
            : Call<ResponseBody>
    @GET("api/v1/user/{user_id}/my/mentee")
    fun getMenteeList(@Path("user_id") userId: Long?, @Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?,  @Query("friend_id") friendId: Long?,@Query("pageNo") pageNo: Int?)
            : Call<ResponseBody>
    @GET("api/v1/user/{user_id}/my/mentor")
    fun getMentorList(@Path("user_id") userId: Long?, @Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?,@Query("friend_id") friendId: Long?, @Query("pageNo") pageNo: Int?)
            : Call<ResponseBody>


    @POST("api/v1/user/{user_id}/feedback")
    fun sendFeedback(@Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?, @Path("user_id") userId: Long?, @Body feedbackRequest: FeedbackRequester.FeedbackRequest)
            : Call<ResponseBody>

    @DELETE("api/v1/user/{user_id}/logout")
    fun logout(@Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?, @Path("user_id") userId: Long?)
            : Call<ResponseBody>


    @GET("api/v1/user/{user_id}/profile/image")
    fun getProfileImage(@Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Path("user_id") userId: Long?)
            : Call<ResponseBody>


    @GET("api/v1/{user_id}/mentor/pending/requests")
    fun getPendingRequest(@Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String,@Header("oauth-token") authToken: String?, @Path("user_id") userId: Long?,@Query("pageNo") pageNo: Int?)
            : Call<ResponseBody>

    @DELETE("api/v1/{user_id}/mentor/delete/request/{mentee_id}")
    fun rejectPendingRequest(@Path("user_id") userId: Long?, @Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?, @Path("mentee_id") menteeId: Long?)
            : Call<ResponseBody>
    @DELETE("api/v1/{user_id}/mentor/cancel/request/{mentee_id}")
    fun cancelPendingRequest(@Path("user_id") userId: Long?, @Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?, @Path("mentee_id") menteeId: Long?)
            : Call<ResponseBody>


    @GET("api/v1/user/{user_id}/blocked/users")
    fun getBlockedUsers(@Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String,@Header("oauth-token") authToken: String?, @Path("user_id") userId: Long?,@Query("pageNo") pageNo: Int?)
            : Call<ResponseBody>

    @POST("api/v1/user/{user_id}/block/{mentor_id}")
    fun blockUser(@Path("user_id") userId: Long?, @Path("mentor_id") mentorId: Long?, @Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?)
            : Call<ResponseBody>
    @POST("api/v1/user/{user_id}/unblock/{mentor_id}")
    fun unBlockUser(@Path("user_id") userId: Long?, @Path("mentor_id") mentorId: Long?, @Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?)
            : Call<ResponseBody>

    @POST("api/v1/user/{user_id}/rate/{mentor_id}")
    fun rateUser(@Path("user_id") userId: Long?, @Path("mentor_id") mentorId: Long?, @Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?,@Body rating:Rating?)
            : Call<ResponseBody>

    @PUT("api/v1/{user_id}/post")
    fun addPost(@Path("user_id") userId: Long?, @Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?, @Body content: AddPostRequester.ContentRequest?)
            : Call<ResponseBody>
    @GET("api/v1/{user_id}/post/search")
    fun searchPostByInterest(@Path("user_id") userId: Long?, @Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?, @Query("interest") interest: MutableList<Int>?,@Query("pageNo") pageNo: Int?)
            : Call<ResponseBody>

    @POST("api/v1/user/{user_id}/update/device")
    fun updateFireBasePushToken(@Path("user_id") userId: Long?, @Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?,@Body deviceInfo:DeviceInfo?)
            : Call<ResponseBody>

    @GET("api/v1/{user_id}/post/{post_id}")
    fun postByPostId(@Path("user_id") userId: Long?, @Header("Content-Type") contentType: String, @Header("Accept") accept: String, @Header("user-agent") userAgent: String, @Header("oauth-token") authToken: String?,@Path("post_id")postId: Long?)
            : Call<ResponseBody>


}