package com.hb.coop.ui.splash

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import butterknife.BindView
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.hb.coop.R
import com.hb.coop.common.AppConstants
import com.hb.coop.navigation.Navigator
import com.hb.coop.utils.NetworkUtils
import com.hb.lib.mvp.impl.HBMvpActivity
import com.hb.lib.utils.Utils
import timber.log.Timber


/**
 * Created by buihai on 7/13/17.
 */

class SplashActivity : HBMvpActivity<SplashPresenter>(), SplashContract.View {


    override fun getResLayoutId(): Int = R.layout.activity_splash

    @BindView(R.id.view_logo)
    lateinit var logoView: View

    @BindView(R.id.image_view_logo)
    lateinit var logoImageView: ImageView

    @BindView(R.id.text_view_version)
    lateinit var versionView: TextView
    @BindView(R.id.viewgroup_splash_container)
    lateinit var background: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val version = "Version ${Utils.getVersionName(this)} (${Utils.getVersionCode(this)})"
        versionView.text = version

        showAlertPermissions()

    }


    private fun registerAppPermission() {
        for (permission in AppConstants.PERMISSIONS_IN_APP) {
            if (!mayRequestPermission(permission))
                return
        }

        logoView.animate()
            .alpha(1.0f)
            .setDuration(1000)
            .withEndAction {
                this.loadData()
            }
            .start()
    }

    private fun showAlertPermissions() {
        statusCheck()
//        val alertPermissions = PermissionsDialogue.Builder(getActivity())
//            .setMessage(getString(R.string.app_name) + " is a sample permissions app and requires the following permissions: ")
//            .setShowIcon(false)
//            .setRequirePhone(PermissionsDialogue.REQUIRED)
//            .setRequireStorage(PermissionsDialogue.REQUIRED)
//            .setRequireCamera(PermissionsDialogue.REQUIRED)
//            .setRequireLocation(PermissionsDialogue.REQUIRED)
//            .setOnContinueClicked { _, dialog ->
//                dialog.dismiss()
//
//            }
//            .setDecorView(this.window.decorView)
//            .build()
//        alertPermissions.show()

    }

    override fun showErrorDialog(msg: String, listener: View.OnClickListener?) {
        val popup = Snackbar.make(getView(), msg, Snackbar.LENGTH_INDEFINITE)
            .setAction("Đóng") {
                loadData()
            }
        popup.show()
    }


    @SuppressLint("MissingPermission")
    override fun loadData() {

        if (!NetworkUtils.isNetworkConnected(this)) {
            val msg = "Internet của bạn đang chập chờn hoặc không vào được"
            showErrorDialog(msg)
            return
        }

        val locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval((10 * 1000).toLong())
            .setFastestInterval((1 * 1000).toLong())

        val settingsBuilder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        settingsBuilder.setAlwaysShow(true)

        val result = LocationServices.getSettingsClient(this)
            .checkLocationSettings(settingsBuilder.build())

        result.addOnCompleteListener {
            try {
                val response = it.getResult(ApiException::class.java)
                Timber.d(response.toString())
                checkUpdate()

            } catch (ex: ApiException) {
                when (ex.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try {
                            val resolvableApiException = ex as ResolvableApiException
                            resolvableApiException.startResolutionForResult(this, LOCATION_SETTINGS_REQUEST)
                        } catch (e: IntentSender.SendIntentException) {
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {

                    }
                }

            }
        }
    }

    lateinit var fusedLocationClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    private fun checkUpdate() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                Timber.d("Test $location")
            }
            mPresenter.loadAppVersion()
        }


    }

    override fun showUpdateDialog(isForce: Boolean) {
        val builder = AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle("Thông báo cập nhật")
            .setMessage("Hiện nay đang có phiên bản mới. Cập nhật để sử dụng tốt hơn.")
            .setPositiveButton("Cập nhật") { dialog, _ ->
                val appPackageName = packageName // getPackageName() from Context or Activity object
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
                } catch (anfe: android.content.ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                        )
                    )
                }

                dialog.dismiss()
                finish()
            }
            .setNegativeButton("Đóng") { dialog, _ ->
                dialog.dismiss()
                finish()
            }

        if (!isForce) {
            builder.setNegativeButton("Bỏ qua") { dialog, _ ->
                dialog.dismiss()
                mPresenter.loadData()
            }
        }


        builder.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_GPS_MANAGER) {
            statusCheck()
        } else if (requestCode == LOCATION_SETTINGS_REQUEST) {
            statusCheck()
        }
    }


    private fun mayRequestPermission(permission: String): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            Snackbar.make(
                getView(), "Permissions are need for application.",
                Snackbar.LENGTH_INDEFINITE
            ).setAction(
                android.R.string.ok
            ) { _ -> ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_ALL_PERMISSION) }
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_ALL_PERMISSION)
        }
        return false
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {

        if (requestCode == REQUEST_ALL_PERMISSION) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                registerAppPermission()
            }
        }
    }


    override fun openIntroductionActivity() {
//        Navigator.startIntroduction(this)
        this.finish()
    }

    override fun openTestActivity() {
//        Navigator.startTest(this)
        this.finish()
    }

    override fun openMainActivity() {
        Navigator.startMain(this)
        this.finish()
    }

    override fun openPassportActivity() {
        Navigator.startPassport(this)
        this.finish()
    }

    private fun statusCheck() {
        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        } else {
            registerAppPermission()
        }
    }

    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Ứng dụng cần phải bật định vị. Bạn có muốn bật không?")
            .setCancelable(false)
            .setPositiveButton("Muốn") { _, _ ->
                startActivityForResult(
                    Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                    REQUEST_GPS_MANAGER
                )
            }
            .setNegativeButton("Không") { dialog, _ ->
                dialog.cancel()
                finish()
            }
        val alert = builder.create()
        alert.show()
    }


    companion object {
        const val REQUEST_ALL_PERMISSION = 0
        const val REQUEST_GPS_MANAGER = 1
        const val LOCATION_SETTINGS_REQUEST = 2
    }
}
