package com.hb.coop.ui.map

interface MapContract {
    interface View {

    }

    interface Presenter {
        fun getToken(): String
    }
}