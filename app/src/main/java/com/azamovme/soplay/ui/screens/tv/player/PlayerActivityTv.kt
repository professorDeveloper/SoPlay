package com.azamovme.soplay.ui.screens.tv.player

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.ImageButton
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.azamovme.soplay.R
import com.azamovme.soplay.data.Movie
import com.azamovme.soplay.databinding.ActivityPlayerTvBinding
import com.azamovme.soplay.ui.activity.PlayerActivity
import com.azamovme.soplay.utils.hideSystemBars
import com.azamovme.soplay.viewmodel.imp.TvViewModelImpl
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.material.snackbar.Snackbar

class PlayerActivityTv : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerTvBinding
    private val model by viewModels<TvViewModelImpl>()
    var playerView: StyledPlayerView? = null
    private lateinit var animePlayingDetails: Movie
    private lateinit var rotateBtn: ImageButton
    private lateinit var icFullScreen: ImageButton
    private var player: SimpleExoPlayer? = null
    private var isFullscreen: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P &&
            resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        ) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        binding = ActivityPlayerTvBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Initialize
        hideSystemBars()
        onBackPressedDispatcher.addCallback(this) {
            finish()
        }

        playerView = binding.player
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        parseExtra()
        setUpPlayer()
        playerView?.keepScreenOn = true
        rotateBtn = playerView?.findViewById(R.id.exo_rotate)!!
        icFullScreen = playerView?.findViewById(R.id.exo_screen)!!
        addMediaItem(animePlayingDetails.href)
        supportActionBar?.hide()
        model.loadHrefData(animePlayingDetails)
        rotateBtn.setOnClickListener {
            toggleScreenOrientation()
        }
        icFullScreen.setOnClickListener {
            if (isFullscreen < 1) isFullscreen += 1 else isFullscreen = 0
            when (isFullscreen) {
                0 -> {
                    if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
                        binding.player.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
                    } else {
                        binding.player.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
                    }
                }

                1 -> {
                    binding.player.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL

                }
            }

            Snackbar.make(
                binding.player, (when (isFullscreen) {
                    0 -> "Original"
                    1 -> "Stretch"
                    else -> "Original"
                }), 1000
            ).show()
        }
    }

    private fun toggleScreenOrientation() {
        requestedOrientation = when (requestedOrientation) {
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT

            else -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        }
        if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
            binding.player.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
        } else {
            binding.player.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
        }

    }

    private fun parseExtra() {
        animePlayingDetails = intent.getSerializableExtra("EXTRA_EPISODE_DATA") as Movie
    }


    private fun setUpPlayer() {
        player = SimpleExoPlayer.Builder(this).build()

        playerView?.player = player

        playerView?.setShowPreviousButton(false)
        playerView?.setShowNextButton(false)
        val exoProgress = findViewById<PlayerActivity.ExtendedTimeBar>(R.id.exo_progress)
        exoProgress.setForceDisabled(true)
    }


    override fun onResume() {
        super.onResume()
        playerView?.player?.play()
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    override fun onPause() {
        super.onPause()
        playerView?.player?.pause()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("rotation", requestedOrientation)
        super.onSaveInstanceState(outState)
    }


    override fun onDestroy() {
        super.onDestroy()
        playerView?.player?.stop()
        playerView?.player?.release()
        finishAndRemoveTask()

    }

    override fun finish() {
        finishAndRemoveTask()

    }


    private fun addMediaItem(mediaUrl: String) {

        val mediaItem = MediaItem.Builder()
            .setUri(mediaUrl)
            .setMimeType(MimeTypes.APPLICATION_M3U8)
            .build()

        player?.setMediaItem(mediaItem)

        player?.prepare()
        player?.repeatMode = Player.REPEAT_MODE_ONE
        player?.play()
    }

    companion object {
        fun newIntent(
            context: Context,
            episodeData: Movie,
        ): Intent {
            val intent = Intent(context, PlayerActivityTv::class.java)
            intent.putExtra("EXTRA_EPISODE_DATA", episodeData)
            return intent
        }
    }

}