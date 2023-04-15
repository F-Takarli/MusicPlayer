package com.example.dunimusic

import android.media.AudioManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dunimusic.databinding.ActivityMainBinding
import com.google.android.material.slider.Slider
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var mediaPlayer: MediaPlayer
    lateinit var timer:Timer
    var isPlaying = true
    var isUserChanging = false
    var isMute=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prepareMusic()
        binding.btnPlayPause.setOnClickListener { configureMusic() }
        binding.btnGoBefore.setOnClickListener { goBeforeMusic() }
        binding.btnGoAfter.setOnClickListener { goAfterMusic() }
        binding.btnVolumeOnOff.setOnClickListener { configureVolume() }

        binding.sliderMain.addOnChangeListener { slider, value, fromUser ->
            binding.txtLeft.text = MillisToString(value.toLong())
            isUserChanging = fromUser
        }
        binding.sliderMain.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
            }

            override fun onStopTrackingTouch(slider: Slider) {
                mediaPlayer.seekTo(slider.value.toInt())
            }
        })

    }

    private fun prepareMusic() {
        mediaPlayer = MediaPlayer.create(this, R.raw.music_file)
        isPlaying = false
        binding.sliderMain.valueTo = mediaPlayer.duration.toFloat()
        binding.txtRight.text = MillisToString(mediaPlayer.duration.toLong())
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    if (!isUserChanging) {
                        binding.sliderMain.value = mediaPlayer.currentPosition.toFloat()
                    }


                }
            }
        }, 1000, 1000)
    }

    private fun configureVolume() {
        val audioManager=getSystemService(AUDIO_SERVICE) as AudioManager
        if(isMute){
            audioManager.adjustVolume(AudioManager.ADJUST_UNMUTE,AudioManager.FLAG_SHOW_UI)
            binding.btnVolumeOnOff.setImageResource(R.drawable.ic_volume_on)
            isMute=false
        }else{
            audioManager.adjustVolume(AudioManager.ADJUST_MUTE,AudioManager.FLAG_SHOW_UI)
            binding.btnVolumeOnOff.setImageResource(R.drawable.ic_volume_off)
            isMute=true
        }
    }

    private fun goAfterMusic() {
        val now = mediaPlayer.currentPosition
        val newValue = now + 15000
        mediaPlayer.seekTo(newValue)
    }

    private fun goBeforeMusic() {
        val now = mediaPlayer.currentPosition
        val newValue = now - 15000
        mediaPlayer.seekTo(newValue)
    }

    private fun configureMusic() {
        if (isPlaying) {
            mediaPlayer.pause()
            binding.btnPlayPause.setImageResource(R.drawable.ic_play)
            isPlaying = false
        } else {
            mediaPlayer.start()
            binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
            isPlaying = true
        }
    }


    private fun MillisToString(duration: Long): String {
        val second = duration / 1000 % 60
        val minute = duration / (1000 * 60) % 60
        return java.lang.String.format(Locale.US, "%02d:%02d", minute, second)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
        mediaPlayer.release()
    }
}