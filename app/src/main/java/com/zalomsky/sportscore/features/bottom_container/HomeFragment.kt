package com.zalomsky.sportscore.features.bottom_container

import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateOvershootInterpolator
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zalomsky.sportscore.R

import androidx.fragment.app.viewModels
import com.zalomsky.sportscore.features.auth.AuthViewModel
import com.zalomsky.sportscore.domain.models.RoleModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var navController: NavController
    private var currentNavBubblePos = 0f

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
        val bubbleIndicator = view.findViewById<View>(R.id.nav_bubble_indicator)

        val navHostFragment = childFragmentManager.findFragmentById(R.id.fragment_content_container) as? NavHostFragment

        if (navHostFragment != null) {
            navController = navHostFragment.navController
            
            // 1. Привязываем NavController
            bottomNavigationView.setupWithNavController(navController)
            
            // 2. Настраиваем визуальный стиль программно для 100% контроля
            applyCustomNavigationStyle(bottomNavigationView)

            bottomNavigationView.post {
                syncBubblePosition(bottomNavigationView, bubbleIndicator, false)
            }
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            val handled = NavigationUI.onNavDestinationSelected(item, navController)
            if (handled) {
                bottomNavigationView.post {
                    syncBubblePosition(bottomNavigationView, bubbleIndicator, true)
                }
            }
            handled
        }
    }

    private fun applyCustomNavigationStyle(navView: BottomNavigationView) {
        // УЛЬТИМАТИВНЫЙ МЕТОД: Отключаем тинтирование иконок.
        // Теперь они будут использовать свой собственный цвет из XML (белый #FFFFFF).
        navView.itemIconTintList = null
        
        // Устанавливаем чистый белый цвет для текста
        navView.itemTextColor = ColorStateList.valueOf(Color.WHITE)
        
        // Скрываем стандартный индикатор Material 3 (пилюлю)
        navView.itemActiveIndicatorColor = ColorStateList.valueOf(Color.TRANSPARENT)
        
        // Убираем рипл-эффект, чтобы он не давал темных пятен на синем фоне
        navView.itemRippleColor = ColorStateList.valueOf(Color.TRANSPARENT)
    }

    private fun syncBubblePosition(navView: BottomNavigationView, bubble: View, animate: Boolean) {
        val selectedItemId = navView.selectedItemId
        val itemView = navView.findViewById<View>(selectedItemId) ?: return

        val itemPos = IntArray(2)
        itemView.getLocationOnScreen(itemPos)

        val containerPos = IntArray(2)
        (bubble.parent as View).getLocationOnScreen(containerPos)

        val targetX = (itemPos[0] - containerPos[0] + (itemView.width / 2) - (bubble.width / 2)).toFloat()
        
        animateBubble(targetX, bubble, animate)
    }

    private fun animateBubble(targetX: Float, bubble: View, animate: Boolean) {
        if (!animate) {
            bubble.x = targetX
            bubble.visibility = View.VISIBLE
            currentNavBubblePos = targetX
            return
        }

        bubble.visibility = View.VISIBLE
        
        val animator = ValueAnimator.ofFloat(currentNavBubblePos, targetX)
        animator.duration = 400
        animator.interpolator = AnticipateOvershootInterpolator(0.8f)
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            bubble.x = value
            
            val fraction = animation.animatedFraction
            val stretch = 1f + (0.25f * Math.sin(fraction * Math.PI).toFloat())
            bubble.scaleX = stretch
            bubble.scaleY = 1f / stretch
        }
        animator.start()
        currentNavBubblePos = targetX
    }
}