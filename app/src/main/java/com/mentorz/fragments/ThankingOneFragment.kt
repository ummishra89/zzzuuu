package com.mentorz.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mentorz.R
import org.jetbrains.anko.doAsync


/**
 * Created by aMAN GUPTA on 25/7/17.
 */
class ThankingOneFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_getting_started_one, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        doAsync {
            someDelay()
            activity.runOnUiThread {
                val manager = activity.supportFragmentManager
                val ft = manager.beginTransaction()
                ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)

                ft.replace(R.id.container, ThankingTwoFragment())
                ft.commit()
            }
        }
    }

    private fun someDelay() {
        Thread.sleep(3000)
    }
}