package com.magicalrice.project.library_base.base

import android.content.Context
import android.media.MediaPlayer
import android.util.Log

/**
 * @package com.magicalrice.project.library_base.base
 * @author Adolph
 * @date 2019-04-22 Mon
 * @description TODO
 */

class MediaPlayerUtiles {
    var mMediaPlayer: MediaPlayer? = null

    private object Holder {
        val INSTANCE = MediaPlayerUtiles()
    }

    fun getMediaPlayer(): MediaPlayer {
        if (null == mMediaPlayer) {
            mMediaPlayer = MediaPlayer()
        }

        return mMediaPlayer!!
    }

    /**
     * 播放音频
     */
    fun playAudio(mContext: Context, fileName: String) {
        try {
            stopAudio()//如果正在播放就停止
            val assetManager = mContext.assets
            val afd = assetManager.openFd(fileName)
            val mediaPlayer = getMediaPlayer()
            mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            mediaPlayer.isLooping = false//循环播放
            mediaPlayer.prepare()
            mediaPlayer.setOnPreparedListener { mediaPlayer -> mediaPlayer.start() }
        } catch (e: Exception) {
            Log.e("播放音频失败", "")
        }
    }

    /**
     * 停止播放音频
     */
    fun stopAudio() {
        try {
            if (mMediaPlayer?.isPlaying == true) {
                mMediaPlayer?.pause()
                mMediaPlayer?.reset()
                mMediaPlayer?.stop()
            }
        } catch (e: Exception) {
            Log.e("stopAudio", e.message)
        }
    }

    companion object {
        fun getInstance() = Holder.INSTANCE
    }
}