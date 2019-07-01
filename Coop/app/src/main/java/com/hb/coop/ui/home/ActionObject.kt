package com.hb.coop.ui.home

data class ActionObject(
    val icon: Int = 0,
    val title: String = "",
    val group: Long = 0,
    val groupTitle: String = "",
    val listener: OnClickListener? = null,
    val isShowed: Boolean = true
) {
    var isEnabled = true



    interface OnClickListener {
        fun onClick()
    }
}