package com.riningan.likeiphonenotifications

import android.app.Application


class App: Application() {
    override fun onCreate() {
        super.onCreate()
        mInstance = this
    }


    companion object {
        private lateinit var mInstance: App

        fun getContext() = mInstance
    }
}