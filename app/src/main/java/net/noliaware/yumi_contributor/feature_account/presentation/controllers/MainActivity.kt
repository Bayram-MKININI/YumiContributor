package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.ACCOUNT_DATA
import net.noliaware.yumi_contributor.commun.util.getSerializable
import net.noliaware.yumi_contributor.feature_login.domain.model.AccountData

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        intent.getSerializable<AccountData>(ACCOUNT_DATA)?.let { accountData ->
            supportFragmentManager.beginTransaction().run {
                replace(R.id.main_fragment_container, HomeFragment.newInstance(accountData))
                commit()
            }
        }
    }

    /*
    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, mainContainer).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun showSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(window, mainContainer).show(WindowInsetsCompat.Type.systemBars())
    }
     */
}