package com.zalomsky.sportscore.notions

import android.Manifest
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.utils.Constans

class NotificationReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_TEAM_ID = "extra_team_id"
        const val EXTRA_TEAM_NAME = "extra_team_name"
        const val ACTION_TEAM_ADDED = "com.zalomsky.sportscore.TEAM_ADDED_NOTIFICATION"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val teamId = intent.getStringExtra(EXTRA_TEAM_ID)
        val teamName = intent.getStringExtra(EXTRA_TEAM_NAME)

        if (intent.action == ACTION_TEAM_ADDED && teamId != null && teamName != null) {
            showTeamAddedNotification(context, teamId, teamName)
        }
    }

    private fun showTeamAddedNotification(
        context: Context,
        teamId: String,
        teamName: String
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val notificationTitle = "Команда добавлена!"
        val notificationText = "$teamName теперь в вашем списке избранного."

        val notification = NotificationCompat.Builder(context, Constans.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_games)
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(teamId.hashCode(), notification)
    }
}