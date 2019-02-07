package com.riningan.likeiphonenotifications.widget

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager
import android.view.animation.Animation
import com.riningan.likeiphonenotifications.App


fun getScreenWidth(): Int {
    val windowManager = App.getContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getRealMetrics(displayMetrics)
    return displayMetrics.widthPixels
}

fun getScreenHeight(): Int {
    val windowManager = App.getContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getRealMetrics(displayMetrics)
    return displayMetrics.heightPixels
}

fun Int.fromDpToPx(): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), App.getContext().resources.displayMetrics).toInt()
}

inline fun Animation.doOnEnd(crossinline action: (animation: Animation) -> Unit) =
        addListener(onEnd = action)

inline fun Animation.addListener(crossinline onEnd: (animation: Animation) -> Unit = {},
                                 crossinline onStart: (animation: Animation) -> Unit = {},
                                 crossinline onRepeat: (animation: Animation) -> Unit = {}) {
    val listener = object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation) = onRepeat(animation)
        override fun onAnimationEnd(animation: Animation) = onEnd(animation)
        override fun onAnimationStart(animation: Animation) = onStart(animation)
    }
    setAnimationListener(listener)
}