package com.vla.sksu.app.application

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.vla.sksu.app.R
import com.vla.sksu.app.data.Book
import com.vla.sksu.app.data.History
import com.vla.sksu.app.manager.APIManager
import com.vla.sksu.app.ui.MainActivity
import com.vla.sksu.app.ui.account.HistoryFragmentArgs
import com.vla.sksu.app.ui.books.BookFragmentArgs
import timber.log.Timber


private const val LOG_TAG = "AppMessagingService"


class AppMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.tag(LOG_TAG).d("From: ${remoteMessage.from}")

        remoteMessage.notification?.let {
            Timber.tag(LOG_TAG).d("Message Notification Body: ${it.body}")
            sendNotification(it, remoteMessage.data)
        }
    }

    override fun onNewToken(token: String) {
        Timber.tag(LOG_TAG).d("Refreshed token: $token")
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?) {
        val apiManager = APIManager.getInstance(this)

        apiManager.updatePushToken(token){ response ->
            if (response.success) {
                Timber.tag(LOG_TAG).v("Push token updated.")
            } else {
                Timber.tag(LOG_TAG).e(response.error)
                Timber.tag(LOG_TAG).e(response.errorString)
            }
        }
    }

    private fun sendNotification(notification: RemoteMessage.Notification, data: Map<String, String>? = null) {
        val pendingIntent = when(data?.get(MainActivity.DATA_TYPE)) {
            Book.NOTIFICATION_TYPE -> {
                val bookArgs = BookFragmentArgs(null, data[MainActivity.DATA_ID]?.toInt() ?: -1)

                NavDeepLinkBuilder(this)
                    .setComponentName(MainActivity::class.java)
                    .setGraph(R.navigation.navigation)
                    .setDestination(R.id.nav_book)
                    .setArguments(bookArgs.toBundle())
                    .createPendingIntent()
            }
            History.NOTIFICATION_TYPE  -> {
                val historyArgs = HistoryFragmentArgs(null, data[MainActivity.DATA_ID]?.toInt() ?: -1)

                NavDeepLinkBuilder(this)
                    .setComponentName(MainActivity::class.java)
                    .setGraph(R.navigation.navigation)
                    .setDestination(R.id.nav_history)
                    .setArguments(historyArgs.toBundle())
                    .createPendingIntent()
            }

            else -> null
        }

        val channelId = getString(R.string.notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(notification.title)
            .setContentText(notification.body)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(getColor(R.color.colorPrimary))
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }
}