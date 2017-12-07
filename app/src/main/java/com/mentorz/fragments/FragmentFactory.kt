package com.mentorz.fragments

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentTransaction


/**
 * This Class Handle the Fragment transaction
 */

class FragmentFactory {

    companion object {
        fun replaceFragment(fragment: Fragment, id: Int, context: Context): Unit {
            if (context is FragmentActivity) {
                val fragmentTransaction: FragmentTransaction = context.supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(id, fragment).commit()
            }
        }

        fun addFragment(fragment: Fragment, id: Int, context: Context) {
            if (context is FragmentActivity) {
                val fragmentTransaction: FragmentTransaction = context.supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(id, fragment).addToBackStack(null).commit()

            }

        }

        fun replaceFragment(fragment: Fragment, id: Int, context: Context, TAG: String) {

            if (context is FragmentActivity) {
                val fragmentByTag: Fragment = context.supportFragmentManager.findFragmentByTag(TAG)
                if (fragmentByTag == null) {

                    val fragmentTransaction: FragmentTransaction = context.supportFragmentManager.beginTransaction()
                    fragmentTransaction.replace(id, fragment).addToBackStack(TAG).commit()
                }
            }
        }

        fun back(context: Context): Unit {
            if (context is FragmentActivity) {
                if (context.supportFragmentManager.backStackEntryCount > 1) {
                    context.supportFragmentManager.popBackStack()
                } else {
                    context.finish()
                }
            }
        }


    }

}
