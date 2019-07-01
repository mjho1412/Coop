package com.hb.coop.ui.base

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.view.updatePadding
import com.hb.coop.R
import com.hb.coop.common.getStatusBarHeight
import com.hb.lib.mvp.base.MvpContract
import com.hb.lib.mvp.impl.HBMvpFragment

abstract class AppWithTranslucentStatusMvpFragment<P  : MvpContract.Presenter> : HBMvpFragment<P>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbarMargin(view)
    }


    private fun setupToolbarMargin(view: View) {
        val toolbar = view.findViewById<View?>(R.id.toolbar)
        toolbar?.let {
            val statusBarHeight = context?.getStatusBarHeight()
            val lp = it.layoutParams as ViewGroup.LayoutParams
            lp.height += statusBarHeight as Int
            if (it is CardView) {
                it.setContentPadding(0, statusBarHeight, 0, 0)
            } else {
                it.updatePadding(top = statusBarHeight)
            }
        }
    }
}