package com.riningan.likeiphonenotifications

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.riningan.likeiphonenotifications.widget.Notification
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val mNotification1 = object : Notification {
        override fun getTitleText() = "Title 1"
        override fun getSubTitleText() = "SubTitle 1"
        override fun getDescriptionText() = "Description 1"
        override fun getTimeText() = "10:10"
    }
    private val mNotification2 = object : Notification {
        override fun getTitleText() = "Title 2"
        override fun getSubTitleText() = "SubTitle 2"
        override fun getDescriptionText() = "Description 2"
        override fun getTimeText() = "10:10"
    }
    private val mNotification3 = object : Notification {
        override fun getTitleText() = "Title 3"
        override fun getSubTitleText() = "SubTitle 3"
        override fun getDescriptionText() = "Description 3"
        override fun getTimeText() = "10:10"
    }
    private val mNotification4 = object : Notification {
        override fun getTitleText() = "Title 4"
        override fun getSubTitleText() = "SubTitle 4"
        override fun getDescriptionText() = "Description 4"
        override fun getTimeText() = "10:10"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nlv.setNotifications(arrayListOf<Notification>().apply {
            add(mNotification1)
            add(mNotification2)
            add(mNotification3)
            add(mNotification4)
        })

        btn0.setOnClickListener {
            nlv.setNotifications(arrayListOf())
        }

        btn1.setOnClickListener {
            nlv.setNotifications(arrayListOf<Notification>().apply {
                add(mNotification1)
            })
        }

        btn2.setOnClickListener {
            nlv.setNotifications(arrayListOf<Notification>().apply {
                add(mNotification1)
                add(mNotification2)
            })
        }

        btn3.setOnClickListener {
            nlv.setNotifications(arrayListOf<Notification>().apply {
                add(mNotification1)
                add(mNotification2)
                add(mNotification3)
            })
        }

        btn4.setOnClickListener {
            nlv.setNotifications(arrayListOf<Notification>().apply {
                add(mNotification1)
                add(mNotification2)
                add(mNotification3)
                add(mNotification4)
            })
        }
    }
}
