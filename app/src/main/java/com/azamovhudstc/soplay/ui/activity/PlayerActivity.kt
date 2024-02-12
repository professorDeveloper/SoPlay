package com.azamovhudstc.soplay.ui.activity

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.*
import android.widget.*
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
import androidx.media3.common.Player
import com.azamovhudstc.soplay.R
import com.azamovhudstc.soplay.data.response.FullMovieData
import com.azamovhudstc.soplay.data.response.MovieInfo
import com.azamovhudstc.soplay.databinding.ActivityPlayerBinding
import com.azamovhudstc.soplay.ui.adapter.CustomAdapter
import com.azamovhudstc.soplay.utils.*
import com.azamovhudstc.soplay.viewmodel.imp.PlayerViewModel
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy
import kotlin.math.min

@AndroidEntryPoint
class PlayerActivity : AppCompatActivity(), Player.Listener {
    private var notchHeight: Int = 1

    private val model by viewModels<PlayerViewModel>()
    private var quality: String = "Auto"
    private lateinit var animePlayingDetails: FullMovieData
    private lateinit var binding: ActivityPlayerBinding
    private var mBackstackLost = true
    private lateinit var exoTopControllers: LinearLayout
    private lateinit var exoMiddleControllers: LinearLayout
    private lateinit var exoBottomControllers: LinearLayout
    private val handler = Handler(Looper.getMainLooper())
    private var isFullscreen: Int = 0
    private var orientationListener: OrientationEventListener? = null

    private var isNormal = true


    // Top buttons
    private lateinit var loadingLayout: LinearLayout
    private lateinit var playerView: DoubleTapPlayerView
    private lateinit var subsToggleButton: ToggleButton
    private lateinit var exoPlay: ImageView
    private lateinit var scaleBtn: ImageButton
    private lateinit var exoRotate: ImageButton
    private lateinit var qualityBtn: ImageButton
    private lateinit var downloadBtn: ImageButton
    private lateinit var prevEpBtn: ImageButton
    private var doubleBackToExitPressedOnce: Boolean = false
    private lateinit var backPressSnackBar: Snackbar
    private lateinit var nextEpBtn: ImageButton
    private lateinit var videoEpTextView: TextView
    private lateinit var exoPip: ImageButton
    private lateinit var exoSpeed: ImageButton
    private lateinit var exoProgress: ExtendedTimeBar
    private lateinit var exoLock: ImageButton
    private var isInit: Boolean = false
    private var isTV: Boolean = false
    private val mCookieManager = CookieManager()
    private lateinit var exoBrightness: Slider
    private lateinit var exoVolume: Slider
    private lateinit var exoBrightnessCont: View
    private lateinit var exoVolumeCont: View
    var rotation = 0

