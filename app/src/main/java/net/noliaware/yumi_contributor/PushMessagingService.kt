package net.noliaware.yumi_contributor

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.noliaware.yumi_contributor.commun.ACTION_PUSH_DATA
import net.noliaware.yumi_contributor.commun.PUSH_BODY
import net.noliaware.yumi_contributor.commun.PUSH_TITLE

class PushMessagingService : FirebaseMessagingService() {

    private var broadcaster: LocalBroadcastManager? = null
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        handleMessage(remoteMessage)
    }

    private fun handleMessage(remoteMessage: RemoteMessage) {
        scope.launch {
            remoteMessage.notification?.let { notification ->
                val intent = Intent(ACTION_PUSH_DATA)
                intent.putExtra(PUSH_TITLE, notification.title)
                intent.putExtra(PUSH_BODY, notification.body)
                broadcaster?.sendBroadcast(intent)
            }
        }
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}