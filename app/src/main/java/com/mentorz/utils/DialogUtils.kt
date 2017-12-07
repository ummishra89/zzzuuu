package com.mentorz.utils


import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.mentorz.MentorzApplication
import com.mentorz.R
import com.mentorz.customviews.CustomTextView
import com.mentorz.extensions.hideKeyBoard
import com.mentorz.listener.PublishDialogClickListener
import com.mentorz.listener.RatingClickListener
import com.mentorz.uploadfile.FileType
import com.squareup.picasso.Picasso
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

/**
 * Created by umesh on 09/08/17.
 */
object DialogUtils {

    var mDialog: Dialog? = null

    fun showDialog(context: Context, title: String, message: String, btn1Text: String) {
        try {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            if (mDialog != null && mDialog!!.isShowing) {
                return
            }
            mDialog = Dialog(context, android.R.style.Theme_Translucent_NoTitleBar)
            val mView = inflater.inflate(R.layout.single_button_alart_dialog, null)
            val txtTitle = mView.findViewById<TextView>(R.id.ctv_title)
            val txtMessage = mView.findViewById<TextView>(R.id.ctv_description)
            val btn1 = mView.findViewById<TextView>(R.id.ctv_button)
            if (title.isNullOrEmpty()) {
                txtTitle.visibility = View.GONE
            } else {
                txtTitle.text = title
            }
            txtMessage.text = message
            btn1.text = btn1Text
            btn1.setOnClickListener {
                dismiss()
            }
            mDialog!!.setContentView(mView)
            mDialog!!.setCancelable(false)
            mDialog!!.show()

        }catch (e :Exception){
            Log.e("DialogUtil",""+e.toString());
        }
    }

    fun showDialog(context: Context, title: String, message: String, btn1Text: String, listener: View.OnClickListener): Dialog {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        if (mDialog != null && mDialog!!.isShowing) {
            return mDialog!!
        }
        mDialog = Dialog(context, android.R.style.Theme_Translucent_NoTitleBar)
        val mView = inflater.inflate(R.layout.single_button_alart_dialog, null)
        val txtTitle = mView.findViewById<TextView>(R.id.ctv_title)
        val txtMessage = mView.findViewById<TextView>(R.id.ctv_description)
        val btn1 = mView.findViewById<TextView>(R.id.ctv_button)
        if (title.isNullOrEmpty()) {
            txtTitle.visibility = View.GONE
        } else {
            txtTitle.text = title
        }
        txtMessage.text = message
        btn1.text = btn1Text
        btn1.setOnClickListener(listener)
        mDialog!!.setContentView(mView)
        mDialog!!.setCancelable(false)
        mDialog!!.show()
        return mDialog!!
    }

