package com.hb.coop.ui.passport

interface PassportContract {
    interface View {

        fun loginCompleted()

        fun loginFailed(msg: String)

        fun forgetCompleted()

    }

    interface Presenter {
        fun login(user: String, pass: String)

        fun forgetPassword(user: String)

    }


}