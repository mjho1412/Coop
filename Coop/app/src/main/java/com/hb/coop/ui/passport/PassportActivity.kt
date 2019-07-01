package com.hb.coop.ui.passport

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton
import butterknife.BindView
import butterknife.OnClick
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.hb.coop.BuildConfig
import com.hb.coop.R
import com.hb.coop.data.DataManager
import com.hb.coop.navigation.Navigator
import com.hb.coop.utils.AppUtils
import com.hb.coop.utils.KeyboardUtils
import com.hb.coop.utils.image.BitmapUtils
import com.hb.lib.mvp.impl.HBMvpActivity

class PassportActivity : HBMvpActivity<PassportPresenter>(), PassportContract.View {

    override fun getResLayoutId(): Int {
        return R.layout.activity_passport
    }

    @BindView(R.id.input_passport_username)
    lateinit var usernameView: TextInputLayout
    @BindView(R.id.input_passport_password)
    lateinit var passwordView: TextInputLayout

    @BindView(R.id.button_passport_login)
    lateinit var mLoginButton: CircularProgressButton

    private val mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (BuildConfig.DEBUG) {
            usernameView.editText!!.setText("admin")
            passwordView.editText!!.setText("coop@123")
        } else {
            usernameView.editText!!.setText(mPresenter.dataManager<DataManager>().getUsername())
        }

        AppUtils.setupInput(usernameView)
        AppUtils.setupInput(passwordView)

        passwordView.editText!!.setOnEditorActionListener { _, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_GO
                || actionId == EditorInfo.IME_ACTION_DONE
                || keyEvent.action == KeyEvent.ACTION_DOWN
                && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                onLogin()
                return@setOnEditorActionListener true
            }
            true
        }


    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mLoginButton.dispose()
    }


    override fun loginCompleted() {
        val drawable = BitmapUtils.loadImageVector(this, R.drawable.ic_check_black_24dp, R.color.white)
        val bitmap = BitmapUtils.drawableToBitmap(drawable)
        mLoginButton.doneLoadingAnimation(
            ContextCompat.getColor(this@PassportActivity, R.color.colorPrimary),
            bitmap
        )

        mHandler.postDelayed({
            Navigator.startMain(this)
            this@PassportActivity.finish()
        }, 1000)
    }

    override fun loginFailed(msg: String) {
        val drawable = BitmapUtils.loadImageVector(this, R.drawable.ic_error_black_24dp, R.color.white)
        val bitmap = BitmapUtils.drawableToBitmap(drawable)
        mLoginButton.doneLoadingAnimation(
            ContextCompat.getColor(this@PassportActivity, R.color.colorPrimary),
            bitmap
        )

        mHandler.postDelayed({
            mLoginButton.revertAnimation()
        }, 1000)

        Snackbar.make(getView(), "$msg", Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.button_close) {
                usernameView.isEnabled = true
                passwordView.isEnabled = true
            }.show()
    }

    override fun forgetCompleted() {
    }

    @OnClick(R.id.button_passport_forget_password)
    fun onForgetPassword() {

    }

    @OnClick(R.id.button_passport_login)
    fun onLogin() {
        val user = usernameView.editText!!.text
        val pass = passwordView.editText!!.text
        KeyboardUtils.hideSoftInput(this)

        if (validated(user, pass)) {
            usernameView.isEnabled = false
            passwordView.isEnabled = false
            mLoginButton.startAnimation()
            mPresenter.login(
                user = user.toString(),
                pass = pass.toString()
            )
        }


    }

    private fun validated(user: Editable, pass: Editable): Boolean {
        if (TextUtils.isEmpty(user)) {
            usernameView.error = getString(R.string.msg_text_empty)
            usernameView.requestFocus()
            return false
        }
        if (TextUtils.isEmpty(pass)) {
            passwordView.error = getString(R.string.msg_text_empty)
            passwordView.requestFocus()
            return false
        }
        return true
    }
}