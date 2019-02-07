package com.riningan.likeiphonenotifications.widget.item

import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import androidx.core.view.postDelayed
import com.riningan.likeiphonenotifications.App
import com.riningan.likeiphonenotifications.R
import com.riningan.likeiphonenotifications.widget.*


class NotificationItemSecond(cv: CardView, ll: LinearLayoutCompat, mNotificationsListView: NotificationsListView) : NotificationItem(cv, ll, mNotificationsListView) {
    init {
        cv.cardElevation = 3.fromDpToPx().toFloat()
    }


    override fun collapse(withAnimation: Boolean) {
        super.collapse(withAnimation)
        cv.startAnimation(AnimationSet(true).apply {
            fillAfter = true
            duration = if (withAnimation) ANIMATION_DURATION else 0
            addAnimation(TranslateAnimation(0f, TRANSLATE_X, 0f, (OFFSET_Y - mNotificationsListView.cvHeightWithTopMargin).toFloat()))
            addAnimation(ScaleAnimation(1f, SCALE, 1f, SCALE))
        })
    }

    override fun expand(withAnimation: Boolean) {
        super.expand(withAnimation)
        cv.startAnimation(AnimationSet(true).apply {
            fillAfter = true
            duration = if (withAnimation) ANIMATION_DURATION else 0
            addAnimation(TranslateAnimation(TRANSLATE_X, 0f, (OFFSET_Y - mNotificationsListView.cvHeightWithTopMargin).toFloat(), 0f))
            addAnimation(ScaleAnimation(SCALE, 1f, SCALE, 1f))
            doOnEnd {
                ll.postDelayed(ANIMATION_DURATION / 4) {
                    tvLeft.alpha = 1f
                    tvRight.alpha = 1f
                }
            }
        })
    }


    companion object {
        private var TRANSLATE_X = (getScreenWidth() - App.getContext().resources.getDimension(R.dimen.left_offset) - App.getContext().resources.getDimension(R.dimen.right_offset)) / 40
        private var SCALE = 0.95f
        private var OFFSET_Y = 7.fromDpToPx()
    }
}