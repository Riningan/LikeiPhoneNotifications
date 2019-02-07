package com.riningan.likeiphonenotifications.widget.item

import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import com.riningan.likeiphonenotifications.widget.ANIMATION_DURATION
import com.riningan.likeiphonenotifications.widget.NotificationsListView
import com.riningan.likeiphonenotifications.widget.animation.ResizeHeightAnimation
import com.riningan.likeiphonenotifications.widget.fromDpToPx


class NotificationItemFirst(cv: CardView, ll: LinearLayoutCompat, mNotificationsListView: NotificationsListView) : NotificationItem(cv, ll, mNotificationsListView) {
    init {
        cv.cardElevation = 4.fromDpToPx().toFloat()
    }


    override fun collapse(withAnimation: Boolean) {
        super.collapse(withAnimation)
        val height = mNotificationsListView.run {
            cvHeight + if (notifications.size > 1) tvCountHeight else 0
        }
        if (withAnimation) {
            cv.startAnimation(ResizeHeightAnimation(cv, height))
            if (tvCount.alpha < 1f) {
                tvCount.animate().alpha(1f).duration = ANIMATION_DURATION
            }
        } else {
            cv.apply {
                layoutParams.height = height
                requestLayout()
            }
            tvCount.alpha = 1f
        }
    }

    override fun expand(withAnimation: Boolean) {
        tvLeft.alpha = 1f
        tvRight.alpha = 1f
        super.expand(withAnimation)
        if (withAnimation) {
            cv.startAnimation(ResizeHeightAnimation(cv, mNotificationsListView.cvHeight))
            if (tvCount.alpha > 0) {
                tvCount.animate().alpha(0f).duration = ANIMATION_DURATION
            }
        } else {
            cv.apply {
                layoutParams.height = mNotificationsListView.cvHeight
                requestLayout()
            }
            tvCount.alpha = 0f
        }
    }
}