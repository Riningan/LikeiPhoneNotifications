package com.riningan.likeiphonenotifications.widget.animation

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import com.riningan.likeiphonenotifications.widget.ANIMATION_DURATION


class ResizeWidthAnimation(private val mView: View, private val mTargetWidth: Int, private val mStartWidth: Int = mView.measuredWidth) : Animation() {
    init {
        duration = ANIMATION_DURATION
    }


    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        if (mView.width != mTargetWidth) {
            val newWidth = (mStartWidth + ((mTargetWidth - mStartWidth) * interpolatedTime))
            mView.layoutParams.width = newWidth.toInt()
            mView.requestLayout()
        }
    }

    override fun cancel() {
        super.cancel()
        mView.layoutParams.width = mTargetWidth
        mView.requestLayout()
    }


    override fun willChangeBounds() = true
}