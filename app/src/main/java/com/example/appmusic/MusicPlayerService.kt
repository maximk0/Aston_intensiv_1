package com.example.appmusic

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class MusicPlayerService : Service() {

    private var music: MediaPlayer? = null

    override fun onBind(p0: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when (intent.action) {
            Constants.ACTION.START_FOREGROUND_ACTION -> {
                playMusic(currentSoundtrack)
            }

            Constants.ACTION.STOP_FOREGROUND_ACTION -> {
                stopMusic()
                stopSelf()
            }

            Constants.ACTION.NEXT_FOREGROUND_ACTION -> {
                nextTrack()
            }

            Constants.ACTION.PREVIOUS_FOREGROUND_ACTION -> {
                previousTrack()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun createNotification(): Notification {
        val stopIntent = Intent(this, MainActivity::class.java)
        stopIntent.action = Constants.ACTION.STOP_FOREGROUND_ACTION
        val pendingIntent =
            PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
            .setContentTitle(NOTIFICATION_TITLE)
            .setContentText(NOTIFICATION_TEXT)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                createNotification(),
                FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(NOTIFICATION_ID, createNotification())
        }

    }

    private fun playMusic(numberOfTrack: Int) {
        isPaused = false
        soundtrackName = listOfTracks[numberOfTrack]

        music?.stop()
        music = MediaPlayer.create(this, listOfTracks[numberOfTrack])
        music?.start()
    }

    private fun stopMusic() {
        isPaused = true

        music?.stop()
        music?.release()
        music = null
    }

    private fun nextTrack() {
        currentSoundtrack++
        if (currentSoundtrack > listOfTracks.lastIndex) {
            currentSoundtrack = 0
        }
        playMusic(currentSoundtrack)
    }

    private fun previousTrack() {
        currentSoundtrack--
        if (currentSoundtrack < 0) {
            currentSoundtrack = listOfTracks.lastIndex
        }
       playMusic(currentSoundtrack)
    }

    companion object {
        private val listOfTracks = listOf(
            R.raw.nikelback_we_will_rock_you,
            R.raw.imagine_dragons_im_so_sorry,
            R.raw.merelin_menson_sweet_dreems
        )

        private var currentSoundtrack = 0
        var isPaused = true
            private set

        var soundtrackName = listOfTracks[currentSoundtrack]
            private set

        const val NOTIFICATION_ID = 1
        const val NOTIFICATION_TITLE = "Music Player"
        const val NOTIFICATION_TEXT = "Soundtrack is playing"

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
