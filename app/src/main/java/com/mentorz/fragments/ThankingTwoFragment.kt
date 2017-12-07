package com.mentorz.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mentorz.R
import com.mentorz.activities.tutorial.TutorialActivity
import org.jetbrains.anko.doAsync

/**
 * Created by aMAN GUPTA on 25/7/17.
 */
class ThankingTwoFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_getting_started_two, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        doAsync {
            someDelay()
            activity.runOnUiThread {
                val intent = Intent(activity, TutorialActivity::class.java)
                startActivity(intent)
                activity.finish()
            }
        }
    }

    private fun someDelay() {
        Thread.sleep(3000)
    }
}