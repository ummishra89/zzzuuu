package com.mentorz.activities.values

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.mentorz.MentorzApplication
import com.mentorz.R
import com.mentorz.activities.BaseActivity
import com.mentorz.extensions.hideProgressBar
import com.mentorz.extensions.showProgressBar
import com.mentorz.extensions.showSnackBar
import com.mentorz.model.ValuesItem
import com.plumillonforge.android.chipview.ChipViewAdapter
import kotlinx.android.synthetic.main.activity_values.*

/**
 * Created by aMAN GUPTA on 20/7/17.
 */
class ValuesActivity : BaseActivity(), ValuesView, View.OnClickListener {

    override fun onSessionExpired() {
        runOnUiThread {

        }
    }

    override fun showEmptyValuesAlert() {
        showSnackBar(window.decorView.rootView, getString(R.string.select_atleast_one_values))

    }

    override fun showProgressBar() {
        runOnUiThread {
            showProgressBar(progressBar)
        }
    }

    override fun hideProgressBar() {
        runOnUiThread {
            hideProgressBar(progressBar)
        }
    }

    override fun onUpdateValuesSuccess() {
        setResult(Activity.RESULT_OK, Intent())
        finish()
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED, Intent())
        finish()

    }

    override fun onUpdateValuesFail() {
        showSnackBar(window.decorView.rootView, getString(R.string.unable_to_update_values))
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            imgBack.id -> finish()
            txtDone.id -> {
                presenter.updateValues()

            }
        }
    }


    override fun setValuesAdapter(values: List<ValuesItem>?) {
        runOnUiThread {
            chipView.adapter = MyChipViewAdapter(this)
            chipView.chipLayoutRes = R.layout.chip_close
            chipView.chipBackgroundColor = resources.getColor(R.color.colorAccent)
            chipView.chipBackgroundColorSelected = resources.getColor(R.color.black)
            chipView.chipList = values

        }
    }

    val presenter = ValuesPresenterImpl(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_values)
        setSupportActionBar(toolbar)
        txtTitle.text = getString(R.string.values)
        if (MentorzApplication.instance?.prefs?.hasValuesInterests()!!) {
            txtDone.text = getString(R.string.done)
        } else {
            txtDone.text = getString(R.string.next)
            valuesDesc.visibility = View.VISIBLE
            defaultView.visibility = View.GONE
        }
        imgBack.setOnClickListener(this)
        txtDone.setOnClickListener(this)
        presenter.getValues()

    }

    class MyChipViewAdapter(context: Context) : ChipViewAdapter(context) {

        override fun getBackgroundRes(position: Int): Int {
            val tag = getChip(position) as ValuesItem
            return 0

        }

        override fun onLayout(view: View?, position: Int) {
            val tagItem = getChip(position) as ValuesItem
            val container = view?.findViewById<View>(android.R.id.content) as FrameLayout
            val imgTick = view.findViewById<View>(R.id.imgTick) as ImageView
            val txtValue = view.findViewById<TextView>(android.R.id.text1) as TextView
            val txtClose = view.findViewById<TextView>(android.R.id.closeButton) as TextView

            txtClose.visibility = View.GONE

            if (tagItem.isMyValue!!) {
                imgTick.setBackgroundResource(R.drawable.selected_tick)
                container.setBackgroundResource(R.drawable.white_rounded_corner)
                txtValue.setTextColor(getColor(R.color.black))

            } else {
                txtValue.setTextColor(getColor(R.color.white))
                imgTick.setBackgroundResource(R.drawable.tick)
                container.setBackgroundResource(R.drawable.gray_rounded_corner)
            }
            (view.findViewById<View>(android.R.id.content) as FrameLayout).setOnClickListener {
                if (tagItem.isMyValue!!) {
                    imgTick.setBackgroundResource(R.drawable.tick)
                    container.setBackgroundResource(R.drawable.gray_rounded_corner)
                    txtValue.setTextColor(getColor(R.color.white))
                } else {
                    imgTick.setBackgroundResource(R.drawable.selected_tick)
                    container.setBackgroundResource(R.drawable.white_rounded_corner)
                    txtValue.setTextColor(getColor(R.color.black))

                }
                tagItem.isMyValue = !tagItem.isMyValue!!
            }
        }

        override fun getLayoutRes(position: Int): Int {
            return 0
        }

        override fun getBackgroundColorSelected(position: Int): Int {
            return 0
        }

        override fun getBackgroundColor(position: Int): Int {
            return 0
        }


    }

}