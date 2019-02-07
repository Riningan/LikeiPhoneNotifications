package com.riningan.likeiphonenotifications.widget

interface Notification {
    fun getTitleText() : String
    fun getSubTitleText() : String
    fun getDescriptionText() : String
    fun getTimeText() : String
}