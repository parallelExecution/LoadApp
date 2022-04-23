package com.example.android.loadapp

import android.animation.*
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.animation.Animation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var animator: ObjectAnimator
    private lateinit var downloadManager: DownloadManager
    private var filenameString = ""
    private var checkedId = -1
    private var URL = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        createChannel(
            getString(R.string.downloaded_notification_channel_id),
            getString(R.string.downloaded_notification_channel_name)
        )

        custom_button.setOnClickListener {
            customAnimator()
            download()
        }

    }

    private fun customAnimator() {
        val rightSweepAnimator =
            PropertyValuesHolder.ofFloat("rightSweep", 0f, custom_button.width.toFloat())
        val sweepAngleAnimator = PropertyValuesHolder.ofFloat("sweepAngle", 0f, 360f)

        animator =
            ObjectAnimator.ofPropertyValuesHolder(
                custom_button,
                rightSweepAnimator,
                sweepAngleAnimator
            ).apply {
                duration = 2000
                repeatCount = Animation.INFINITE
                repeatMode = ValueAnimator.RESTART
                addUpdateListener {
                    custom_button.invalidate()
                }
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        super.onAnimationStart(animation)
                        custom_button.isClickable = false
                        custom_button.buttonState = ButtonState.Loading
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        custom_button.isClickable = true
                        custom_button.buttonState = ButtonState.Completed
                    }
                })
            }
        animator.start()
    }

    private fun createChannel(channelId: String, channelName: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "File Download utility"

            notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("Range")
        override fun onReceive(context: Context, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            var statusString = ""
            if (id == downloadID) {
                animator.end()

                downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val cursor = downloadManager.query(DownloadManager.Query().setFilterById(id))
                if (cursor != null && cursor.moveToNext()) {
                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

                    if (status == DownloadManager.STATUS_FAILED) {
                        statusString = "Fail"
                    } else if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        statusString = "Success"
                    }
                }

                notificationManager = ContextCompat.getSystemService(
                    context,
                    NotificationManager::class.java
                ) as NotificationManager

                notificationManager.sendNotification(
                    context.getString(R.string.notification_description),
                    context,
                    statusString,
                    filenameString
                )
            }
        }
    }

    private fun download() {

        if (radio_group.checkedRadioButtonId != -1) {
            checkedId = radio_group.checkedRadioButtonId
            when (checkedId) {
                radioButton1.id -> {
                    filenameString = radioButton1.text.toString()
                    URL = URL_GLIDE
                }
                radioButton2.id -> {
                    filenameString = radioButton2.text.toString()
                    URL = URL_MOSHI
                }
                else -> {
                    filenameString = radioButton3.text.toString()
                    URL = URL_RETROFIT
                }
            }
        } else {
            Toast.makeText(this, R.string.toast_message, Toast.LENGTH_SHORT).show()
            animator.end()
            animator.duration = 3000
            animator.repeatCount = 0
            animator.start()
            return
        }

        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val URL_GLIDE =
            "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val URL_MOSHI =
            "https://github.com/square/moshi/archive/refs/tags/moshi-random.zip"
        private const val URL_RETROFIT =
            "https://github.com/square/retrofit/archive/refs/heads/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

}
