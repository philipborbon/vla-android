package com.vla.sksu.app.application

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.vla.sksu.app.R
import com.vla.sksu.app.manager.APIManager
import com.vla.sksu.app.ui.MainActivity
import timber.log.Timber


private const val LOG_TAG = "AppMessagingService"


class AppMessagingService : FirebaseMessagingService() {

    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Timber.tag(LOG_TAG).d("From: ${remoteMessage.from}")

        // Check if message contains a data payload.
//        remoteMessage?.data?.isNotEmpty()?.let {
//            Timber.tag(LOG_TAG).d("Message data payload: %s", remoteMessage.data)
//
//            if (/* Check if data needs to be processed by long running job */ true) {
//                // For long-running tasks (10 seconds or more) use WorkManager.
//                scheduleJob()
//            } else {
//                // Handle message within 10 seconds
//                handleNow()
//            }
//        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Timber.tag(LOG_TAG).d("Message Notification Body: ${it.body}")

            sendNotification(it)
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    // [START on_new_token]
    override fun onNewToken(token: String) {
        Timber.tag(LOG_TAG).d("Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }
    // [END on_new_token]

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

    private fun sendNotification(notification: RemoteMessage.Notification) {
        val backIntent = Intent(this, MainActivity::class.java)
        backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val intent = Intent(notification.clickAction)

        val pendingIntent = PendingIntent.getActivities (
            this,
            822 /* Request code */,
            arrayOf(backIntent, intent),
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = getString(R.string.notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(notification.title)
            .setContentText(notification.body)
            .setSmallIcon(R.drawable.ic_notification)
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