package com.mentorz.extensions

import android.app.Activity
import android.content.Context
import android.support.design.widget.Snackbar
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * Created by craterzone on 05/07/17.
 */

fun Activity.showProgressBar(view: View?) {
    runOnUiThread {
        view?.visibility = View.VISIBLE

    }
}

fun Activity.hideProgressBar(view: View?) {
    runOnUiThread {
        view?.visibility = View.GONE
    }
}

fun Activity.hideKeyBoard() {
    val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.window.decorView.windowToken, 0)
}

fun Activity.showSnackBar(view: View, message: String) {
    Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
}

