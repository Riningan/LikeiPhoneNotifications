package com.riningan.likeiphonenotifications.widget

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.riningan.likeiphonenotifications.R
import com.riningan.likeiphonenotifications.widget.animation.ResizeHeightAnimation
import com.riningan.likeiphonenotifications.widget.animation.ResizeWidthAnimation
import com.riningan.likeiphonenotifications.widget.item.NotificationItem
import com.riningan.likeiphonenotifications.widget.item.NotificationItemFirst
import com.riningan.likeiphonenotifications.widget.item.NotificationItemSecond
import com.riningan.likeiphonenotifications.widget.item.NotificationItemThird
import kotlinx.android.synthetic.main.view_notifications_list.view.*


class NotificationsListView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr), View.OnClickListener {
    private val mItem1: NotificationItem
    private val mItem2: NotificationItem
    private val mItem3: NotificationItem

    private val mTvHeaderHeight: Int
    private val mLlCollapseCollapsedWidth: Int
    private val mLlCollapseExpandedWidth: Int
    private val mLlRemoveAllCollapsedWidth: Int
    private val mLlRemoveAllExpandedWidth: Int
    internal val cvHeight: Int
    internal val cvHeightWithTopMargin: Int
    internal val tvCountHeight: Int
    private val mViewSeparatorHeight: Int
    private val mTvMoreHeight: Int

    internal val cvMaxXLeftDragOffset: Float
    internal val cvMaxXRightDragOffset: Float

    internal var isCollapsed = true
    internal var notifications = ArrayList<Notification>()
    internal var listener: OnNotificationsListener? = null


