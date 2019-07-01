package com.hb.coop.ui.test.order

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hb.coop.R
import com.hb.coop.common.replaceFragmentInActivity
import com.hb.coop.ui.base.listener.BottomNavigationViewListener
import com.hb.coop.ui.base.listener.OnBackPressedListener
import kotlinx.android.synthetic.main.activity_order.*

class OrderActivity : AppCompatActivity(), BottomNavigationViewListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_TranslucentStatus)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
        setupFragment(OrderFragment.newInstance())
    }

    private fun setupFragment(fragment: Fragment) {
        supportFragmentManager.findFragmentById(R.id.fragment_container)
            ?: fragment.let {
                replaceFragmentInActivity(it, R.id.fragment_container)
            }
    }

    override fun hideBottomNavigationView() {
        if (bottom_navigation.translationY == 0f)
            bottom_navigation.animate()
                .translationY(bottom_navigation.height.toFloat())
                .setDuration(250)
                .start()
    }

    override fun showBottomNavigationView() {
        if (bottom_navigation.translationY >= bottom_navigation.height.toFloat())
            bottom_navigation.animate()
                .translationY(0f)
                .setDuration(400)
                .start()
    }

    override fun onBackPressed() {
        val fragmentList = supportFragmentManager.fragments
        var proceedToSuper = true
        for (fragment in fragmentList) {
            if (fragment is OnBackPressedListener) {
                proceedToSuper = false
                (fragment as OnBackPressedListener).onBackPressed()
            }
        }
        if (proceedToSuper) super.onBackPressed()
    }
}