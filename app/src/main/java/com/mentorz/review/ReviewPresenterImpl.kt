package com.mentorz.review

import android.text.TextUtils
import com.mentorz.match.UserListItem
import com.mentorz.model.ProfileImage
import com.mentorz.model.ProfileImageResponse
import com.mentorz.requester.GetReviewRequester
import com.mentorz.requester.ProfileImageRequester
import com.mentorz.requester.RatingRequester
import com.mentorz.requester.SignedUrlRequester
import com.mentorz.retrofit.listeners.ProfileImageListener
import com.mentorz.retrofit.listeners.RatingListener
import com.mentorz.retrofit.listeners.SignedUrlListener
import com.mentorz.uploadfile.FileType

/**
 * Created by umesh on 05/08/17.
 */
class ReviewPresenterImpl(view: ReviewView) : ReviewResponseListener, RatingListener, ProfileImageListener, SignedUrlListener {

    override fun onNetworkFail() {
        view.hideProgress()
        view.networkError()
    }

    override fun signedUrlSuccess(url: String, model: Any?) {
        reviewList?.forEach {
            if (it.userId!! == (model as ProfileImage).userId) {
                it.userProfile?.lresId = url
            }
        }
        view.dataSetChanged()
    }

    override fun signedUrlFail() {

    }

    override fun profileImageSuccess(profileImageResponse: ProfileImageResponse, userId: Long?) {
        if (!TextUtils.isEmpty(profileImageResponse.lresId)) {
            val model = ProfileImage()
            model.userId = userId
            SignedUrlRequester(FileType.FILE, this, model, profileImageResponse.lresId).execute()
        }
    }

    override fun profileImageFail() {
    }

    override fun onSessionExpired() {
        view.onSessionExpired()
    }

    override fun onRatingFail() {

    }

    override fun onRatingSuccess(rating: Float, mentorId: Long) {
        reviewList?.forEach {
            it.rating = rating
        }
        view.onRatingResponseSuccess()
    }

    override fun onReviewResponseSuccess(reviewList: MutableList<UserListItem>) {
        view.hideProgress()
        this.reviewList = reviewList
        reviewList.forEach {
            RatingRequester(it.userId, this).execute()
            ProfileImageRequester(it.userId, this).execute()

        }
        view.setReviewAdapter(reviewList)
    }


    override fun onReviewResponseFail() {
        view.hideProgress()
    }

    override fun reviewNoFound() {
        view.hideProgress()
        view.noReviewFound()

    }

    fun getReviews(userId: Long, pageNo: Int,isRefresh:Boolean) {
        if (pageNo == 0&&!isRefresh) {
            view.showProgress()
        }
        GetReviewRequester(userId, pageNo, this, this).execute()
    }

    var view: ReviewView = view
    var reviewList: MutableList<UserListItem>? = null
}