    init {
        LayoutInflater.from(context).inflate(R.layout.view_notifications_list, this)
        val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(getScreenWidth(), View.MeasureSpec.AT_MOST)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        mItem1 = NotificationItemFirst(cv1 as CardView, ll1 as LinearLayoutCompat, this)
        mItem2 = NotificationItemSecond(cv2 as CardView, ll2 as LinearLayoutCompat, this)
        mItem3 = NotificationItemThird(cv3 as CardView, ll3 as LinearLayoutCompat, this)

        mTvHeaderHeight = (tvHeader.layoutParams as ConstraintLayout.LayoutParams).topMargin + tvHeader.layoutParams.height

        tvCollapse.measure(widthMeasureSpec, heightMeasureSpec)
        mLlCollapseCollapsedWidth = llCollapse.layoutParams.height
        mLlCollapseExpandedWidth = mLlCollapseCollapsedWidth +
                tvCollapse.measuredWidth +
                (tvCollapse.layoutParams as LinearLayoutCompat.LayoutParams).rightMargin
        llCollapse.setOnClickListener(this)

        tvRemoveAll.measure(widthMeasureSpec, heightMeasureSpec)
        mLlRemoveAllCollapsedWidth = llRemoveAll.layoutParams.height
        mLlRemoveAllExpandedWidth = mLlRemoveAllCollapsedWidth +
                tvRemoveAll.measuredWidth
        llRemoveAll.setOnClickListener(this)

        mItem1.tvMain.measure(widthMeasureSpec, heightMeasureSpec)
        mItem1.tvSub.measure(widthMeasureSpec, heightMeasureSpec)
        cvHeight = mItem1.cl.paddingTop +
                mItem1.tvTitle.layoutParams.height +
                (mItem1.tvMain.layoutParams as ConstraintLayout.LayoutParams).topMargin +
                mItem1.tvMain.measuredHeight +
                (mItem1.tvSub.layoutParams as ConstraintLayout.LayoutParams).topMargin +
                mItem1.tvSub.measuredHeight +
                mItem1.cl.paddingBottom
        cvHeightWithTopMargin = (mItem1.cv.layoutParams as ConstraintLayout.LayoutParams).topMargin +
                cvHeight
        mItem1.tvCount.measure(widthMeasureSpec, heightMeasureSpec)
        tvCountHeight = (mItem1.tvCount.layoutParams as ConstraintLayout.LayoutParams).topMargin +
                mItem1.tvCount.measuredHeight
        cvMaxXLeftDragOffset = (mItem1.tvLeft.layoutParams.width - mItem1.tvLeft.paddingRight).toFloat()
        cvMaxXRightDragOffset = -1 * (mItem1.tvRight.layoutParams.width - mItem1.tvRight.paddingLeft).toFloat()

        mItem1.setHeight(cvHeight)
        mItem2.setHeight(cvHeight)
        mItem3.setHeight(cvHeight)

        mTvMoreHeight = tvMore.layoutParams.height + (tvMore.layoutParams as ConstraintLayout.LayoutParams).bottomMargin

        mViewSeparatorHeight = viewSeparator.layoutParams.height
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isCollapsed) {
            collapse(false)
        } else {
            expand(false)
        }
        collapseRemoveAll(false)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(if (state != null && state is Bundle) {
            isCollapsed = state.getBoolean(STATE_IS_COLLAPSED)
            if (isCollapsed) {
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
        bundle.putBoolean(STATE_IS_COLLAPSED, isCollapsed)
        return bundle
    }

    override fun onClick(view: View) {
        when (view) {
            mItem1.cv -> {
                if (notifications.size == 1) {
                    listener?.onCardClick(0, notifications[0])
                } else {
                    if (isCollapsed) {
                        if (llRemoveAll.layoutParams.width == mLlRemoveAllExpandedWidth) {
                            collapseRemoveAll(true)
                        }
                        expand(true)
                    } else {
                        listener?.onCardClick(0, notifications[0])
                    }
                }
            }
            mItem2.cv -> listener?.onCardClick(1, notifications[1])
            mItem3.cv -> listener?.onCardClick(2, notifications[2])
            mItem1.tvLeft -> listener?.onLeftClick(0, notifications[0])
            mItem2.tvLeft -> listener?.onLeftClick(1, notifications[1])
            mItem3.tvLeft -> listener?.onLeftClick(2, notifications[2])
            mItem1.tvRight -> listener?.onRightClick(0, notifications[0])
            mItem2.tvRight -> listener?.onRightClick(1, notifications[1])
            mItem3.tvRight -> listener?.onRightClick(2, notifications[2])
            tvMore -> listener?.onMoreClick()
            llCollapse -> {
                collapse(true)
                collapseRemoveAll(true)
            }
            llRemoveAll -> {
                if (llRemoveAll.layoutParams.width < mLlRemoveAllExpandedWidth) {
                    expandRemoveAll(true)
                } else {
                    collapseRemoveAll(true)
                    listener?.onRemoveAllClick()
                }
            }
        }
    }


    fun setListener(listener: OnNotificationsListener) {
        this.listener = listener
    }


    fun setNotifications(list: List<Notification>) {
        val oldCount = notifications.size
        notifications.clear()
        notifications.addAll(list)
        if (notifications.size > 3) {
            tvMore.visibility = View.VISIBLE
            tvMore.text = context.getString(R.string.more, notifications.size - 3)
        } else {
            tvMore.visibility = View.INVISIBLE
            tvMore.text = ""
        }
        if (notifications.size > 2) {
            mItem3.setNotification(notifications[2])
        } else {
            mItem3.setNotification(null)
        }
        if (notifications.size > 1) {
            collapseRemoveAll(false)
            llRemoveAll.visibility = View.VISIBLE
            if (!isCollapsed) {
                showCollapse(false)
            }
            mItem1.tvCount.text = context.getString(R.string.more, notifications.size - 1)
            mItem2.setNotification(notifications[1])
        } else {
            llRemoveAll.visibility = View.INVISIBLE
            hideCollapse(false)
            mItem1.tvCount.text = ""
            mItem2.setNotification(null)
        }
        if (notifications.size > 0) {
            mItem1.setNotification(notifications[0])
        } else {
            mItem1.setNotification(null)
        }
        if (oldCount != notifications.size) {
            if (isCollapsed) {
                if (notifications.size == 1) {
                    mItem1.cv.apply {
                        layoutParams.height = cvHeight
                        requestLayout()
                    }
                    mItem1.expand(false)
                    isCollapsed = true
                } else if (oldCount > 1 && notifications.size <= 1) {
                    mItem1.cv.apply {
                        layoutParams.height = cvHeight
                        requestLayout()
                    }
                } else if (oldCount <= 1 && notifications.size > 1) {
                    mItem1.cv.apply {
                        layoutParams.height = cvHeight + tvCountHeight
                        requestLayout()
                    }
                }
                setHeight(getCollapsedHeight(), false)
            } else {
                setHeight(getExpandedHeight(), false)
            }
        }
    }


    private fun collapse(withAnimation: Boolean) {
        setHeight(getCollapsedHeight(), withAnimation)
        mItem1.collapse(withAnimation)
        mItem2.collapse(withAnimation)
        mItem3.collapse(withAnimation)
        hideCollapse(withAnimation)
        isCollapsed = true
        listener?.onCollapsed(withAnimation)
    }

    private fun expand(withAnimation: Boolean) {
        setHeight(getExpandedHeight(), withAnimation)
        mItem1.expand(withAnimation)
        mItem2.expand(withAnimation)
        mItem3.expand(withAnimation)
        isCollapsed = false
        if (notifications.size > 1) {
            showCollapse(withAnimation)
        }
        listener?.onExpanded(withAnimation)
    }


    private fun showCollapse(withAnimation: Boolean) {
        llCollapse.isEnabled = true
        setCollapseAlpha(1f, withAnimation)
    }

    private fun hideCollapse(withAnimation: Boolean) {
        llCollapse.isEnabled = false
        setCollapseAlpha(0f, withAnimation)
    }

    private fun setCollapseAlpha(alpha: Float, withAnimation: Boolean) {
        tvCollapse.clearAnimation()
        ivCollapse.clearAnimation()
        if (withAnimation) {
            if (llCollapse.alpha != alpha) {
                llCollapse.animate().alpha(alpha).duration = ANIMATION_DURATION
                tvCollapse.animate().alpha(alpha).duration = ANIMATION_DURATION
                ivCollapse.animate().alpha(alpha).duration = ANIMATION_DURATION
            }
        } else {
            llCollapse.alpha = alpha
            tvCollapse.alpha = alpha
            ivCollapse.alpha = alpha
        }
    }


    private fun expandRemoveAll(withAnimation: Boolean) {
        if (withAnimation) {
            llCollapse.startAnimation(ResizeWidthAnimation(llCollapse, mLlCollapseCollapsedWidth))
            llRemoveAll.startAnimation(ResizeWidthAnimation(llRemoveAll, mLlRemoveAllExpandedWidth))
            ivRemoveAll.animate().alpha(0f).duration = ANIMATION_DURATION
            tvRemoveAll.animate().translationX((-16).fromDpToPx().toFloat()).duration = ANIMATION_DURATION
        } else {
            llCollapse.apply {
                layoutParams.width = mLlCollapseCollapsedWidth
                requestLayout()
            }
            llRemoveAll.apply {
                layoutParams.width = mLlRemoveAllExpandedWidth
                requestLayout()
            }
            ivRemoveAll.alpha = 0f
            tvRemoveAll.translationX = (-16).fromDpToPx().toFloat()
        }
    }

    private fun collapseRemoveAll(withAnimation: Boolean) {
        if (withAnimation) {
            llCollapse.startAnimation(ResizeWidthAnimation(llCollapse, mLlCollapseExpandedWidth))
            llRemoveAll.startAnimation(ResizeWidthAnimation(llRemoveAll, mLlRemoveAllCollapsedWidth))
            ivRemoveAll.animate().alpha(1f).duration = ANIMATION_DURATION
            tvRemoveAll.animate().translationX(16.fromDpToPx().toFloat()).duration = ANIMATION_DURATION
        } else {
            llCollapse.apply {
                layoutParams.width = mLlCollapseExpandedWidth
                requestLayout()
            }
            llRemoveAll.apply {
                layoutParams.width = mLlRemoveAllCollapsedWidth
                requestLayout()
            }
            ivRemoveAll.alpha = 1f
            tvRemoveAll.translationX = 16.fromDpToPx().toFloat()
        }
    }


    private fun getCollapsedHeight() = mTvHeaderHeight + when (notifications.size) {
        0 -> mViewSeparatorHeight
        1 -> cvHeightWithTopMargin + mViewSeparatorHeight
        else -> cvHeightWithTopMargin + tvCountHeight + mViewSeparatorHeight
    }

    private fun getExpandedHeight() = mTvHeaderHeight + when (notifications.size) {
        0 -> mViewSeparatorHeight
        1 -> cvHeightWithTopMargin + mViewSeparatorHeight
        2 -> cvHeightWithTopMargin + cvHeightWithTopMargin + mViewSeparatorHeight
        3 -> cvHeightWithTopMargin + cvHeightWithTopMargin + cvHeightWithTopMargin + mViewSeparatorHeight
        else -> cvHeightWithTopMargin + cvHeightWithTopMargin + cvHeightWithTopMargin + mViewSeparatorHeight + mTvMoreHeight
    }


    private fun setHeight(newHeight: Int, withAnimation: Boolean) {
        if (measuredHeight == newHeight) {
            return
        }
        clearAnimation()
        if (withAnimation) {
            startAnimation(ResizeHeightAnimation(this, newHeight))
        } else {
            layoutParams.height = newHeight
            requestLayout()
        }
    }


    interface OnNotificationsListener {
        fun onCardClick(index: Int, notification: Notification)
        fun onLeftClick(index: Int, notification: Notification)
        fun onRightClick(index: Int, notification: Notification)
        fun onSwipeStart(notification: Notification)
        fun onSwipeEnd(notification: Notification)
        fun onRemoveAllClick()
        fun onMoreClick()
        fun onCollapsed(withAnimamation: Boolean)
        fun onExpanded(withAnimamation: Boolean)
    }


    companion object {
        private const val STATE_IS_COLLAPSED = "isCollapsed"
        private const val STATE_SUPER = "superState"
    }
}