package com.zalomsky.sportscore.features.bottom_container.games

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.domain.models.responses.MatchResponseModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class MatchAdapter(
    private var matches: List<MatchResponseModel>
) : RecyclerView.Adapter<MatchAdapter.MatchViewHolder>() {

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

        fun bind(match: MatchResponseModel) {

            val matchDateTime = match.matchDate?.let {

                try {
                    LocalDateTime.parse(it)
                } catch (e: Exception) {
                    null
                }
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

            loadImage(homeTeamIcon, match.homeImageUrl)
            loadImage(awayTeamIcon, match.awayImageUrl)
        }

        private fun loadImage(imageView: ImageView, url: String?) {
            Glide.with(imageView.context)
                .load(url)
                .transition(DrawableTransitionOptions.withCrossFade())
                .circleCrop()
                .into(imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_match, parent, false)
        return MatchViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        holder.bind(matches[position])
    }

    override fun getItemCount(): Int = matches.size

    fun updateMatches(newMatches: List<MatchResponseModel>) {
        this.matches = newMatches
        notifyDataSetChanged()
    }
}