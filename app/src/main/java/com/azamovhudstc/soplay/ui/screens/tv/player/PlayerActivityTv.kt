package com.azamovhudstc.soplay.ui.screens.tv.player

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.azamovhudstc.soplay.data.Movie
import com.azamovhudstc.soplay.data.response.FullMovieData
import com.azamovhudstc.soplay.data.response.MovieInfo
import com.azamovhudstc.soplay.databinding.ActivityPlayerTvBinding
import com.azamovhudstc.soplay.ui.activity.PlayerActivity
import com.azamovhudstc.soplay.utils.Refresh.activity
import com.azamovhudstc.soplay.utils.hideSystemBars
import com.azamovhudstc.soplay.viewmodel.imp.TvViewModelImpl
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.util.MimeTypes

class PlayerActivityTv : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerTvBinding
    private val model by viewModels<TvViewModelImpl>()
    var playerView: StyledPlayerView? = null
    private lateinit var animePlayingDetails: Movie

    private var player: SimpleExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        // show video inside notch if API >= 28 and orientation is landscape

        // show video inside notch if API >= 28 and orientation is landscape
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

        model.hrefData.observe(this) {
            playerView = binding.player
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            playerView!!.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
            setUpPlayer()
            playerView?.setKeepScreenOn(true)
            addMediaItem(it)
        }

      supportActionBar?.hide()
        parseExtra()
        model.loadHrefData(animePlayingDetails)
    }

    private fun parseExtra() {
        animePlayingDetails = intent.getSerializableExtra("EXTRA_EPISODE_DATA") as Movie
    }


    private fun setUpPlayer() {

        //initializing exoplayer
        player = SimpleExoPlayer.Builder(this).build()

        //set up audio attributes

        playerView?.player = player


        //hiding all the ui StyledPlayerView comes with
        playerView?.setShowPreviousButton(false)
        playerView?.setShowNextButton(false)
        playerView?.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT

        //setting the scaling mode to scale to fit
        player?.videoScalingMode = C.VIDEO_SCALING_MODE_DEFAULT
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

        //Creating a media item of HLS Type
        val mediaItem = MediaItem.Builder()
            .setUri(mediaUrl)
            .setMimeType(MimeTypes.APPLICATION_M3U8)
            .build()

        player?.setMediaItem(mediaItem)

        player?.prepare()
        player?.repeatMode = Player.REPEAT_MODE_ONE //repeating the video from start after it's over
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