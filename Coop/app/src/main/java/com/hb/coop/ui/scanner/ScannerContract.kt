package com.hb.coop.ui.scanner

interface ScannerContract {
    interface View {

        fun completed()

        fun showError(error: Throwable)
    }

    interface Presenter {

        fun addContentScanner(content: String)

        fun clearAllContentScanner()

        fun getAllContentScanner(): ArrayList<String>

        fun saveBarcodeList()

    }
}