package com.hb.coop.ui.web

interface WebContract {

    interface View {
        fun setupWebView()
    }

    interface Presenter {

    }

    companion object {
        const val TITLE_TAG = "TITLE_TAG"
        const val WEB_URL_TAG = "WEB_URL_TAG"
    }

}