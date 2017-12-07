package com.mentorz.tag

import android.text.SpannableString
import com.mentorz.utils.Constant
import java.util.*
import java.util.regex.Pattern


/**
 * Created by craterzone on 7/3/16.
 */
object TagUtil {


    fun getViewMoreSpans(body: String, prefix: String): ArrayList<IntArray> {
        val spans = ArrayList<IntArray>()

        val pattern = Pattern.compile(prefix)
        val matcher = pattern.matcher(body)

        // Check all occurrences
        while (matcher.find()) {
            val currentSpan = IntArray(2)
            currentSpan[0] = matcher.start()
            currentSpan[1] = matcher.end()
            spans.add(currentSpan)
        }
        return spans
    }


    fun setSpanViewMore(context: TagListener, commentsUname: SpannableString, viewmoreSpans: ArrayList<IntArray>) {
        for (i in viewmoreSpans.indices) {
            val span = viewmoreSpans[i]
            val viewmoreStart = span[0]
            val viewmoreEnd = span[1]
            commentsUname.setSpan(ViewMoreSpannable(context),
                    viewmoreStart,
                    viewmoreEnd, 1)

        }
    }


    fun getSpannableString(context: TagListener, str: String): SpannableString {

        val viewmoreSpans = TagUtil.getViewMoreSpans(str, Constant.READ_MORE_TEXT)
        val commentsContent1 = SpannableString(str)
        setSpanViewMore(context, commentsContent1, viewmoreSpans)
        return commentsContent1
    }


}