    override fun onAttachedToWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val displayCutout = window.decorView.rootWindowInsets.displayCutout
            if (displayCutout != null) {
                if (displayCutout.boundingRects.size > 0) {
                    notchHeight = min(
                        displayCutout.boundingRects[0].width(),
                        displayCutout.boundingRects[0].height()
                    )
                    checkNotch()
                }
            }
        }
        super.onAttachedToWindow()
    }


    private fun checkNotch() {
        if (notchHeight != 0) {
            val orientation = resources.configuration.orientation
            playerView.findViewById<View>(R.id.exo_controller_cont)
                .updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        marginStart = notchHeight
                        marginEnd = notchHeight
                        topMargin = 0
                    } else {
                        topMargin = notchHeight
                        marginStart = 0
                        marginEnd = 0
                    }
                }
            playerView.findViewById<View>(androidx.media3.ui.R.id.exo_buffering).translationY =
                (if (orientation == Configuration.ORIENTATION_LANDSCAPE) 0 else (notchHeight + 8f.px)).dp
            exoBrightnessCont.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                marginEnd =
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) notchHeight else 0
            }
            exoVolumeCont.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                marginStart =
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) notchHeight else 0
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
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
        parseExtra()
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // hiding 3 bottom buttons by default and showing when user swipes
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }


        //Initialize
        hideSystemBars()
        onBackPressedDispatcher.addCallback(this) {
            finish()
        }




        mCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        CookieHandler.setDefault(mCookieManager)
        playerView = binding.exoPlayerView
        playerView.doubleTapOverlay = binding.doubleTapOverlay
        loadingLayout = binding.loadingLayout

        exoPip = playerView.findViewById(R.id.exo_pip)
        exoSpeed = playerView.findViewById(R.id.exo_speed)
        prevEpBtn = playerView.findViewById(R.id.exo_prev_ep)
        exoRotate = playerView.findViewById(R.id.exo_rotate)
        nextEpBtn = playerView.findViewById(R.id.exo_next_ep)
        videoEpTextView = playerView.findViewById(R.id.exo_title)
        downloadBtn = playerView.findViewById(R.id.exo_download)
        exoLock = playerView.findViewById(R.id.exo_lock)
        exoPlay = playerView.findViewById(R.id.exo_play_pause)
        exoMiddleControllers = findViewById(R.id.exo_middle_controllers)
        exoTopControllers = findViewById(R.id.exo_top_controllers)
        exoBottomControllers = findViewById(R.id.exo_bottom_controllers)
        exoBrightness = findViewById(R.id.exo_brightness)
        exoVolume = findViewById(R.id.exo_volume)
        exoBrightnessCont = findViewById(R.id.exo_brightness_cont)
        exoVolumeCont = findViewById(R.id.exo_volume_cont)
        exoProgress = findViewById(R.id.exo_progress)
        updateEpisodeName()
        playerView.keepScreenOn = true
        playerView.player = model.player
        playerView.subtitleView?.visibility = View.VISIBLE
        playerView.findViewById<ExtendedTimeBar>(R.id.exo_progress).setKeyTimeIncrement(10000)
        prepareButtons()


        model.downloadLink.observe(this) { link ->
            downloadBtn.show()
            downloadBtn.setOnClickListener {
                download(
                    this,
                    movieInfo!!,
                    link,
                    epListByName.get(currentEpIndex).first
                )
            }
        }


        model.isLoading.observe(this) { isLoading ->
            loadingLayout.isVisible = isLoading
            playerView.isVisible = !isLoading
        }
        model.keepScreenOn.observe(this) { keepScreenOn ->
            playerView.keepScreenOn = keepScreenOn
        }
        model.playNextEp.observe(this) { playNextEp ->
            if (playNextEp) setNewEpisode()
        }
        model.isError.observe(this) { isError ->
            if (isError) {
                finishAndRemoveTask()
            }
        }
        if (!isInit) {
            model.setAnimeLink(
                epList[currentEpIndex] as String
            )
            prevEpBtn.setImageViewEnabled(PlayerActivity.currentEpIndex.toInt() >= 2)
            nextEpBtn.setImageViewEnabled(currentEpIndex.toInt() != epCount.toInt())
        }
        isInit = true

    }


    private fun setNewEpisode(increment: Int = 1) {
        currentEpIndex = currentEpIndex.toInt() + increment
        println(currentEpIndex)
        if (currentEpIndex.toInt() > epCount.toInt() || currentEpIndex.toInt() < 1)
        else {
            model.setAnimeLink(
                epList.get(currentEpIndex),
                true
            )
            prevEpBtn.setImageViewEnabled(currentEpIndex.toInt() >= 2)
            nextEpBtn.setImageViewEnabled(currentEpIndex.toInt() != epCount.toInt())
            model.player.stop()
            updateEpisodeName()

        }
    }


    @SuppressLint("StringFormatInvalid")
    private fun changeVideoSpeed(byInt: Float) {
        model.player.playbackParameters = PlaybackParameters(byInt)
    }

    private fun lockScreen(locked: Boolean) {
        if (locked) {
            exoTopControllers.visibility = View.INVISIBLE
            exoMiddleControllers.visibility = View.INVISIBLE
            exoBottomControllers.visibility = View.INVISIBLE
            exoProgress.setForceDisabled(true)
        } else {
            exoProgress.setForceDisabled(false)
            exoTopControllers.visibility = View.VISIBLE
            exoMiddleControllers.visibility = View.VISIBLE
            exoBottomControllers.visibility = View.VISIBLE
        }
    }


    @SuppressLint("WrongConstant")
    private fun prepareButtons() {
        // For Screen Rotation
        var flag = true
        exoRotate.setOnClickListener {
            if (flag) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
                flag = false
            } else {
                this.requestedOrientation =
                    ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
                flag = true

            }
        }






        playerView.setLongPressListenerEvent {
            val currentSpeed = model.player.playbackParameters.speed
            if (currentSpeed == 1f && model.player.playWhenReady && isNormal) {
                val params = PlaybackParameters(2f)
                model.player.setPlaybackParameters(params)
                snackString("Speed 2x", this@PlayerActivity)
            }
        }



        playerView.setActionUpListener {
            val currentSpeed = model.player.playbackParameters.speed
            if (currentSpeed == 2f && model.player.playWhenReady && isNormal) {
                val params = PlaybackParameters(1f)
                model.player.setPlaybackParameters(params)
                snackString("Speed 1x", this@PlayerActivity)
            }
        }

        exoLock.setOnClickListener {
            if (!isLocked) {
                exoLock.setImageResource(R.drawable.ic_round_lock_24)
            } else {
                exoLock.setImageResource(R.drawable.ic_round_lock_open_24)
            }
            isLocked = !isLocked
            lockScreen(isLocked)
        }

        scaleBtn = playerView.findViewById(R.id.exo_screen)
        qualityBtn = playerView.findViewById(R.id.exo_quality)
        prevEpBtn = playerView.findViewById(R.id.exo_prev_ep)
        nextEpBtn = playerView.findViewById(R.id.exo_next_ep)
        subsToggleButton = playerView.findViewById(R.id.subs_toggle_btn)


