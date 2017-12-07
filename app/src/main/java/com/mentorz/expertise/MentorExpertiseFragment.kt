package com.mentorz.expertise


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.mentorz.R
import com.mentorz.fragments.BaseFragment
import com.plumillonforge.android.chipview.ChipViewAdapter
import kotlinx.android.synthetic.main.fragment_mentor_expertise.*

/**
 * A simple [Fragment] subclass.
 */
class MentorExpertiseFragment : BaseFragment() {
    override fun networkError() {
        super.networkError()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mentor_expertise, container, false)
    }

    var expertiseList: MutableList<ExpertiseItem>? = null
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chipView.adapter = MyChipViewAdapter(activity)
        chipView.chipLayoutRes = R.layout.chip_close
        chipView.chipBackgroundColor = resources.getColor(R.color.colorAccent)
        chipView.chipBackgroundColorSelected = resources.getColor(R.color.black)
        val list: List<ExpertiseItem> = activity.intent.getParcelableArrayListExtra("mentor_expertise")
        expertiseList = list as MutableList<ExpertiseItem>
        expertiseList!!
                .filter { expertiseList!!.contains(it) && it.hasChildren!! }
                .forEach { removeAllChild(it) }
        chipView.chipList = list

    }

    fun removeAllChild(expertiseItem: ExpertiseItem) {
        val list = expertiseList!!.filter {
            it.parentId == expertiseItem.expertiseId
        }
        for (item in list) {
            if (item.hasChildren!!) {
                removeAllChild(item)
                expertiseList!!.remove(item)
            } else {
                expertiseList!!.remove(item)
            }
        }
    }

    class MyChipViewAdapter(context: Context) : ChipViewAdapter(context) {

        override fun getBackgroundRes(position: Int): Int {
            return 0

        }


        override fun onLayout(view: View?, position: Int) {
            val tagItem = getChip(position) as ExpertiseItem
            val container = view?.findViewById<View>(android.R.id.content) as FrameLayout
            val imgTick = view.findViewById<View>(R.id.imgTick) as ImageView
            val txtValue = view.findViewById<TextView>(android.R.id.text1) as TextView
            val txtClose = view.findViewById<View>(android.R.id.closeButton) as TextView

            txtClose.visibility = View.GONE
            imgTick.setBackgroundResource(R.drawable.selected_tick)
            container.setBackgroundResource(R.drawable.white_rounded_corner)
            txtValue.setTextColor(getColor(R.color.black))

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
