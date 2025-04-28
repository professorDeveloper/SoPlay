package com.azamovme.soplay.data

import java.io.Serializable

data class PlayerSettings(
    //Video
    var videoInfo: Boolean = true,
    var defaultSpeed: Int = 5,
    var cursedSpeeds: Boolean = false,
    var resize: Int = 0,

    //Subtitles
    var subtitles: Boolean = true,
    var primaryColor: Int = 4,
    var secondaryColor: Int = 0,
    var outline: Int = 0,
    var subBackground: Int = 0,
    var subWindow: Int = 0,
    var font: Int = 0,
    var fontSize: Int = 20,
    var locale: Int = 2,

    //TimeStamps
    var timeStampsEnabled: Boolean = true,
    var useProxyForTimeStamps: Boolean = true,
    var showTimeStampButton: Boolean = true,

    //Auto
    var autoSkipOPED: Boolean = false,
    var autoPlay: Boolean = true,
    var autoSkipFiller: Boolean = false,

    //Update Progress
    var askIndividual: Boolean = true,
    var updateForH: Boolean = false,
    var watchPercentage: Float = 0.8f,

    //Behaviour
    var alwaysContinue: Boolean = true,
    var focusPause: Boolean = true,
    var gestures: Boolean = true,
    var doubleTap: Boolean = true,
    var seekTime: Int = 10,
    var skipTime: Int = 85,

    //Other
    var cast: Boolean = false,
    var pip: Boolean = true
) : Serializable
data class UserInterfaceSettings(
    var darkMode: Boolean? = null,
    var showYtButton: Boolean = true,
    var animeDefaultView: Int = 0,
    var mangaDefaultView: Int = 0,

    //App
    var immersiveMode: Boolean = false,
    var smallView: Boolean = true,
    var defaultStartUpTab: Int = 1,
    var homeLayoutShow: MutableList<Boolean> = mutableListOf(true, false, false, true, false, false, true),

    //Animations
    var bannerAnimations: Boolean = true,
    var layoutAnimations: Boolean = true,
    var animationSpeed: Float = 1f

) : Serializable