package com.mentorz.extensions

import android.content.Context
import android.support.v4.app.Fragment
import android.view.inputmethod.InputMethodManager
import android.widget.Toast

fun Fragment.hideKeyBoard() {
    val imm: InputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
}

fun Fragment.showToast(message: String) {
    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
}