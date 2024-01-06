package net.noliaware.yumi_contributor.feature_login.presentation.controllers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.Navigation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.Push.ACTION_PUSH_DATA
import net.noliaware.yumi_contributor.commun.Push.PUSH_BODY
import net.noliaware.yumi_contributor.commun.Push.PUSH_TITLE
import net.noliaware.yumi_contributor.commun.util.getColorCompat
import net.noliaware.yumi_contributor.commun.util.getDrawableCompat
import net.noliaware.yumi_contributor.commun.util.tint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private val messageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            intent.extras?.let {
                val title = it.getString(PUSH_TITLE)
                val body = it.getString(PUSH_BODY)
                with(MaterialAlertDialogBuilder(this@MainActivity, R.style.AlertDialog)) {
                    setTitle(title)
                    getColorCompat(R.color.grey_6).also { color ->
                        setIcon(getDrawableCompat(R.drawable.ic_push)?.tint(color))
                    }
                    setMessage(body)
                    setPositiveButton(R.string.ok, null)
                    show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            messageReceiver,
            IntentFilter(ACTION_PUSH_DATA)
        )
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver)
    }

    override fun onSupportNavigateUp() = Navigation.findNavController(
        this,
        R.id.app_nav_host_fragment
    ).navigateUp()
}