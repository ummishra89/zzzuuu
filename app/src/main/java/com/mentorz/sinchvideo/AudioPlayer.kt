package com.mentorz.sinchvideo

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.mentorz.R
import java.io.IOException

/**
 * Created by craterzone on 17/07/17.
 */
class AudioPlayer(context: Context) {

    internal val LOG_TAG = AudioPlayer::class.java.simpleName

    private var mContext: Context? = null

    private var mPlayer: MediaPlayer? = null

    private var mProgressTone: AudioTrack? = null

    private val SAMPLE_RATE = 16000

    fun playRingtone() {
        val audioManager = mContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.isSpeakerphoneOn = false
        audioManager.isMicrophoneMute = false

        // Honour silent mode
        when (audioManager.ringerMode) {
            AudioManager.RINGER_MODE_NORMAL -> {
                mPlayer = MediaPlayer()
                mPlayer!!.setAudioStreamType(AudioManager.STREAM_RING)

                try {
                    mPlayer!!.setDataSource(mContext,
                            Uri.parse("android.resource://" + mContext?.packageName + "/" + R.raw.phone_loud1))
                    mPlayer!!.prepare()
                } catch (e: IOException) {
                    Log.e(LOG_TAG, "Could not setup media player for ringtone")
                    mPlayer = null
                    return
                }

                mPlayer!!.isLooping = true
                mPlayer!!.start()
            }
        }
    }

    fun stopRingtone() {
        if (mPlayer != null) {
            mPlayer!!.stop()
            mPlayer!!.release()
            mPlayer = null
        }
    }

    fun playProgressTone() {
        stopProgressTone()
        try {
            mProgressTone = createProgressTone(mContext)
            mProgressTone!!.play()
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Could not play progress tone", e)
        }

    }

    fun stopProgressTone() {
        if (mProgressTone != null) {
            mProgressTone!!.stop()
            mProgressTone!!.release()
            mProgressTone = null
        }
    }

    @Throws(IOException::class)
    private fun createProgressTone(context: Context?): AudioTrack {
        val fd = context?.resources?.openRawResourceFd(R.raw.progress_tone)
        val length = fd?.length?.toInt()

        val audioTrack = AudioTrack(AudioManager.STREAM_VOICE_CALL, SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, length!!, AudioTrack.MODE_STATIC)
        val data = ByteArray(length)
        readFileToBytes(fd, data)

        audioTrack.write(data, 0, data.size)
        audioTrack.setLoopPoints(0, data.size / 2, 30)

        return audioTrack
    }

    @Throws(IOException::class)
    private fun readFileToBytes(fd: AssetFileDescriptor, data: ByteArray) {
        val inputStream = fd.createInputStream()

        var bytesRead = 0
        while (bytesRead < data.size) {
            val res = inputStream.read(data, bytesRead, data.size - bytesRead)
            if (res == -1) {
                break
            }
            bytesRead += res
        }
    }

    init {
        this.mContext = context.applicationContext
    }

}