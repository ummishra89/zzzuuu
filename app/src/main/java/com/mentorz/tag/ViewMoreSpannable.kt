package com.mentorz.tag

import android.graphics.Color
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView


class ViewMoreSpannable(ctx: TagListener) : ClickableSpan() {
    var listener: TagListener = ctx


    override fun updateDrawState(ds: TextPaint) {
        ds.color = Color.parseColor("#9c9c9c")
        //ds.setARGB(255, 222, 22, 225);

    }

    override fun onClick(widget: View) {
        val tv = widget as TextView
        val s = tv.text as Spanned

        val start = s.getSpanStart(this)
        val end = s.getSpanEnd(this)
        val theWord = s.subSequence(start + 1, end).toString()
        // you can start another activity here
        listener.onClickViewMore(theWord)

    }
}
