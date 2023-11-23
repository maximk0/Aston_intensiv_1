package com.example.appmusic

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder

class MusicPlayerService : Service() {

    private var music: MediaPlayer? = null
    var isPaused = true
        private set

    override fun onCreate() {
        super.onCreate()
//        music = MediaPlayer.create(this, listOfTracks.first())
//        music?.start()
    }

    override fun onBind(p0: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when (intent.action) {
            Constants.ACTION.START_FOREGROUND_ACTION -> {
                startForeground(NOTIFICATION_ID, createNotification())
                playMusic(1)
            }

            Constants.ACTION.STOP_FOREGROUND_ACTION -> {
                stopMusic()
                stopSelf()
            }

            Constants.ACTION.NEXT_FOREGROUND_ACTION -> {
                startForeground(NOTIFICATION_ID, createNotification())
                changeTrack(true)
            }

            Constants.ACTION.PREVIOUS_FOREGROUND_ACTION -> {
                startForeground(NOTIFICATION_ID, createNotification())
                changeTrack(false)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun createNotification(): Notification {
        val stopIntent = Intent(this, MainActivity::class.java)
        stopIntent.action = Constants.ACTION.STOP_FOREGROUND_ACTION
        val pendingIntent =
            PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE)

        return Notification.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Music Player")
            .setContentText("Playing")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun playMusic(index: Int) {
        isPaused = false
        music = MediaPlayer.create(this, listOfTracks[index])
        music?.start()
    }

    private fun stopMusic() {
        isPaused = true
        music?.stop()
        music?.release()
        music = null
    }

    private fun changeTrack(isNext: Boolean) {
        music?.stop()
        when (isNext) {
            true -> playMusic(listOfTracks.lastIndex)
            false -> playMusic(0)
        }
    }

    companion object {
        const val NOTIFICATION_ID = 1

        val listOfTracks = listOf(
            R.raw.nikelback_we_will_rock_you,
            R.raw.imagine_dragons_im_so_sorry,
            R.raw.merelin_menson_sweet_dreems
        )

        fun getIntent(context: Context) = Intent(context, MusicPlayerService::class.java)
    }
}

object Constants {
    object ACTION {
        const val START_FOREGROUND_ACTION = "com.example.musicplayer.action.START_FOREGROUND"
        const val STOP_FOREGROUND_ACTION = "com.example.musicplayer.action.STOP_FOREGROUND"
        const val NEXT_FOREGROUND_ACTION = "com.example.musicplayer.action.NEXT_FOREGROUND"
        const val PREVIOUS_FOREGROUND_ACTION = "com.example.musicplayer.action.PREVIOUS_FOREGROUND"
    }

    const val NOTIFICATION_CHANNEL_ID = "musicplayer_channel"
    const val NAME_NOTIFICATION_CHANNEL = "Music Player"
}
