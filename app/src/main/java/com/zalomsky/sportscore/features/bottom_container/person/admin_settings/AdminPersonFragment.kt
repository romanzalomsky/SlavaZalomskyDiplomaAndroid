package com.zalomsky.sportscore.features.bottom_container.person.admin_settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import com.zalomsky.sportscore.R

class AdminPersonFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_person, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val linkToCountry = view.findViewById<Button>(R.id.linkToCountryButton)
        val linkToCity = view.findViewById<Button>(R.id.linkToCityButton)
        val linkToLeague = view.findViewById<Button>(R.id.linkToLeagueButton)
        val linkToPlayer = view.findViewById<Button>(R.id.linkToPlayerButton)
        val linkToTeam = view.findViewById<Button>(R.id.linkToTeamButton)

        linkToCountry.setOnClickListener {
            it.findNavController().navigate(R.id.action_adminPersonFragment_to_countryFragment)
        }
        linkToCity.setOnClickListener {
            it.findNavController().navigate(R.id.action_adminPersonFragment_to_cityFragment)
        }
        linkToLeague.setOnClickListener {
            it.findNavController().navigate(R.id.action_adminPersonFragment_to_leagueFragment)
        }
        linkToPlayer.setOnClickListener {
            it.findNavController().navigate(R.id.action_adminPersonFragment_to_playerFragment)
        }
        linkToTeam.setOnClickListener {
            it.findNavController().navigate(R.id.action_adminPersonFragment_to_teamFragment2)
        }
    }
}