    fun showDialog(context: Context, title: String, message: String, btn1Text: String, btn2Text: String, listener1: View.OnClickListener, listener2: View.OnClickListener) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (mDialog != null && mDialog!!.isShowing) {
            return
        }
        mDialog = Dialog(context, android.R.style.Theme_Translucent_NoTitleBar)
        val mView = inflater.inflate(R.layout.two_button_alart_dialog, null)
        val txtTitle = mView.findViewById<TextView>(R.id.ctv_title)
        val txtMessage = mView.findViewById<TextView>(R.id.ctv_description)
        val btn1 = mView.findViewById<TextView>(R.id.ctv_button)
        val btn2 = mView.findViewById<TextView>(R.id.ctv_button2)
        txtTitle.text = title
        txtMessage.text = message
        btn1.text = btn1Text
        btn2.text = btn2Text
        btn1.setOnClickListener(listener1)
        btn2.setOnClickListener(listener2)
        mDialog!!.setContentView(mView)
        mDialog!!.setCancelable(false)
        mDialog!!.show()

    }
    fun showRatingDialog(context: Context, name: String,listener: RatingClickListener): Dialog {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        if (mDialog != null && mDialog!!.isShowing) {
            return mDialog!!
        }
        mDialog = Dialog(context, android.R.style.Theme_Translucent_NoTitleBar)
        val mView = inflater.inflate(R.layout.layout_rate_now, null)
        val txtTitle = mView.findViewById<TextView>(R.id.ctv_title)
        val rootView = mView.findViewById<RelativeLayout>(R.id.rootView)
        val edtComment = mView.findViewById<EditText>(R.id.edtComment)
        val submit = mView.findViewById<TextView>(R.id.submit)
        val notNow = mView.findViewById<TextView>(R.id.notNow)
        val ratingBar =mView.findViewById<RatingBar>(R.id.rating)
        txtTitle.text = context.getString(R.string.rate)+":"+name+"!"
        mDialog!!.setContentView(mView)
        mDialog!!.setCancelable(false)
        rootView.setOnClickListener {
            (context as? Activity)?.hideKeyBoard()
        }
        submit.setOnClickListener {
            if(edtComment.text.isEmpty()||ratingBar.rating==0f){
                return@setOnClickListener
            }
            listener.onSubmit(ratingBar.rating,edtComment.text.toString().trim())
            dismiss()
        }
        notNow.setOnClickListener {
            dismiss()
        }
        mDialog!!.show()
        return mDialog!!
    }
    fun showPublishPostDialog(fileType:String,context: Context, uri: Uri?=null, listener: PublishDialogClickListener, text: String): Dialog {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        mDialog = Dialog(context, android.R.style.Theme_Translucent_NoTitleBar)
        val mView = inflater.inflate(R.layout.publish_post_dialog, null)
        val rootView = mView.findViewById<RelativeLayout>(R.id.rootView)
        val edtComment = mView.findViewById<EditText>(R.id.edtComment)
        val publish = mView.findViewById<TextView>(R.id.publish)
        val txtNumberOfWords = mView.findViewById<TextView>(R.id.txtNumberOfWords)
        val close = mView.findViewById<ImageView>(R.id.close)
        val action_image = mView.findViewById<ImageView>(R.id.action_image)
        val action_video = mView.findViewById<ImageView>(R.id.action_video)
        val txtWriteText = mView.findViewById<CustomTextView>(R.id.txtWriteText)
        val txtChooseImage = mView.findViewById<CustomTextView>(R.id.txtChooseImage)
        edtComment.setText(text)
        txtNumberOfWords.text = edtComment.text.toString().length.toString() + "/5000"
        txtNumberOfWords.setTextColor(Color.BLACK)
        edtComment.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
//                TODO("not TODOimplemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if(p0.toString().length<=5000){
                    txtNumberOfWords.setTextColor(Color.BLACK)
                    txtNumberOfWords.text = p0.toString().length.toString() + "/5000"
                }else{
                    txtNumberOfWords.setTextColor(Color.RED)
                    var textLength =5000-p0.toString().length as Int
                    txtNumberOfWords.text = ""+ textLength.toString()
                }
            }

        })
        val imagePreview = mView.findViewById<ImageView>(R.id.imgPreview)
        if(fileType==FileType.IMAGE && uri!=null){
            imagePreview.visibility=View.VISIBLE
            imagePreview.setImageURI(uri)
        }
        else if(fileType==FileType.VIDEO && uri!=null) {
            imagePreview.visibility = View.VISIBLE
            val bitmap = Utils.getBitmapFromUri(context,uri)
            imagePreview.setImageBitmap(bitmap)
        }
        mDialog!!.setContentView(mView)
        mDialog!!.setCancelable(false)
        rootView.setOnClickListener {
            (context as? Activity)?.hideKeyBoard()
        }
        action_image.setOnClickListener {
            listener.onImageClick(edtComment.text.toString())

        }
        action_video.setOnClickListener {
            listener.onVideoPublishClick(edtComment.text.toString())

        }
        publish.setOnClickListener {
            if(edtComment.text.length>5000){
                txtWriteText.text = context.getString(R.string.story_exceed)
                txtWriteText.visibility = View.VISIBLE
                Handler().postDelayed({
                    context.runOnUiThread {
                        txtWriteText.visibility = View.INVISIBLE
                    }
                }, 3000)
                return@setOnClickListener
            }
             if(edtComment.text.isEmpty()){
                 txtWriteText.text = context.getString(R.string.write_something_about_content)
                 txtWriteText.visibility = View.VISIBLE
                 Handler().postDelayed({
                     context.runOnUiThread {
                         txtWriteText.visibility = View.INVISIBLE
                     }
                 }, 3000)
                return@setOnClickListener
             }
             else if(uri==null){
                 txtChooseImage.visibility = View.VISIBLE
                 Handler().postDelayed({
                     context.runOnUiThread {
                         txtChooseImage.visibility = View.INVISIBLE
                     }
                 }, 3000)
                 return@setOnClickListener
             }

            listener.onPublish(fileType,uri,edtComment.text.toString().trim())
            dismiss()
        }
        close.setOnClickListener {
            dismiss()
        }
        mDialog!!.show()
        return mDialog!!
    }
    fun showPublishPostDialogWithOption(fileType:String,context: Context, uri: Uri?=null, listener: PublishDialogClickListener, text: String,activity:Activity): Dialog {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        mDialog = Dialog(context, android.R.style.Theme_Translucent_NoTitleBar)
        val mView = inflater.inflate(R.layout.publish_post_dialog, null)
        val rootView = mView.findViewById<RelativeLayout>(R.id.rootView)
        val edtComment = mView.findViewById<EditText>(R.id.edtComment)
        val publish = mView.findViewById<TextView>(R.id.publish)
        val txtNumberOfWords = mView.findViewById<TextView>(R.id.txtNumberOfWords)
        val close = mView.findViewById<ImageView>(R.id.close)
        val action_image = mView.findViewById<ImageView>(R.id.action_image)
        val action_video = mView.findViewById<ImageView>(R.id.action_video)
        val txtWriteText = mView.findViewById<CustomTextView>(R.id.txtWriteText)
        val txtChooseImage = mView.findViewById<CustomTextView>(R.id.txtChooseImage)
        edtComment.setText(text)
        txtNumberOfWords.text = edtComment.text.toString().length.toString() + "/5000"
        action_image.visibility=View.GONE
        action_video.visibility=View.GONE
        txtNumberOfWords.visibility=View.VISIBLE
        txtNumberOfWords.setTextColor(Color.BLACK)
        edtComment.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
//                TODO("not TODOimplemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0.toString().length<=5000){
                    txtNumberOfWords.setTextColor(Color.BLACK)
                    txtNumberOfWords.text = p0.toString().length.toString() + "/5000"
                }else{
                    txtNumberOfWords.setTextColor(Color.RED)
                    var textLength =5000-p0.toString().length as Int
                    txtNumberOfWords.text = ""+ textLength.toString()
                }

            }

        })
        val imagePreview = mView.findViewById<ImageView>(R.id.imgPreview)
        if(fileType==FileType.IMAGE && uri!=null){
            imagePreview.visibility=View.VISIBLE
            imagePreview.setImageURI(uri)
        }
        else if(fileType==FileType.VIDEO && uri!=null) {
            imagePreview.visibility = View.VISIBLE
            val bitmap = Utils.getBitmapFromUri(context,uri)
            imagePreview.setImageBitmap(bitmap)
        }
        mDialog!!.setContentView(mView)
        mDialog!!.setCancelable(false)
        rootView.setOnClickListener {
            (context as? Activity)?.hideKeyBoard()
        }
        action_image.setOnClickListener {
            listener.onImageClick(edtComment.text.toString())

        }
        action_video.setOnClickListener {
            listener.onVideoPublishClick(edtComment.text.toString())

        }
        publish.setOnClickListener {
            if(edtComment.text.length>5000){
                txtWriteText.text = context.getString(R.string.story_exceed)
                txtWriteText.visibility = View.VISIBLE
                Handler().postDelayed({
                    context.runOnUiThread {
                        txtWriteText.visibility = View.INVISIBLE
                    }
                }, 3000)
                return@setOnClickListener
            }
            if(edtComment.text.isEmpty()){
                txtWriteText.text = context.getString(R.string.write_something_about_content)
                txtWriteText.visibility = View.VISIBLE
                Handler().postDelayed({
                    context.runOnUiThread {
                        txtWriteText.visibility = View.INVISIBLE
                    }
                }, 3000)
                return@setOnClickListener
            }
            else if(uri==null){
                txtChooseImage.visibility = View.VISIBLE
                Handler().postDelayed({
                    context.runOnUiThread {
                        txtChooseImage.visibility = View.INVISIBLE
                    }
                }, 3000)
                return@setOnClickListener
            }

            listener.onPublish(fileType,uri,edtComment.text.toString().trim())
            dismiss()
        }
        close.setOnClickListener {
            dismiss()
            activity.finish();

        }
        txtNumberOfWords.visibility=View.VISIBLE
        mDialog!!.show()
        return mDialog!!
    }

    fun showImagePreview(context: Context, url: String?): Dialog {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        if (mDialog != null && mDialog!!.isShowing) {
            return mDialog!!
        }
        mDialog = Dialog(context, android.R.style.Theme_Translucent_NoTitleBar)
        val mView = inflater.inflate(R.layout.layout_image_preview, null)
        val done = mView.findViewById<TextView>(R.id.txtDone)
        val imageView = mView.findViewById<ImageView>(R.id.imageView)

        mDialog!!.setContentView(mView)
        mDialog!!.setCancelable(false)
        mDialog!!.setOnKeyListener({ _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dismiss()
            }
            true
        })
        done.setOnClickListener {
            dismiss()
        }
        if(!url.isNullOrEmpty()) {
            Picasso.with(context).load(url).into(imageView)
        }
        mDialog!!.show()
        return mDialog!!
    }
    fun dismiss() {
        if (mDialog != null && mDialog!!.isShowing) {
            mDialog!!.dismiss()
            mDialog = null
        }
    }

    fun showFullScreenImage(context: Context, uri: Uri?=null): Dialog {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mDialog = Dialog(context, android.R.style.Theme_Translucent_NoTitleBar)
        val mView = inflater.inflate(R.layout.dialog_fullscreen_image, null)
        val full_image = mView.findViewById<ImageView>(R.id.iv_fullscreen_image)
        val close = mView.findViewById<TextView>(R.id.tv_close)
        full_image.setImageURI(uri)
        close.setOnClickListener {
            dismiss()
        }
        mDialog!!.show()
        return mDialog!!
    }
}