//        model.player.playbackParameters =

        qualityBtn.setOnClickListener {
            initPopupQuality().show()
        }

        exoSpeed.setOnClickListener {
            val builder =
                AlertDialog.Builder(this, R.style.DialogTheme)
            builder.setTitle("Speed")


            val speed = arrayOf("0.25", "0.5", "Normal", "1.5", "2")
            val adapter = CustomAdapter(
                this,
                speed
            )
            builder.setAdapter(adapter) { dad, which ->
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
                hideSystemBars()

                when (which) {
                    0 -> {
                        isNormal = false
                        adapter.setSelected(0)
                        changeVideoSpeed(0.25f)
                    }

                    1 -> {
                        isNormal = false
                        adapter.setSelected(1)
                        changeVideoSpeed(0.5f)
                    }

                    2 -> {
                        isNormal = true

                        adapter.setSelected(2)
                        changeVideoSpeed(1f)
                    }

                    3 -> {
                        isNormal = false
                        adapter.setSelected(3)
                        changeVideoSpeed(1.5f)
                    }

                    else -> {
                        isNormal = false
                        adapter.setSelected(4)
                        changeVideoSpeed(2f)

                    }
                }
            }
            hideSystemBars()

            val dialog = builder.create()
            dialog.show()
        }


        subsToggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                playerView.subtitleView?.visibility = View.VISIBLE
            } else {
                playerView.subtitleView?.visibility = View.GONE
            }
        }

        exoPip.setOnClickListener {
            val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val status = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                appOps.checkOpNoThrow(
                    AppOpsManager.OPSTR_PICTURE_IN_PICTURE, android.os.Process.myUid(), packageName
                ) == AppOpsManager.MODE_ALLOWED
            } else false
            // API >= 26 check
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (status) {
                    this.enterPictureInPictureMode(
                        PictureInPictureParams.Builder().build()
                    )
                    playerView.useController = false
                    pipStatus = false
                } else {
                    val intent = Intent(
                        "android.settings.PICTURE_IN_PICTURE_SETTINGS",
                        Uri.parse("package:$packageName")
                    )
                    startActivity(intent)
                }
            } else {
                Toast.makeText(
                    this,
                    "Feature not supported on this device",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        when (isFullscreen) {
            0 -> {
                if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
                    playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
                } else {
                    playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
                }
            }

            1 -> {
                playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
            }
        }




        scaleBtn.setOnClickListener {
            if (isFullscreen < 1) isFullscreen += 1 else isFullscreen = 0
            when (isFullscreen) {
                0 -> {
                    if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
                        playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
                    } else {
                        playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
                    }
                }

                1 -> {
                    playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL

                }
            }

            Snackbar.make(
                binding.exoPlayerView, (
                        when (isFullscreen) {
                            0 -> "Original"
                            1 -> "Stretch"
                            else -> "Original"
                        }
                        ), 1000
            ).show()
        }


        exoPlay.setOnClickListener {
            if (isInit) {
                if (model.player.isPlaying) pauseVideo()
                else playVideo()

            }
        }
        nextEpBtn.setOnClickListener {
            setNewEpisode(1)
        }
        prevEpBtn.setOnClickListener {
            setNewEpisode(-1)
        }

