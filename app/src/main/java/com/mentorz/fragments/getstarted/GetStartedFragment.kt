package com.mentorz.fragments.getstarted

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mentorz.MentorzApplication
import com.mentorz.R
import com.mentorz.activities.ThankingActivity
import com.mentorz.activities.values.ValuesActivity
import com.mentorz.fragments.BaseFragment
import com.mentorz.interest.InterestsActivity
import com.mentorz.utils.Constant
import kotlinx.android.synthetic.main.fragment_get_started.*

/**
 * Created by aMAN GUPTA on 21/7/17.
 */
class GetStartedFragment : BaseFragment() {

    val VALUES_REQUEST_CODE = 106
    var fragmentNumber = 0
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_get_started, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        if (arguments != null) {
            fragmentNumber = arguments.getInt(Constant.FRAGMENT_POSITION)
        }
        when (fragmentNumber) {
            0 -> {
                root.setBackgroundResource(R.drawable.walkthrough_one)
                ctv_text.setText(R.string.share_what_you_learn)
            }
            1 -> {
                root.setBackgroundResource(R.drawable.walkthrough_two)
                ctv_text.setText(R.string.find_a_mentor_based_on)
            }
            2 -> {
                root.setBackgroundResource(R.drawable.walkthrough_three)
                ctv_text.setText(R.string.be_a_mentor_to_others)
            }
        }

//        var layoutParams = ctv_get_started.layoutParams
//        layoutParams.height
//        (activity as GetStartedActivity).setIndicatorHeight(ctv_get_started.y + ctv_get_started.height)
        ctv_get_started.setOnClickListener {
            val intent = Intent(context, ValuesActivity::class.java)
            startActivityForResult(intent, VALUES_REQUEST_CODE)
        }

    }

    private val INTEREST_REQUEST_CODE = 107

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            VALUES_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    MentorzApplication.instance?.prefs?.setValue()
                    val intent = Intent(context, InterestsActivity::class.java)
                    startActivityForResult(intent, INTEREST_REQUEST_CODE)

                } else {
                    activity.finish()
                }
            }
            INTEREST_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    MentorzApplication.instance?.prefs?.setInterest()

                    val intent = Intent(context, ThankingActivity::class.java)
                    startActivity(intent)
                }
                activity.finish()
            }
        }
    }
}