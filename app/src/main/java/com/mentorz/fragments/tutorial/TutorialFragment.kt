package com.mentorz.fragments.tutorial

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mentorz.R
import com.mentorz.activities.HomeActivity
import com.mentorz.fragments.BaseFragment
import com.mentorz.utils.Constant
import kotlinx.android.synthetic.main.fragment_toturial.*

/**
 * Created by aMAN GUPTA on 21/7/17.
 */
class TutorialFragment : BaseFragment() {

    var fragmentNumber = 0
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_toturial, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        if (arguments != null) {
            fragmentNumber = arguments.getInt(Constant.FRAGMENT_POSITION)
        }
        when (fragmentNumber) {
            0 -> {
                root.setImageResource(R.drawable.tutorial1)

            }
            1 -> {
                root.setImageResource(R.drawable.tutorial2)

            }
            2 -> {
                root.setImageResource(R.drawable.tutorial3)

            }
            3 -> {
                root.setImageResource(R.drawable.tutorial4)

            }
            4 -> {
                root.setImageResource(R.drawable.tutorial5)

            }
            5 -> {
                root.setImageResource(R.drawable.tutorial6)
                action.setText(R.string.done)
            }
        }
        action.setOnClickListener {
            val intent = Intent(context, HomeActivity::class.java)
            startActivityForResult(intent, 0)
            activity.finish()
        }

    }
}