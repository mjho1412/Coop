package com.hb.coop.app.fcm

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat


class NotificationHelper(private val mContext: Context) {
    private var mNotificationManager: NotificationManager? = null
    private var mBuilder: NotificationCompat.Builder? = null

//    fun showNotificationInChat(title: String, message: String) {
//        val activity = App.instance.currentActivity
//
//        var msg = message
//        if (activity is ChatActivity) {
//            return
//        }
//
//        if (Alerter.isShowing) {
//            Alerter.hide()
//        }
//        val font = ResourcesCompat.getFont(mContext, R.font.montserrat_regular)!!
//
//        if (!AppUtils.extractUrlsImage(msg).isEmpty()) {
//            msg = "[Link Ảnh]"
//        }
//
//        Alerter.create(activity)
//            .apply {
//                setDuration(15 * 60 * 1000)
//                setTitle(title)
//                setText(msg)
//                setIcon(R.drawable.ic_chat_white_24dp)
//                setBackgroundColorRes(R.color.colorPrimary)
//                setTitleTypeface(font)
//                setTextTypeface(font)
//                addButton(text = "Trả lời", onClick = View.OnClickListener {
//                    Navigator.startChat(activity)
//                    App.instance.appComponent.apply {
//                        dataManager().setBadgeInChat(0)
//                        bus().send(UpdateBadgetChatEvent())
//                    }
//                    if (Alerter.isShowing) {
//                        Alerter.hide()
//                    }
//                })
//                addButton(text = "Hủy", onClick = View.OnClickListener {
//                    if (Alerter.isShowing) {
//                        Alerter.hide()
//                    }
//                })
//            }
//            .show()
//    }
//
//    @SuppressLint("InvalidWakeLockTag")
//    fun createNotificationByAlert(title: String, message: String, showNotificationSystem: Boolean = true) {
//
//        if (Alerter.isShowing) {
//            Alerter.hide()
//        }
//        val font = ResourcesCompat.getFont(mContext, R.font.montserrat_regular)!!
//        val alert = Alerter.create(App.instance.currentActivity)
//                .apply {
//                    setDuration(1000)
//                    setTitle(title)
//                    setText(message)
//                    setBackgroundColorRes(R.color.colorAccent)
//                    setTitleTypeface(font)
//                    setTextTypeface(font)
//                }
//                .show()
//
//        val tvTitle = alert!!.findViewById<TextView>(R.id.tvText)
//        tvTitle.ellipsize = TextUtils.TruncateAt.END
//        tvTitle.maxLines = 2
//
//        if (showNotificationSystem) {
//            val vibrator = mContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                vibrator.vibrate(
//                    VibrationEffect.createWaveform(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400), -1)
//                )
//            } else {
//                vibrator.vibrate(500L)
//            }
//
//
//            if (App.isDriver) {
//                val pm = mContext.getSystemService(Context.POWER_SERVICE) as PowerManager
//                val isScreenOn = pm.isInteractive
//                if (!isScreenOn) {
//                    val wakeLock = pm.newWakeLock(
//                        PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
//                        "tag"
//                    )
//                    wakeLock.acquire(10 * 60 * 1000L)
//                }
//
//                createNotification(title, message)
//            }
//
//            val notification = RingtoneManager.getDefaultUri(
//                if (App.isDriver) RingtoneManager.TYPE_RINGTONE
//                else RingtoneManager.TYPE_NOTIFICATION
//            )
//
//            val r = RingtoneManager.getRingtone(mContext, notification)
//            r.play()
//            ringTone = r
//
//            Handler().postDelayed({
//                stopRingtone()
//            }, 20 * 1000)
//
//        }
//
//    }
//
//    fun stopRingtone() {
//        if (ringTone != null && ringTone!!.isPlaying) {
//            ringTone!!.stop()
//        }
//
//        mNotificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
//        if (mNotificationManager != null) {
//            mNotificationManager!!.cancel(REQUEST_ID)
//        }
//
//    }
//
//    private var ringTone: Ringtone? = null
//
//    /**
//     * Create and push the notification
//     */
//    private fun createNotification(title: String, message: String) {
//
//        val resultIntent = Intent(mContext, DriverMainActivity::class.java)
//
//        val resultPendingIntent = PendingIntent.getActivity(mContext,
//                0 /* Request code */, resultIntent,
//                0)
//
//        mBuilder = NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID)
//        mBuilder!!.setContentTitle(title)
//                .setSmallIcon(R.drawable.logo_notification)
//                .setContentText(message)
//                .setAutoCancel(false)
//                .setContentIntent(resultPendingIntent)
//
//        mNotificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            val importance = NotificationManager.IMPORTANCE_HIGH
//            val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance)
//            notificationChannel.enableLights(true)
//            notificationChannel.lightColor = Color.RED
//            notificationChannel.enableVibration(true)
//            notificationChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
//            assert(mNotificationManager != null)
//            mBuilder!!.setChannelId(NOTIFICATION_CHANNEL_ID)
//            mNotificationManager!!.createNotificationChannel(notificationChannel)
//        }
//        assert(mNotificationManager != null)
//        mNotificationManager!!.notify(REQUEST_ID /* Request Code */, mBuilder!!.build())
//    }
//
//    companion object {
//        const val REQUEST_ID = 100
//        const val NOTIFICATION_CHANNEL_ID = "10001"
//        const val NOTIFICATION_CHANNEL_ID_SERVICE = "10002"
//        const val NOTIFICATION_CHANNEL_ID_INFO = "10003"
//    }
}