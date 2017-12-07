package com.mentorz.match


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mentorz.MentorzApplication
import com.mentorz.R
import com.mentorz.activities.EditProfileActivity
import com.mentorz.activities.authentication.AuthenticationActivity
import com.mentorz.expertise.ExpertiseActivity
import com.mentorz.expertise.ExpertiseItem
import com.mentorz.expertise.ExpertisePresenterImpl
import com.mentorz.expertise.ExpertiseView
import com.mentorz.extensions.hideProgressBar
import com.mentorz.extensions.showProgressBar
import com.mentorz.extensions.showSnackBar
import com.mentorz.fragments.BaseFragment
import com.mentorz.utils.Constant
import com.mentorz.utils.DialogUtils
import com.mentorz.utils.Global
import com.mentorz.utils.Prefs
import kotlinx.android.synthetic.main.fragment_become_mentor.*


/**
 * A simple [Fragment] subclass.
 */
class BeMentorFragment : BaseFragment(), MatchView, View.OnClickListener, ExpertiseView {
    val EXPERTISE_REQUEST_CODE = 100
    var EDIT_PROFILE_REQUEST_CODE = 101


    override fun networkError() {
        super.networkError()
    }
    override fun onSessionExpired() {
        activity?.runOnUiThread {
            DialogUtils.
                    showDialog(activity, "", getString(R.string.session_expired_message), getString(R.string.ok), View.OnClickListener {
                        DialogUtils.dismiss()
                        startActivity(Intent(activity, AuthenticationActivity::class.java))
                        activity.finish()
                    })
        }
    }

    override fun onMentorExpertiseSuccess() {
        activity?.runOnUiThread {
            setExpertise()
        }
    }

    override fun onMentorExpertiseFail() {

    }

    override fun onMentorExpertiseNotFound() {
        activity?.runOnUiThread {
            btnSubmit?.text = getString(R.string.be_a_mentor)
        }

    }

    override fun onMentorCreated() {
        activity?.runOnUiThread {
            DialogUtils.
                    showDialog(activity, "Alert", getString(R.string.you_can_receive_membership_request), getString(R.string.ok), View.OnClickListener {
                        DialogUtils.dismiss()
                    })
//            activity.showSnackBar(rootView, getString(R.string.you_can_receive_membership_request))
            btnSubmit.text = getString(R.string.update)

        }
    }

    override fun onBeMentorSuccess() {
        activity?.runOnUiThread {
            DialogUtils.
                    showDialog(activity, getString(R.string.congratulation), getString(R.string.mentorship_detail_updated), getString(R.string.ok), View.OnClickListener {
                        DialogUtils.dismiss()
                    })
//            activity.showSnackBar(rootView, getString(R.string.updated_successfully))

            var intent = Intent(Constant.UPDATE_PROFILE)
            activity?.sendBroadcast(intent)
        }
    }

    override fun onBeMentorFail() {
        activity?.runOnUiThread {
            DialogUtils.
                    showDialog(activity, "Alert", getString(R.string.something_went_wrong_please_try_later), getString(R.string.ok), View.OnClickListener {
                        DialogUtils.dismiss()
                    })
//            activity.showSnackBar(rootView, getString(R.string.something_went_wrong_please_try_later))
        }
    }

    override fun showProgress() {
        activity?.runOnUiThread {
            activity.showProgressBar(progressBar)
        }
    }

