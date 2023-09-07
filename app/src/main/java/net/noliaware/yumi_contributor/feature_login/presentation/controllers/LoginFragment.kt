package net.noliaware.yumi_contributor.feature_login.presentation.controllers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.ACCOUNT_DATA
import net.noliaware.yumi_contributor.commun.ACTION_PUSH_DATA
import net.noliaware.yumi_contributor.commun.PUSH_BODY
import net.noliaware.yumi_contributor.commun.PUSH_TITLE
import net.noliaware.yumi_contributor.commun.util.ViewModelState.DataState
import net.noliaware.yumi_contributor.commun.util.ViewModelState.LoadingState
import net.noliaware.yumi_contributor.commun.util.handleSharedEvent
import net.noliaware.yumi_contributor.commun.util.inflate
import net.noliaware.yumi_contributor.commun.util.startWebBrowserAtURL
import net.noliaware.yumi_contributor.feature_account.presentation.controllers.MainActivity
import net.noliaware.yumi_contributor.feature_login.presentation.views.LoginParentLayout
import net.noliaware.yumi_contributor.feature_login.presentation.views.LoginView.LoginViewCallback
import net.noliaware.yumi_contributor.feature_login.presentation.views.PasswordView.PasswordViewCallback

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel: LoginFragmentViewModel by viewModels()
    private var loginParentLayout: LoginParentLayout? = null
    private val passwordIndexes = mutableListOf<Int>()

    private val messageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            intent.extras?.let {
                val title = it.getString(PUSH_TITLE)
                val body = it.getString(PUSH_BODY)
                val text = "$title:$body"
                view?.let { view ->
                    Snackbar.make(
                        view,
                        text,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return container?.inflate(R.layout.login_layout)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginParentLayout = view as LoginParentLayout
        loginParentLayout?.loginView?.callback = loginViewCallback
        loginParentLayout?.passwordView?.callback = passwordViewCallback
        collectFlows()
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            messageReceiver,
            IntentFilter(ACTION_PUSH_DATA)
        )
    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(messageReceiver)
        super.onStop()
    }

    private fun getAndroidId(): String = Settings.Secure.getString(
        context?.applicationContext?.contentResolver,
        Settings.Secure.ANDROID_ID
    )

    private fun collectFlows() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.forceUpdateStateFlow.collectLatest { vmState ->
                when (vmState) {
                    is LoadingState -> loginParentLayout?.setLoginViewProgressVisible(true)
                    is DataState -> vmState.data?.let {
                        loginParentLayout?.setLoginViewProgressVisible(false)
                        showForceUpdateDialog()
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.prefsStateFlow.collectLatest { vmState ->
                when (vmState) {
                    is LoadingState -> Unit
                    is DataState -> vmState.data?.let { userPrefs ->
                        loginParentLayout?.setLogin(userPrefs.login)
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.initEventsHelper.eventFlow.collectLatest { sharedEvent ->
                loginParentLayout?.setLoginViewProgressVisible(false)
                handleSharedEvent(sharedEvent)
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.initEventsHelper.stateFlow.collect { vmState ->
                when (vmState) {
                    is LoadingState -> Unit
                    is DataState -> vmState.data?.let { initData ->
                        viewModel.saveDeviceIdPreferences(initData.deviceId)
                        loginParentLayout?.setLoginViewProgressVisible(false)
                        loginParentLayout?.displayPasswordView()
                        loginParentLayout?.fillPadViewWithData(initData.keyboard)
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.accountDataEventsHelper.eventFlow.collectLatest { sharedEvent ->
                loginParentLayout?.let {
                    loginParentLayout?.setPasswordViewProgressVisible(false)
                    it.clearSecretDigits()
                    passwordIndexes.clear()
                }
                handleSharedEvent(sharedEvent)
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.accountDataEventsHelper.stateFlow.collect { vmState ->
                when (vmState) {
                    is LoadingState -> loginParentLayout?.setPasswordViewProgressVisible(true)
                    is DataState -> vmState.data?.let { accountData ->
                        loginParentLayout?.setPasswordViewProgressVisible(false)
                        Intent(requireActivity(), MainActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            putExtra(ACCOUNT_DATA, accountData)
                            startActivity(this)
                        }
                    }
                }
            }
        }
    }

    private fun showForceUpdateDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.update_app)
            .setMessage(R.string.update_message)
            .setPositiveButton(R.string.update) { _, _ ->
                viewModel.forceUpdateUrl?.let { url ->
                    context?.startWebBrowserAtURL(url)
                }
            }
            .setCancelable(false)
            .create().apply {
                setCanceledOnTouchOutside(false)
                show()
            }
    }

    private val loginViewCallback: LoginViewCallback by lazy {
        LoginViewCallback { login ->
            viewModel.saveLoginPreferences(login)
            viewModel.callInitWebservice(getAndroidId(), login)
        }
    }

    private val passwordViewCallback: PasswordViewCallback by lazy {
        object : PasswordViewCallback {

            override fun onPadClickedAtIndex(index: Int) {
                if (passwordIndexes.size >= 6)
                    return

                passwordIndexes.add(index)
                loginParentLayout?.addSecretDigit()
            }

            override fun onClearButtonPressed() {
                passwordIndexes.clear()
                loginParentLayout?.clearSecretDigits()
            }

            override fun onConfirmButtonPressed() {
                if (passwordIndexes.isEmpty())
                    return
                viewModel.callConnectWebserviceWithIndexes(passwordIndexes)
            }
        }
    }

    override fun onDestroyView() {
        loginParentLayout = null
        super.onDestroyView()
    }
}