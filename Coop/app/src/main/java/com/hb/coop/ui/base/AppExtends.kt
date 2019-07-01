package com.hb.coop.ui.base

import android.app.Activity
import android.os.Build
import android.view.View


internal fun Activity.setLightNavBarCompat() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        var flags = window.decorView.systemUiVisibility
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        window.decorView.systemUiVisibility = flags
    }
}
