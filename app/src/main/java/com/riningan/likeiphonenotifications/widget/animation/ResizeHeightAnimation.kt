package com.riningan.likeiphonenotifications.widget.animation

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import com.riningan.likeiphonenotifications.widget.ANIMATION_DURATION


class ResizeHeightAnimation(private val mView: View, private val mTargetHeight: Int, private val mStartHeight: Int = mView.measuredHeight) : Animation() {
    init {
        duration = ANIMATION_DURATION
    }


    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        if (mView.height != mTargetHeight) {
            val newHeight = (mStartHeight + ((mTargetHeight - mStartHeight) * interpolatedTime))
            mView.layoutParams.height = newHeight.toInt()
            mView.requestLayout()
        }
    }

    override fun cancel() {
        super.cancel()
        mView.layoutParams.height = mTargetHeight
        mView.requestLayout()
    }

    override fun willChangeBounds() = true
}