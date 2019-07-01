package com.hb.coop.ui.splash

/**
 * Created by buihai on 7/13/17.
 */

interface SplashContract {

    interface View {
        fun loadData()

        fun openMainActivity()

        fun openPassportActivity()

        fun openTestActivity()

        fun openIntroductionActivity()

        fun showUpdateDialog(isForce: Boolean)

    }

    interface Presenter {

        fun loadAppVersion()

        fun isLogin(): Boolean

        fun isIntroduction(): Boolean

        fun loadData()


    }


}
