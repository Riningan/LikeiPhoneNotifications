@file:Suppress("LeakingThis")

package com.riningan.likeiphonenotifications.widget.item

import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import com.riningan.likeiphonenotifications.R
import com.riningan.likeiphonenotifications.widget.Notification
import com.riningan.likeiphonenotifications.widget.NotificationsListView
import com.riningan.likeiphonenotifications.widget.fromDpToPx
import kotlin.math.absoluteValue


@SuppressLint("ClickableViewAccessibility")
abstract class NotificationItem(val cv: CardView, val ll: LinearLayoutCompat, val mNotificationsListView: NotificationsListView) : View.OnTouchListener {
    val cl: ConstraintLayout = cv.findViewById(R.id.cl)
    val tvTitle: TextView = cv.findViewById(R.id.tvTitle)
    val tvMain: TextView = cv.findViewById(R.id.tvMain)
    val tvSub: TextView = cv.findViewById(R.id.tvSub)
    private val tvTime: TextView = cv.findViewById(R.id.tvTime)
    val tvCount: TextView = cv.findViewById(R.id.tvCount)
    val tvLeft: TextView = ll.findViewById(R.id.tvLeft)
    val tvRight: TextView = ll.findViewById(R.id.tvRight)

    private var mCvStartXOffset = 0f
    private var mCvXOffset = 0f

    private var mNotification: Notification? = null


    init {
        cv.setOnTouchListener(this)
        tvLeft.setOnClickListener(mNotificationsListView)
        tvRight.setOnClickListener(mNotificationsListView)
    }


    override fun onTouch(view: View, event: MotionEvent): Boolean {
        if (!mNotificationsListView.isCollapsed) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.clearAnimation()
                    view.setOnClickListener(mNotificationsListView)
                    mCvStartXOffset = view.translationX
                    mCvXOffset = event.rawX
                }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    val offset = mCvStartXOffset + event.rawX - mCvXOffset
                    val targetX = if (offset > 0) {
                        if (offset > mNotificationsListView.cvMaxXLeftDragOffset / 2) {
                            mNotificationsListView.cvMaxXLeftDragOffset
                        } else {
                            0f
                        }
                    } else {
                        if (offset < mNotificationsListView.cvMaxXRightDragOffset / 2) {
                            mNotificationsListView.cvMaxXRightDragOffset
                        } else {
                            0f
                        }
                    }
                    view.animate().translationX(targetX).setListener(AnimatorSet().doOnEnd {
                        view.setOnClickListener(mNotificationsListView)
                    }).duration = 100
                    mNotificationsListView.listener?.onSwipeEnd(mNotification!!)
                }
                MotionEvent.ACTION_MOVE -> {
                    val offset = mCvStartXOffset + event.rawX - mCvXOffset
                    if (offset.absoluteValue > 5.fromDpToPx()) {
                        mNotificationsListView.listener?.onSwipeStart(mNotification!!)
                        view.setOnClickListener(null)
                    }
                    view.translationX = if (offset > 0) {
                        Math.min(offset, mNotificationsListView.cvMaxXLeftDragOffset)
                    } else {
                        Math.max(offset, mNotificationsListView.cvMaxXRightDragOffset)
                    }
                }
            }
        }
        return false
    }


    open fun collapse(withAnimation: Boolean) {
        tvLeft.alpha = 0f
        tvRight.alpha = 0f
        cv.apply {
            clearAnimation()
            translationX = 0f
        }
    }

    open fun expand(withAnimation: Boolean) {
        cv.clearAnimation()
    }


    fun setNotification(notification: Notification?) {
        mNotification = notification
        if (notification == null) {
            cv.apply {
                alpha = 0f
                visibility = View.INVISIBLE
            }
            ll.visibility = View.INVISIBLE
            cv.setOnClickListener(null)
        } else {
            cv.apply {
                alpha = 1f
                visibility = View.VISIBLE
                translationX = 0f
            }
            ll.visibility = View.VISIBLE
            tvTitle.text = notification.getTitleText()
            tvMain.text = notification.getSubTitleText()
            tvSub.text = notification.getDescriptionText()
            tvTime.text = notification.getTimeText()
            cv.setOnClickListener(mNotificationsListView)
        }
    }


    fun setHeight(height: Int) {
        cv.layoutParams.height = height
        ll.layoutParams.height = height
    }
}