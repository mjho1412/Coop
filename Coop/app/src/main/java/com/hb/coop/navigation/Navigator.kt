package com.hb.coop.navigation

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.core.app.ActivityOptionsCompat
import com.hb.coop.ui.main.MainActivity
import com.hb.coop.ui.passport.PassportActivity
import com.hb.coop.ui.scanner.ScannerActivity
import com.hb.coop.ui.supplier.SupplierActivity
import com.hb.coop.ui.supplier.basket.BasketByProductActivity
import com.hb.coop.ui.supplier.basket.BasketByProductContract
import com.hb.coop.ui.system.basket.BasketActivity
import com.hb.coop.ui.test.order.OrderActivity
import com.hb.coop.ui.test.product.ProductActivity
import com.hb.coop.ui.vehicle.VehicleActivity
import com.hb.coop.ui.web.WebActivity
import com.hb.coop.ui.web.WebContract


object Navigator {

    @SuppressLint("MissingPermission")
    fun callToNumber(activity: Activity, phoneNumber: String) {
        val intent = Intent(Intent.ACTION_CALL)

        intent.data = Uri.parse("tel:$phoneNumber")
        activity.startActivity(intent)
    }

    fun startWeb(activity: Activity, title: String = "", url: String) {
        val intent = Intent(activity, WebActivity::class.java)
        intent.putExtra(WebContract.TITLE_TAG, title)
        intent.putExtra(WebContract.WEB_URL_TAG, url)
        activity.startActivity(intent)
    }


    fun startBrower(activity: Activity, url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        activity.startActivity(browserIntent)
    }

    fun startMain(activity: Activity) {
        val intent = Intent(activity, MainActivity::class.java)
        activity.startActivity(intent)
    }

    fun startPassport(activity: Activity) {
        val intent = Intent(activity, PassportActivity::class.java)
        activity.startActivity(intent)
    }

    fun startProfile(activity: Activity) {

    }

    fun startProduct(activity: Activity) {
        val intent = Intent(activity, ProductActivity::class.java)
        activity.startActivity(intent)
    }

    fun startScanner(activity: Activity, requestCode: Int = -1) {
        val intent = Intent(activity, ScannerActivity::class.java)
        if (requestCode == -1) {
            activity.startActivity(intent)
        } else {
            activity.startActivityForResult(intent, requestCode)
        }
    }

    fun startOrder(activity: Activity) {
        val intent = Intent(activity, OrderActivity::class.java)
        activity.startActivity(intent)
    }

    fun startProvider(activity: Activity) {
        val intent = Intent(activity, SupplierActivity::class.java)
        activity.startActivity(intent)
    }

    fun startBoxByProduct(activity: Activity, position: Int, options: ActivityOptionsCompat? = null) {
        val intent = Intent(activity, BasketByProductActivity::class.java)
        intent.putExtra(BasketByProductContract.EXTRA_POSITION, position)
        activity.startActivity(intent, options?.toBundle())
    }

    fun startVehicle(activity: Activity) {
        val intent = Intent(activity, VehicleActivity::class.java)
        activity.startActivity(intent)
    }

    fun startBasket(activity: Activity) {
        val intent = Intent(activity, BasketActivity::class.java)
        activity.startActivity(intent)
    }
}