package com.example.flowmind.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import android.os.Build
import com.example.flowmind.R

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskName = intent.getStringExtra("taskName") ?: "Task"

        // Get Notification Manager
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create Notification Channel for Android O and above
        val channel = NotificationChannel(
            "flowmind_channel",
            "Task Reminder",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        // Build the notification
        val notificationBuilder = NotificationCompat.Builder(context, "flowmind_channel")
            .setSmallIcon(R.drawable.ic_notifications_black_24dp) // Your notification icon
            .setContentTitle("Task Reminder")
            .setContentText("It's time to work on: $taskName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // Show the notification
        notificationManager.notify(1001, notificationBuilder.build())
    }
}
