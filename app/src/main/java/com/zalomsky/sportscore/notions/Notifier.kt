package com.zalomsky.sportscore.notions

import android.content.Context
import android.content.Intent
import com.zalomsky.sportscore.domain.models.responses.TeamResponseModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class Notifier @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun showTeamAddedNotification(team: TeamResponseModel) {
        val teamId = team.teamId ?: return
        val teamName = team.teamName ?: return

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = NotificationReceiver.ACTION_TEAM_ADDED

            putExtra(NotificationReceiver.EXTRA_TEAM_ID, teamId)
            putExtra(NotificationReceiver.EXTRA_TEAM_NAME, teamName)
        }

        context.sendBroadcast(intent)
    }
}