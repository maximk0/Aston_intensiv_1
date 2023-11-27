package com.example.appmusic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.example.appmusic.MusicPlayerService.Companion.isPaused
import com.example.appmusic.databinding.ActivityMainBinding

private const val IS_PAUSED = "playing key"

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private var isClickPaused = isPaused

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        savedInstanceState?. let {
            isClickPaused = it.getBoolean(IS_PAUSED)
        }

        updateButtonsState()

        Log.d("MAIN_ACTIVITY", "onCreate isPaused = $isClickPaused")

        binding.trackName.isSelected = true

        with(binding) {
            updateButtonsState()

            next.setOnClickListener {
                isClickPaused = false

                startMusicService(Constants.ACTION.NEXT_FOREGROUND_ACTION)
                updateButtonsState()
            }

            play.setOnClickListener {
                isClickPaused = !isClickPaused

                startMusicService(Constants.ACTION.START_FOREGROUND_ACTION)
                updateButtonsState()
            }

            pause.setOnClickListener {
                isClickPaused = true

                startMusicService(Constants.ACTION.STOP_FOREGROUND_ACTION)
                updateButtonsState()
            }

            previous.setOnClickListener {
                isClickPaused = false

                startMusicService(Constants.ACTION.PREVIOUS_FOREGROUND_ACTION)
                updateButtonsState()
            }
        }

    }

    private fun updateButtonsState() {
        if (!isClickPaused) {
            binding.pause.visibility = View.VISIBLE
            binding.play.visibility = View.GONE
            binding.trackName.text = getString(
                R.string.track_name,
                resources.getResourceEntryName(MusicPlayerService.soundtrackName)
            )
            binding.lottieAnimationView.playAnimation()
        } else {
            binding.play.visibility = View.VISIBLE
            binding.pause.visibility = View.GONE
            binding.lottieAnimationView.pauseAnimation()
        }
    }

    private fun startMusicService(action: String) {
        MusicPlayerService.getIntent(this).also {
            it.action = action
            ContextCompat.startForegroundService(this, it)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(IS_PAUSED, isClickPaused)
        Log.d("MAIN_ACTIVITY", "onSaveInstanceState isPaused = $isClickPaused")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d("MAIN_ACTIVITY", "onRestoreInstanceState isPaused = $isClickPaused")
        isClickPaused = savedInstanceState.getBoolean(IS_PAUSED)
        updateButtonsState()
    }

}
