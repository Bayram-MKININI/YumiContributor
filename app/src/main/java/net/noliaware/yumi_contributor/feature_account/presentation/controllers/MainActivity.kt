package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.ACCOUNT_DATA
import net.noliaware.yumi_contributor.commun.util.getSerializableExtraCompat
import net.noliaware.yumi_contributor.feature_login.domain.model.AccountData

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        intent.getSerializableExtraCompat<AccountData>(ACCOUNT_DATA)?.let { accountData ->
            supportFragmentManager.beginTransaction().run {
                replace(R.id.main_fragment_container, HomeFragment.newInstance(accountData))
                commit()
            }
        }
    }
}