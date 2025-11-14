package com.zalomsky.sportscore.features.games

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zalomsky.sportscore.R

class GameFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       val view = inflater.inflate(R.layout.fragment_game, container, false)

       return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnClickListener { item ->
            when(item.id){
                R.id.nav_games -> {
                    true
                }
                R.id.nav_favorite -> {
                    view.findNavController().navigate(R.id.action_gameFragment_to_favoriteFragment)
                    true
                }
                R.id.nav_person -> {
                    view.findNavController().navigate(R.id.action_gameFragment_to_personFragment)
                    true
                }
                else -> false
            }
        }
    }
}