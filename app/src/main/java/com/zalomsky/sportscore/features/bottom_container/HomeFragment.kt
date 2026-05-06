package com.zalomsky.sportscore.features.bottom_container

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zalomsky.sportscore.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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
            
            // Добавляем плавную анимацию масштабирования при нажатии
            bottomNavigationView.setOnItemSelectedListener { item ->
                if (item.itemId != bottomNavigationView.selectedItemId) {
                    val itemView = bottomNavigationView.findViewById<View>(item.itemId)
                    itemView?.let { animateIcon(it) }
                    
                    // Стандартная обработка навигации
                    navController.navigate(item.itemId)
                }
                true
            }
        }
    }

    private fun animateIcon(view: View) {
        view.animate()
            .scaleX(1.15f)
            .scaleY(1.15f)
            .setDuration(150)
            .setInterpolator(OvershootInterpolator())
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(150)
                    .start()
            }
            .start()
    }
}