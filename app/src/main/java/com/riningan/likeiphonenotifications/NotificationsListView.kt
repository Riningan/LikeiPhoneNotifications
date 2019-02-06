package com.riningan.likeiphonenotifications

import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.view.postDelayed
import kotlin.math.absoluteValue


class NotificationsListView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr), View.OnTouchListener, View.OnClickListener {
    private var mCv1Height = 0
    private var mCv1ClearHeight = 0
    private var mTv1CountHeight = 0
    private var mCv2Height = 0
    private var mCv2ClearHeight = 0
    private var mCv3Height = 0
    private var mCv3ClearHeight = 0
    private val mViewSeparatorHeight: Int
    private val mTvMoreHeight: Int

    private var mCvStartXOffset = 0f
    private var mCvXOffset = 0f
    private val mCvMaxXLeftOffset: Float
    private val mCvMaxXRightOffset: Float

    private var mIsCollapsed = true

    private var mNotifications = ArrayList<Notification>()

    private var mListener: OnNotificationsListener? = null


    init {
        LayoutInflater.from(context).inflate(R.layout.view_notifications_list, this)
        visibility = View.INVISIBLE

        calculateCardsSizes()
        mViewSeparatorHeight = viewSeparator.layoutParams.height
        mTvMoreHeight = tvMore.layoutParams.height + (tvMore.layoutParams as ConstraintLayout.LayoutParams).bottomMargin

        mCvMaxXLeftOffset = (tv1Open.layoutParams.width - tv1Open.paddingEnd).toFloat()
        mCvMaxXRightOffset = -1 * (tv1Remove.layoutParams.width - tv1Remove.paddingStart).toFloat()

        ll1.layoutParams.height = mCv1ClearHeight
        ll1.requestLayout()
        ll2.layoutParams.height = mCv2ClearHeight
        ll2.requestLayout()
        ll3.layoutParams.height = mCv3ClearHeight
        ll3.requestLayout()

        cv1.setOnTouchListener(this)
        tv1Open.setOnClickListener(this)
        tv1Remove.setOnClickListener(this)
        cv2.setOnTouchListener(this)
        tv2Open.setOnClickListener(this)
        tv2Remove.setOnClickListener(this)
        cv3.setOnTouchListener(this)
        tv3Open.setOnClickListener(this)
        tv3Remove.setOnClickListener(this)
        tvMore.setOnClickListener(this)
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (mIsCollapsed) {
            collapse(false)
        } else {
            expand(false)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(if (state != null && state is Bundle) {
            mIsCollapsed = state.getBoolean(STATE_IS_COLLAPSED)
            if (mIsCollapsed) {
                collapse(false)
            } else {
                expand(false)
            }
            state.getParcelable(STATE_SUPER)
        } else {
            state
        })
    }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable(STATE_SUPER, super.onSaveInstanceState())
        bundle.putBoolean(STATE_IS_COLLAPSED, mIsCollapsed)
        return bundle
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, event: MotionEvent): Boolean {
        if (!mIsCollapsed) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.clearAnimation()
                    view.setOnClickListener(this)
                    mCvStartXOffset = view.translationX
                    mCvXOffset = event.rawX
                }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    val offset = mCvStartXOffset + event.rawX - mCvXOffset
                    val targetX = if (offset > 0) {
                        if (offset > mCvMaxXLeftOffset / 2) {
                            mCvMaxXLeftOffset
                        } else {
                            0f
                        }
                    } else {
                        if (offset < mCvMaxXRightOffset / 2) {
                            mCvMaxXRightOffset
                        } else {
                            0f
                        }
                    }
                    view.animate().translationX(targetX).setListener(AnimatorSet().doOnEnd {
                        view.setOnClickListener(this)
                    }).duration = 100
                    mListener?.onNotificationSwipeEnd()
                }
                MotionEvent.ACTION_MOVE -> {
                    val offset = mCvStartXOffset + event.rawX - mCvXOffset
                    if (offset.absoluteValue > 5.fromDpToPx()) {
                        mListener?.onNotificationSwipeStart()
                        view.setOnClickListener(null)
                    }
                    view.translationX = if (offset > 0) Math.min(offset, mCvMaxXLeftOffset) else Math.max(offset, mCvMaxXRightOffset)
                }
            }
        }
        return false
    }

    override fun onClick(view: View) {
        when (view) {
            cv1 -> if (mNotifications.size == 1) {
                mListener?.onNotificationCardClick(0, mNotifications[0])
            } else {
                if (mIsCollapsed) {
                    expand(true)
                } else {
                    mListener?.onNotificationCardClick(0, mNotifications[0])
                }
            }
            cv2 -> mListener?.onNotificationCardClick(1, mNotifications[1])
            cv3 -> mListener?.onNotificationCardClick(2, mNotifications[2])
            tv1Open -> mListener?.onNotificationOpenClick(0, mNotifications[0])
            tv2Open -> mListener?.onNotificationOpenClick(1, mNotifications[1])
            tv3Open -> mListener?.onNotificationOpenClick(2, mNotifications[2])
            tv1Remove -> mListener?.onNotificationRemoveClick(0, mNotifications[0])
            tv2Remove -> mListener?.onNotificationRemoveClick(1, mNotifications[1])
            tv3Remove -> mListener?.onNotificationRemoveClick(2, mNotifications[2])
            tvMore -> mListener?.onNotificationsMoreClick()
        }
    }


    fun setListener(listener: OnNotificationsListener) {
        mListener = listener
    }


    fun setNotifications(notifications: List<Notification>) {
        val oldCount = mNotifications.size
        mNotifications.clear()
        mNotifications.addAll(notifications)
        if (mNotifications.size > 3) {
            tvMore.visibility = View.VISIBLE
            tvMore.text = context.getString(R.string.general_bottom_notifications_more, mNotifications.size - 3)
        } else {
            tvMore.visibility = View.INVISIBLE
            tvMore.text = ""
        }
        if (mNotifications.size > 2) {
            cv3.apply {
                alpha = 1f
                visibility = View.VISIBLE
                translationX = 0f
                setOnClickListener(this@NotificationsListView)
            }
            ll3.visibility = View.VISIBLE
            notifications[2].let {
                tvTitle3.text = it.title
                tvMain3.text = it.main
                tvSub3.text = it.sub
            }
        } else {
            cv3.apply {
                alpha = 0f
                visibility = View.INVISIBLE
                setOnClickListener(null)
            }
            ll3.visibility = View.INVISIBLE
        }
        if (mNotifications.size > 1) {
            tv1Count.text = context.getString(R.string.more, mNotifications.size - 1)
            cv2.apply {
                alpha = 1f
                visibility = View.VISIBLE
                translationX = 0f
                setOnClickListener(this@NotificationsListView)
            }
            ll2.visibility = View.VISIBLE
            notifications[1].let {
                tvTitle2.text = it.title
                tvMain2.text = it.main
                tvSub2.text = it.sub
            }
        } else {
            tv1Count.text = ""
            cv2.apply {
                alpha = 0f
                visibility = View.INVISIBLE
                setOnClickListener(null)
            }
            ll2.visibility = View.INVISIBLE
        }
        if (mNotifications.size > 0) {
            visibility = View.VISIBLE
            cv1.apply {
                alpha = 1f
                visibility = View.VISIBLE
                translationX = 0f
                setOnClickListener(this@NotificationsListView)
            }
            ll1.visibility = View.VISIBLE
            notifications[0].let {
                tvTitle1.text = it.title
                tvMain1.text = it.main
                tvSub1.text = it.sub
            }
        } else {
            visibility = View.INVISIBLE
            cv1.apply {
                alpha = 0f
                visibility = View.INVISIBLE
                setOnClickListener(null)
            }
            ll1.visibility = View.INVISIBLE
        }
        calculateCardsSizes()
        if (oldCount != mNotifications.size) {
            if (mIsCollapsed) {
                if (oldCount > 1 && mNotifications.size <= 1) {
                    cv1.apply {
                        layoutParams.height = mCv1ClearHeight
                        requestLayout()
                    }
                } else if (oldCount <= 1 && mNotifications.size > 1) {
                    cv1.apply {
                        layoutParams.height = mCv1ClearHeight + mTv1CountHeight
                        requestLayout()
                    }
                }
                setHeight(getCollapsedHeight(), false)
            } else {
                setHeight(getExpandedHeight(), false)
            }
        }
    }

    fun collapse(withAnimamation: Boolean) {
        setHeight(getCollapsedHeight(), withAnimamation)

        tv1Open.alpha = 0f
        tv1Remove.alpha = 0f
        cv1.apply {
            clearAnimation()
            translationX = 0f
        }
        val cv1Height = mCv1ClearHeight + if (mNotifications.size > 1) mTv1CountHeight else 0
        if (withAnimamation) {
            cv1.startAnimation(ResizeHeightAnimation(cv1, cv1Height))
            if (tv1Count.alpha < 1f) {
                tv1Count.animate().alpha(1f).duration = ANIMATION_DURATION
            }
        } else {
            cv1.apply {
                layoutParams.height = cv1Height
                requestLayout()
            }
            tv1Count.alpha = 1f
        }

        tv2Open.alpha = 0f
        tv2Remove.alpha = 0f
        cv2.apply {
            clearAnimation()
            translationX = 0f
            startAnimation(AnimationSet(true).apply {
                fillAfter = true
                duration = if (withAnimamation) ANIMATION_DURATION else 0
                addAnimation(TranslateAnimation(0f, CV2_TRANSLATE_X, 0f, (CV2_OFFSET_X - mCv1Height).toFloat()))
                addAnimation(ScaleAnimation(1f, CV2_SCALE, 1f, CV2_SCALE))
            })
        }

        tv3Open.alpha = 0f
        tv3Remove.alpha = 0f
        cv3.apply {
            clearAnimation()
            translationX = 0f
            startAnimation(AnimationSet(true).apply {
                fillAfter = true
                duration = if (withAnimamation) ANIMATION_DURATION else 0
                addAnimation(TranslateAnimation(0f, CV3_TRANSLATE_X, 0f, (CV3_OFFSET_X - mCv1Height - mCv2Height).toFloat()))
                addAnimation(ScaleAnimation(1f, CV3_SCALE, 1f, CV3_SCALE))
            })
        }

        mIsCollapsed = true
        mListener?.onNotificationsCollapsed(withAnimamation)
    }

    fun expand(withAnimamation: Boolean) {
        setHeight(getExpandedHeight(), withAnimamation)

        tv1Open.alpha = 1f
        tv1Remove.alpha = 1f
        cv1.clearAnimation()
        if (withAnimamation) {
            cv1.startAnimation(ResizeHeightAnimation(cv1, mCv1ClearHeight))
            if (tv1Count.alpha > 0) {
                tv1Count.animate().alpha(0f).duration = ANIMATION_DURATION
            }
        } else {
            cv1.apply {
                layoutParams.height = mCv1ClearHeight
                requestLayout()
            }
            tv1Count.alpha = 0f
        }

        cv2.apply {
            clearAnimation()
            startAnimation(AnimationSet(true).apply {
                fillAfter = true
                duration = if (withAnimamation) ANIMATION_DURATION else 0
                addAnimation(TranslateAnimation(CV2_TRANSLATE_X, 0f, (CV2_OFFSET_X - mCv1Height).toFloat(), 0f))
                addAnimation(ScaleAnimation(CV2_SCALE, 1f, CV2_SCALE, 1f))
                doOnEnd {
                    this@NotificationsListView.ll2.postDelayed(ANIMATION_DURATION / 4) {
                        this@NotificationsListView.tv2Open.alpha = 1f
                        this@NotificationsListView.tv2Remove.alpha = 1f
                    }
                }
            })
        }

        cv3.apply {
            clearAnimation()
            startAnimation(AnimationSet(true).apply {
                fillAfter = true
                duration = if (withAnimamation) ANIMATION_DURATION else 0
                addAnimation(TranslateAnimation(CV3_TRANSLATE_X, 0f, (CV3_OFFSET_X - mCv1Height - mCv2Height).toFloat(), 0f))
                addAnimation(ScaleAnimation(CV3_SCALE, 1f, CV3_SCALE, 1f))
                doOnEnd {
                    this@NotificationsListView.ll3.postDelayed(ANIMATION_DURATION / 4) {
                        this@NotificationsListView.tv3Open.alpha = 1f
                        this@NotificationsListView.tv3Remove.alpha = 1f
                    }
                }
            })
        }

        mIsCollapsed = false
        mListener?.onNotificationsExpanded(withAnimamation)
    }


    private fun getCollapsedHeight() = when (mNotifications.size) {
        0 -> mViewSeparatorHeight
        1 -> mCv1Height + mViewSeparatorHeight
        else -> mCv1Height + mTv1CountHeight + mViewSeparatorHeight
    }

    private fun getExpandedHeight() = when (mNotifications.size) {
        0 -> mViewSeparatorHeight
        1 -> mCv1Height + mViewSeparatorHeight
        2 -> mCv1Height + mCv2Height + mViewSeparatorHeight
        3 -> mCv1Height + mCv2Height + mCv3Height + mViewSeparatorHeight
        else -> mCv1Height + mCv2Height + mCv3Height + mViewSeparatorHeight + mTvMoreHeight
    }

    private fun setHeight(newHeight: Int, withAnimamation: Boolean) {
        if (measuredHeight == newHeight) {
            return
        }
        clearAnimation()
        if (withAnimamation) {
            startAnimation(ResizeHeightAnimation(this, newHeight))
        } else {
            layoutParams.height = newHeight
            requestLayout()
        }
    }

    private fun calculateCardsSizes() {
        val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(SCREEN_WIDTH, View.MeasureSpec.AT_MOST)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        // cv1
        tvSub1.measure(widthMeasureSpec, heightMeasureSpec)
        mCv1ClearHeight = cl1.paddingTop +
                tvTime1.layoutParams.height +
                (tvMain1.layoutParams as ConstraintLayout.LayoutParams).topMargin +
                tvMain1.layoutParams.height +
                (tvSub1.layoutParams as ConstraintLayout.LayoutParams).topMargin +
                tvSub1.measuredHeight +
                cl1.paddingBottom
        mCv1Height = (cv1.layoutParams as ConstraintLayout.LayoutParams).topMargin + mCv1ClearHeight
        mTv1CountHeight = (tv1Count.layoutParams as ConstraintLayout.LayoutParams).topMargin + tv1Count.layoutParams.height
        // cv2
        tvSub2.measure(widthMeasureSpec, heightMeasureSpec)
        mCv2ClearHeight = cl2.paddingTop +
                tvTime2.layoutParams.height +
                (tvMain2.layoutParams as ConstraintLayout.LayoutParams).topMargin +
                tvMain2.layoutParams.height +
                (tvSub2.layoutParams as ConstraintLayout.LayoutParams).topMargin +
                tvSub2.measuredHeight +
                cl2.paddingBottom
        mCv2Height = (cv2.layoutParams as ConstraintLayout.LayoutParams).topMargin + mCv2ClearHeight
        // cv3
        tvSub3.measure(widthMeasureSpec, heightMeasureSpec)
        mCv3ClearHeight = cl3.paddingTop +
                tvTime3.layoutParams.height +
                (tvMain3.layoutParams as ConstraintLayout.LayoutParams).topMargin +
                tvMain3.layoutParams.height +
                (tvSub3.layoutParams as ConstraintLayout.LayoutParams).topMargin +
                tvSub3.measuredHeight +
                cl3.paddingBottom
        mCv3Height = (cv3.layoutParams as ConstraintLayout.LayoutParams).topMargin + mCv3ClearHeight
    }


    interface OnNotificationsListener {
        fun onNotificationCardClick(index: Int, notification: Notification)
        fun onNotificationOpenClick(index: Int, notification: Notification)
        fun onNotificationRemoveClick(index: Int, notification: Notification)
        fun onNotificationsMoreClick()
        fun onNotificationSwipeStart()
        fun onNotificationSwipeEnd()
        fun onNotificationsCollapsed(withAnimamation: Boolean)
        fun onNotificationsExpanded(withAnimamation: Boolean)
    }


    companion object {
        private const val STATE_IS_COLLAPSED = "isCollapsed"
        private const val STATE_SUPER = "superState"
        private var SCREEN_WIDTH = DeviceUtil.getScreenWidth()
        private var CV2_TRANSLATE_X = ((SCREEN_WIDTH - (14 * 2).fromDpToPx()) / 40).toFloat()
        private var CV2_SCALE = 0.95f
        private var CV2_OFFSET_X = 7.fromDpToPx()
        private var CV3_TRANSLATE_X = ((SCREEN_WIDTH - (14 * 2).fromDpToPx()) / 20).toFloat()
        private var CV3_SCALE = 0.9f
        private var CV3_OFFSET_X = 2.fromDpToPx()
    }
}