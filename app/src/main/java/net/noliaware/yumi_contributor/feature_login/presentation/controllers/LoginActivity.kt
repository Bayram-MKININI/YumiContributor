package net.noliaware.yumi_contributor.feature_login.presentation.controllers

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import net.noliaware.yumi_contributor.R

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_login)
        supportFragmentManager.beginTransaction().run {
            replace(R.id.main_fragment_container, LoginFragment())
            commitAllowingStateLoss()
        }
    }
}