    override fun hideProgress() {
        activity?.runOnUiThread {
            activity.hideProgressBar(progressBar)
        }
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            lExpertise.id -> {
                Global.tempExpertiseMap = mutableMapOf()
                Global.defaultExpertiseMap!!.forEach {
                    val list = mutableListOf<ExpertiseItem>()
                    it.value.mapTo(list) { it.copy() }
                    Global.tempExpertiseMap!!.put(it.key, list)
                }
                startActivityForResult(Intent(activity, ExpertiseActivity::class.java), EXPERTISE_REQUEST_CODE)
            }
            lEditProfile.id -> {
                startActivityForResult(Intent(activity, EditProfileActivity::class.java), EDIT_PROFILE_REQUEST_CODE)
            }

            btnSubmit.id -> {
                if (TextUtils.isEmpty(MentorzApplication.instance?.prefs?.getString(Prefs.Key.OCCUPATION, ""))) {
                    DialogUtils.
                            showDialog(activity, "Alert", getString(R.string.please_enter_your_titile_or_occupation), getString(R.string.ok), View.OnClickListener {
                                DialogUtils.dismiss()
                            })
//                    activity.showSnackBar(rootView, getString(R.string.please_enter_your_titile_or_occupation))
                    return

                } else if (TextUtils.isEmpty(MentorzApplication.instance?.prefs?.getString(Prefs.Key.ORGANIZATION, ""))) {
                    DialogUtils.
                            showDialog(activity, "Alert", getString(R.string.please_enter_the_orgination), getString(R.string.ok), View.OnClickListener {
                                DialogUtils.dismiss()
                            })
//                    activity.showSnackBar(rootView, getString(R.string.please_enter_the_orgination))
                    return

                } else if (TextUtils.isEmpty(MentorzApplication.instance?.prefs?.getString(Prefs.Key.LOCATION, ""))) {
                    DialogUtils.
                            showDialog(activity, "Alert", getString(R.string.please_enter_your_current_location), getString(R.string.ok), View.OnClickListener {
                                DialogUtils.dismiss()
                            })
//                    activity.showSnackBar(rootView, getString(R.string.please_enter_your_current_location))
                    return

                } else if (MentorzApplication.instance?.prefs?.getInt(Prefs.Key.EXPERIENCE, -1) == -1) {
                    DialogUtils.
                            showDialog(activity, "Alert", getString(R.string.please_enter_your_experience), getString(R.string.ok), View.OnClickListener {
                                DialogUtils.dismiss()
                            })
//                    activity.showSnackBar(rootView, getString(R.string.please_enter_your_experience))
                    return

                }
                if (Global.myexpertises!!.isEmpty()) {
                    DialogUtils.
                            showDialog(activity, "Alert", getString(R.string.please_select_expertise), getString(R.string.ok), View.OnClickListener {
                                DialogUtils.dismiss()
                            })
//                    activity.showSnackBar(rootView, getString(R.string.please_select_expertise))
                    return
                }
                val beMentorRequest = BeMentorRequest()
                beMentorRequest.designation = MentorzApplication.instance?.prefs?.getString(Prefs.Key.OCCUPATION, "")
                beMentorRequest.organization = MentorzApplication.instance?.prefs?.getString(Prefs.Key.ORGANIZATION, "")
                beMentorRequest.location = MentorzApplication.instance?.prefs?.getString(Prefs.Key.LOCATION, "")
                beMentorRequest.expYears = MentorzApplication.instance?.prefs?.getInt(Prefs.Key.EXPERIENCE, 0)
                Global.myexpertises!!.forEach {
                    it.isUpdated = null
                }
                beMentorRequest.experties = Global.myexpertises
                presenter.beMentor(beMentorRequest)
            }

        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_become_mentor, container, false)
    }

    val presenter = MatchPresenterImpl(this)
    val expertisePresenter = ExpertisePresenterImpl(this)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lEditProfile.setOnClickListener(this)
        lExpertise.setOnClickListener(this)
        btnSubmit.setOnClickListener(this)
        if(Global.myexpertises!!.isEmpty()) {
            expertisePresenter.getMyExpertise()
        }
        else{
            setExpertise()
        }
        if (MentorzApplication.instance?.prefs?.getBasicInfo().isNullOrEmpty()) {
            txtTitleOrOccupation.text = ""
        } else {
            txtTitleOrOccupation.text = MentorzApplication.instance?.prefs?.getOccupation()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            EDIT_PROFILE_REQUEST_CODE -> {
                if (Activity.RESULT_OK == resultCode) {
                    txtTitleOrOccupation.text = MentorzApplication.instance?.prefs?.getOccupation()
                }
            }
            EXPERTISE_REQUEST_CODE -> {
                if (Activity.RESULT_OK == resultCode) {
                    setExpertise()
                }
            }

        }

    }

    fun setExpertise() {
        if (Global.myexpertises!!.isEmpty()) {
            btnSubmit.text = getString(R.string.be_a_mentor)
        } else {
            btnSubmit.text = getString(R.string.update)

            loop@ for (item in Global.myexpertises!!.iterator()) {
                txtExpertise.text = item.expertise
                break@loop
            }
            if (Global.myexpertises!!.count() > 1) {
                val count = Global.myexpertises!!.count() - 1
                txtExpertiseCountDesc.text = " +$count more"

            } else {
                txtExpertiseCountDesc.text = ""
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        Global.defaultExpertiseMap?.clear()

    }
}