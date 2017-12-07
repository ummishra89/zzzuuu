package com.mentorz.utils

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import java.io.File

/**
 * Created by umesh on 16/08/17.
 */
object Utils {

     fun appFolderCheckandCreate(): String {

        var appFolderPath = ""
        val externalStorage = Environment.getExternalStorageDirectory()

        if (externalStorage.canWrite()) {
            appFolderPath = externalStorage.absolutePath + "/MyApp"
            val dir = File(appFolderPath)

            if (!dir.exists()) {
                dir.mkdirs()
            }

        } else {
//            showToast("  Storage media not found or is full ! ")
        }

        return appFolderPath
    }
    fun getMimeType(context: Context, uri: Uri): String {
        val extension: String

        //Check uri format to avoid null
        if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            //If scheme is a content
            val mime = MimeTypeMap.getSingleton()
            extension = mime.getExtensionFromMimeType(context.contentResolver.getType(uri))
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())

        }

        return extension
    }
     fun getRealPathFromURI(activity: Activity,contentUri: Uri): String {
         try{
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = activity.managedQuery(contentUri, proj, null, null, null)
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)}
         catch (e :Exception)
         {
             return contentUri.path
         }
    }

    var thumbColumns = arrayOf(MediaStore.Video.Thumbnails.DATA)
    var mediaColumns = arrayOf(MediaStore.Video.Media._ID)

    fun getThumbnailPathForLocalFile(context: Activity,
                                     fileUri: Uri): String? {

        val fileId = getFileId(context, fileUri)

        MediaStore.Video.Thumbnails.getThumbnail(context.contentResolver,
                fileId, MediaStore.Video.Thumbnails.MICRO_KIND, null)

        var thumbCursor: Cursor? = null
        try {

            thumbCursor = context.managedQuery(
                    MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                    thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID + " = "
                    + fileId, null, null)

            if (thumbCursor!!.moveToFirst()) {
                val thumbPath = thumbCursor.getString(thumbCursor
                        .getColumnIndex(MediaStore.Video.Thumbnails.DATA))

                return thumbPath
            }

        } finally {
        }

        return null
    }

    fun getFileId(context: Activity, fileUri: Uri): Long {

        val cursor = context.managedQuery(fileUri, mediaColumns,
                null, null, null)

        if (cursor.moveToFirst()) {
            val columnIndex = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val id = cursor.getInt(columnIndex)

            return id.toLong()
        }

        return 0
    }
    fun getBitmapFromUri(context: Context,uri: Uri): Bitmap {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, filePathColumn, null, null, null)
        cursor.moveToFirst()
        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
        val picturePath = cursor.getString(columnIndex)
        cursor.close()
        return ThumbnailUtils.createVideoThumbnail(picturePath, MediaStore.Video.Thumbnails.MINI_KIND)
    }

}