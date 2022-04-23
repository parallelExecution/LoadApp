package com.example.android.loadapp

import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val statusSting = intent.getStringExtra("DOWNLOAD_STATUS")
        val filenameString = intent.getStringExtra("FILENAME_RESULT")
        val NOTIFICATION_ID = intent.getIntExtra("NOTIFICATION_ID", 0)

        val notificationManager = getSystemService(
            NotificationManager::class.java
        )
        notificationManager.cancel(NOTIFICATION_ID)

        status_result.text = statusSting
        if (statusSting == "Fail") {
            status_result.setTextColor(Color.RED)
        }

        filename_result.text = filenameString

        button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            this.finish()
        }
    }

}