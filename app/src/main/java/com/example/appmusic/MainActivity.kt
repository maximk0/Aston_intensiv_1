package com.example.appmusic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import com.example.appmusic.databinding.ActivityMainBinding

private const val IS_PAUSED = "playing key"

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private var isPaused = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        savedInstanceState?. let {
            isPaused = it.getBoolean(IS_PAUSED)
        }
        updateButtonsState()

        Log.d("MAIN_ACTIVITY", "onCreate isPaused = $isPaused")

        binding.trackName.isSelected = true

        with(binding) {
            updateButtonsState()

            next.setOnClickListener {
                isPaused = false
                updateButtonsState()
                MusicPlayerService.getIntent(this@MainActivity).also {
                    it.action = Constants.ACTION.NEXT_FOREGROUND_ACTION
                    startService(it)
                }
            }

            play.setOnClickListener {
                isPaused = !isPaused
                updateButtonsState()

                MusicPlayerService.getIntent(this@MainActivity).also {
                    it.action = Constants.ACTION.START_FOREGROUND_ACTION
                    startService(it)
                }
            }

            pause.setOnClickListener {
                isPaused = !isPaused
                updateButtonsState()

                MusicPlayerService.getIntent(this@MainActivity).also {
                    it.action = Constants.ACTION.STOP_FOREGROUND_ACTION
                    startService(it)
                }
            }

            previous.setOnClickListener {
                isPaused = false
                updateButtonsState()
                MusicPlayerService.getIntent(this@MainActivity).also {
                    it.action = Constants.ACTION.PREVIOUS_FOREGROUND_ACTION
                    startService(it)
                }
            }
        }

    }

    private fun updateButtonsState() {
        if (!isPaused) {
            binding.pause.visibility = View.VISIBLE
            binding.play.visibility = View.GONE
            binding.trackName.text = getString(
                R.string.track_name,
                resources.getResourceEntryName(R.raw.imagine_dragons_im_so_sorry)
            )
            binding.lottieAnimationView.playAnimation()
        } else {
            binding.play.visibility = View.VISIBLE
            binding.pause.visibility = View.GONE
            binding.lottieAnimationView.pauseAnimation()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(IS_PAUSED, isPaused)
        Log.d("MAIN_ACTIVITY", "onSaveInstanceState isPaused = $isPaused")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d("MAIN_ACTIVITY", "onRestoreInstanceState isPaused = $isPaused")
        isPaused = savedInstanceState.getBoolean(IS_PAUSED)
        updateButtonsState()
    }

}
