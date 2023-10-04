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
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import net.noliaware.yumi_contributor.feature_login.presentation.views.LoginLayout
import net.noliaware.yumi_contributor.feature_login.presentation.views.LoginView.LoginViewCallback
import net.noliaware.yumi_contributor.feature_login.presentation.views.PasswordView.PasswordViewCallback
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.Push.ACTION_PUSH_DATA
import net.noliaware.yumi_contributor.commun.Push.PUSH_BODY
import net.noliaware.yumi_contributor.commun.Push.PUSH_TITLE
import net.noliaware.yumi_contributor.commun.util.ViewModelState.DataState
import net.noliaware.yumi_contributor.commun.util.ViewModelState.LoadingState
import net.noliaware.yumi_contributor.commun.util.handleSharedEvent
import net.noliaware.yumi_contributor.commun.util.safeNavigate
import net.noliaware.yumi_contributor.commun.util.startWebBrowserAtURL

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel: LoginFragmentViewModel by viewModels()
    private var loginLayout: LoginLayout? = null
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
        return inflater.inflate(R.layout.login_layout, container, false).apply {
            loginLayout = this as LoginLayout
            loginLayout?.getLoginView?.callback = loginViewCallback
            loginLayout?.getPasswordView?.callback = passwordViewCallback
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectFlows()
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            messageReceiver,
            IntentFilter(ACTION_PUSH_DATA)
        )
    }

    override fun onResume() {
        super.onResume()
        loginLayout?.computeLoginLayout()
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
                    is LoadingState -> loginLayout?.setLoginViewProgressVisible(true)
                    is DataState -> vmState.data?.let {
                        loginLayout?.setLoginViewProgressVisible(false)
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
                        loginLayout?.setLogin(userPrefs.login)
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.initEventsHelper.eventFlow.collectLatest { sharedEvent ->
                loginLayout?.setLoginViewProgressVisible(false)
                handleSharedEvent(sharedEvent)
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.initEventsHelper.stateFlow.collect { vmState ->
                when (vmState) {
                    is LoadingState -> Unit
                    is DataState -> vmState.data?.let { initData ->
                        viewModel.saveDeviceIdPreferences(initData.deviceId)
                        loginLayout?.setLoginViewProgressVisible(false)
                        loginLayout?.displayPasswordView()
                        loginLayout?.fillPadViewWithData(initData.keyboard)
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.accountDataEventsHelper.eventFlow.collectLatest { sharedEvent ->
                loginLayout?.let {
                    loginLayout?.setPasswordViewProgressVisible(false)
                    it.clearSecretDigits()
                    passwordIndexes.clear()
                }
                handleSharedEvent(sharedEvent)
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.accountDataEventsHelper.stateFlow.collect { vmState ->
                when (vmState) {
                    is LoadingState -> loginLayout?.setPasswordViewProgressVisible(true)
                    is DataState -> vmState.data?.let { accountData ->
                        loginLayout?.setPasswordViewProgressVisible(false)
                        findNavController().safeNavigate(
                            LoginFragmentDirections.actionLoginFragmentToHomeFragment(accountData)
                        )
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
                loginLayout?.addSecretDigit()
            }

            override fun onClearButtonPressed() {
                passwordIndexes.clear()
                loginLayout?.clearSecretDigits()
            }

            override fun onConfirmButtonPressed() {
                if (passwordIndexes.isEmpty())
                    return
                viewModel.callConnectWebserviceWithIndexes(passwordIndexes)
            }
        }
    }

    override fun onDestroyView() {
        loginLayout = null
        super.onDestroyView()
    }
}