// Back Button
        playerView.findViewById<ImageButton>(R.id.exo_back).apply {
            setOnClickListener {
                model.player.release()
                finish()
            }
        }


    }


    private fun initPopupQuality(): Dialog {

        val trackSelectionDialogBuilder =
            com.google.android.exoplayer2.ui.TrackSelectionDialogBuilder(
                this,
                "Available Qualities",
                model.player,
                C.TRACK_TYPE_VIDEO
            )
        trackSelectionDialogBuilder.setTheme(R.style.DialogTheme)
        trackSelectionDialogBuilder.setTrackNameProvider {
            if (it.frameRate > 0f) it.height.toString() + "p" else it.height.toString() + "p (fps : N/A)"
        }
        val trackDialog = trackSelectionDialogBuilder.build()
        trackDialog.setOnDismissListener { hideSystemBars() }
        return trackDialog
    }


    override fun onUserLeaveHint() {
        // Handle leaving PiP mode using the home button
        finishAndRemoveTask()
    }

    private fun updateEpisodeName() {
        if (epCount != 1) {
            videoEpTextView.isSelected = true
            videoEpTextView.isSingleLine = true
            videoEpTextView.text = epListByName[currentEpIndex.toInt()].first

        } else {
            videoEpTextView.isSelected = true
            videoEpTextView.isSingleLine = true
            videoEpTextView.text = movieInfo!!.title
        }

    }

    private fun parseExtra() {
        animePlayingDetails =
            intent.getSerializableExtra("EXTRA_EPISODE_DATA") as FullMovieData
    }

    private fun onPiPChanged(isInPictureInPictureMode: Boolean) {
        playerView.useController = !isInPictureInPictureMode
        if (isInPictureInPictureMode) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            orientationListener?.disable()
        } else {
            mBackstackLost = true;
            orientationListener?.enable()
        }
        if (isInit) {
            saveData(
                "${animePlayingDetails.imageUrls}_${currentEpIndex}",
                model.player.currentPosition,
                this
            )
            hideSystemBars()
            model.player.play()
        }
    }


    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean) {
        onPiPChanged(isInPictureInPictureMode)
        super.onPictureInPictureModeChanged(isInPictureInPictureMode)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onPictureInPictureUiStateChanged(pipState: PictureInPictureUiState) {
        onPiPChanged(isInPictureInPictureMode)
        super.onPictureInPictureUiStateChanged(pipState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        onPiPChanged(isInPictureInPictureMode)
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
    }

    override fun onStop() {
        model.player.pause()
        super.onStop()

    }

    companion object {
        var sourceType = ""
        var pipStatus: Boolean = false
        var epCount: Int = 0
        var movieInfo: MovieInfo? = null
        var epListByName: ArrayList<Pair<String, String>> = arrayListOf()
        var epList: ArrayList<String> = arrayListOf()
        var currentEpIndex = 0
        private var isLocked: Boolean = false
        fun newIntent(
            context: Context,
            episodeData: FullMovieData,
        ): Intent {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("EXTRA_EPISODE_DATA", episodeData)
            return intent
        }
    }

    private fun playVideo() {
        model.player.play()
    }

    private fun pauseVideo() {
        model.player.pause()
    }


    public override fun onResume() {
        super.onResume()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        playerView.useController = true
        model.player.prepare()
    }

    public override fun onPause() {
        super.onPause()
        if (pipStatus) pauseVideo()
    }

    override fun finish() {
        if (mBackstackLost) {
            finishAndRemoveTask()
        } else {
            super.finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        pipStatus = false
        model.player.stop()
        model.player.release()
        finishAndRemoveTask()
    }

    private val keyMap: MutableMap<Int, (() -> Unit)?> = mutableMapOf(
        KeyEvent.KEYCODE_DPAD_RIGHT to null,
        KeyEvent.KEYCODE_DPAD_LEFT to null,
        KeyEvent.KEYCODE_SPACE to { exoPlay.performClick() },
        KeyEvent.KEYCODE_N to { nextEpBtn.performClick() },
        KeyEvent.KEYCODE_B to { prevEpBtn.performClick() }
    )

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return if (keyMap.containsKey(event.keyCode)) {
            (event.action == KeyEvent.ACTION_UP).also {
                if (isInit && it) keyMap[event.keyCode]?.invoke()
            }
        } else {
            super.dispatchKeyEvent(event)
        }
    }

    private fun ImageView.setImageViewEnabled(enabled: Boolean) = if (enabled) {
        drawable.clearColorFilter()
        isEnabled = true
        isFocusable = true
    } else {
        drawable.colorFilter = PorterDuffColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN)
        isEnabled = false
        isFocusable = false
    }

    @SuppressLint("ViewConstructor")
    class ExtendedTimeBar(
        context: Context,
        attrs: AttributeSet?
    ) : DefaultTimeBar(context, attrs) {
        private var enabled = false
        private var forceDisabled = false
        override fun setEnabled(enabled: Boolean) {
            this.enabled = enabled
            super.setEnabled(!forceDisabled && this.enabled)
        }

        fun setForceDisabled(forceDisabled: Boolean) {
            this.forceDisabled = forceDisabled
            isEnabled = enabled
        }

        private var previewBitmap: Bitmap? = null
        private val previewPaint = Paint()

        init {
            // Preview image paint settings
            previewPaint.isFilterBitmap = true
            // Load or generate the preview image
            // Replace R.drawable.preview_image with your actual image resource
            previewBitmap = BitmapFactory.decodeResource(resources, R.drawable.anim_rewind)
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            // Preview image display logic during seek
            val duration = 0L
            if (duration > 0) {
                val position = 3L
                val relativePos =
                    if (duration == 0L) 0f else (position.toFloat() / duration.toFloat())
                val width = width
                val previewWidth = previewBitmap?.width ?: 0
                val previewHeight = previewBitmap?.height ?: 0

                // Calculate the position to draw the preview image
                val previewX = (relativePos * width - previewWidth / 2).toInt()
                val previewY = height - previewHeight

                // Display the preview image
                previewBitmap?.let {
                    canvas.drawBitmap(it, previewX.toFloat(), previewY.toFloat(), previewPaint)
                }
            }
        }
    }
}

