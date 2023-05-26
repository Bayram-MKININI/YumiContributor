package net.noliaware.yumi_contributor

import android.content.Intent
import android.os.Looper
import androidx.core.os.HandlerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import net.noliaware.yumi_contributor.commun.ACTION_PUSH_DATA
import net.noliaware.yumi_contributor.commun.PUSH_BODY
import net.noliaware.yumi_contributor.commun.PUSH_TITLE


class PushMessagingService : FirebaseMessagingService() {

    private var broadcaster: LocalBroadcastManager? = null

    override fun onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        handleMessage(remoteMessage)
    }

    private fun handleMessage(remoteMessage: RemoteMessage) {
        HandlerCompat.createAsync(
            Looper.getMainLooper()
        ).post {
            remoteMessage.notification?.let { notification ->
                val intent = Intent(ACTION_PUSH_DATA)
                intent.putExtra(PUSH_TITLE, notification.title)
                intent.putExtra(PUSH_BODY, notification.body)
                broadcaster?.sendBroadcast(intent)
            }
        }
    }
}