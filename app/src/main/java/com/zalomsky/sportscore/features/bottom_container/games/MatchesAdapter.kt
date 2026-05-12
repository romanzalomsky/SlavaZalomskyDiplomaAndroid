package com.zalomsky.sportscore.features.bottom_container.games

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.domain.models.responses.MatchResponseModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class MatchesAdapter : ListAdapter<MatchResponseModel, MatchesAdapter.MatchViewHolder>(
    MatchesDiffCallback()
) {
    private var scheduledMatchIds: Set<String> = emptySet()

    fun updateScheduledMatches(scheduledIds: Set<String>) {
        if (scheduledMatchIds != scheduledIds) {
            scheduledMatchIds = scheduledIds
            notifyDataSetChanged()
        }
    }

    private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM", Locale("ru"))
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale("ru"))

    inner class MatchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        val homeTeamIcon: ImageView = itemView.findViewById(R.id.homeTeamIcon)
        val homeTeamName: TextView = itemView.findViewById(R.id.homeTeamName)
        val homeTeamScore: TextView = itemView.findViewById(R.id.homeTeamScore)
        val awayTeamIcon: ImageView = itemView.findViewById(R.id.awayTeamIcon)
        val awayTeamName: TextView = itemView.findViewById(R.id.awayTeamName)
        val awayTeamScore: TextView = itemView.findViewById(R.id.awayTeamScore)
        val streamLinkTextView: TextView = itemView.findViewById(R.id.streamLinkTextView)

        fun bind(match: MatchResponseModel, isScheduled: Boolean) {
            val matchDateTimeString = match.matchDate
            val matchDateTime = try {
                if (!matchDateTimeString.isNullOrEmpty()) LocalDateTime.parse(matchDateTimeString) else null
            } catch (e: Exception) {
                null
            }

            if (matchDateTime != null) {
                dateTextView.text = matchDateTime.format(dateFormatter)
                timeTextView.text = matchDateTime.format(timeFormatter)
            } else {
                dateTextView.text = "—"
                timeTextView.text = "TBD"
            }

            homeTeamName.text = match.homeName
            awayTeamName.text = match.awayName

            homeTeamScore.text = match.homeScore?.toString() ?: "-"
            awayTeamScore.text = match.awayScore?.toString() ?: "-"
            if (match.streamUrl.isNullOrBlank()) {
                streamLinkTextView.visibility = View.GONE
            } else {
                streamLinkTextView.visibility = View.VISIBLE
                streamLinkTextView.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(match.streamUrl))
                    itemView.context.startActivity(intent)
                }
            }

            loadImage(homeTeamIcon, match.homeImageUrl)
            loadImage(awayTeamIcon, match.awayImageUrl)
        }

        private fun loadImage(imageView: ImageView, url: String?) {
            Glide.with(imageView.context)
                .load(url)
                .transition(DrawableTransitionOptions.withCrossFade())
                .circleCrop()
                .placeholder(R.drawable.ic_download)
                .error(R.drawable.ic_pause)
                .into(imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_match, parent, false)
        return MatchViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val match = getItem(position)
        val isScheduled = scheduledMatchIds.contains(match.matchId)
        holder.bind(match, isScheduled)
    }

    class MatchesDiffCallback : DiffUtil.ItemCallback<MatchResponseModel>() {
        override fun areItemsTheSame(
            oldItem: MatchResponseModel,
            newItem: MatchResponseModel
        ): Boolean {
            return oldItem.matchId == newItem.matchId
        }

        override fun areContentsTheSame(
            oldItem: MatchResponseModel,
            newItem: MatchResponseModel
        ): Boolean {
            return oldItem.matchDate == newItem.matchDate &&
                    oldItem.homeName == newItem.homeName &&
                    oldItem.awayName == newItem.awayName
        }
    }
}