package com.zalomsky.sportscore.features.bottom_container

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zalomsky.sportscore.R

class HomeFragment : Fragment() {

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottom_navigation)

        val navHostFragment = childFragmentManager.findFragmentById(R.id.fragment_content_container) as? NavHostFragment

        if (navHostFragment != null) {
            navController = navHostFragment.navController

            bottomNavigationView.setupWithNavController(navController)
        } else {
           Log.e("HomeFragment", "NavHostFragment not found in container")
        }
    }
}