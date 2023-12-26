package net.noliaware.yumi_contributor

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import net.noliaware.yumi_contributor.commun.Push.ACTION_PUSH_DATA
import net.noliaware.yumi_contributor.commun.Push.PUSH_BODY
import net.noliaware.yumi_contributor.commun.Push.PUSH_TITLE

class PushMessagingService : FirebaseMessagingService() {

    private val broadcaster by lazy {
        LocalBroadcastManager.getInstance(this)
    }

    override fun onNewToken(token: String) = Unit

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        handleMessage(remoteMessage)
    }

    private fun handleMessage(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let { notification ->
            Intent(ACTION_PUSH_DATA).apply {
                putExtra(PUSH_TITLE, notification.title)
                putExtra(PUSH_BODY, notification.body)
            }.also {
                broadcaster.sendBroadcast(it)
            }
        }
    }
}