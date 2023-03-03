package net.noliaware.yumi_contributor.feature_login.presentation.controllers

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.ACCOUNT_DATA
import net.noliaware.yumi_contributor.commun.util.ViewModelState
import net.noliaware.yumi_contributor.commun.util.handleSharedEvent
import net.noliaware.yumi_contributor.commun.util.inflate
import net.noliaware.yumi_contributor.feature_account.presentation.controllers.MainActivity
import net.noliaware.yumi_contributor.feature_login.presentation.views.LoginParentView
import net.noliaware.yumi_contributor.feature_login.presentation.views.LoginView
import net.noliaware.yumi_contributor.feature_login.presentation.views.LoginView.*
import net.noliaware.yumi_contributor.feature_login.presentation.views.PasswordView
import java.net.NetworkInterface

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel: LoginFragmentViewModel by viewModels()
    private var loginParentView: LoginParentView? = null
    private val passwordIndexes = mutableListOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return container?.inflate(R.layout.login_layout, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginParentView = view as LoginParentView
        loginParentView?.loginView?.callback = loginViewCallback
        loginParentView?.passwordView?.callback = passwordViewCallback
        collectFlows()
    }

    fun getAndroidId(): String = Settings.Secure.getString(
        context?.applicationContext?.contentResolver,
        Settings.Secure.ANDROID_ID
    )

    private fun getMac(): String? =
        try {
            NetworkInterface.getNetworkInterfaces()
                .toList()
                .find { networkInterface ->
                    networkInterface.name.equals(
                        "wlan0",
                        ignoreCase = true
                    )
                }
                ?.hardwareAddress
                ?.joinToString(separator = ":") { byte -> "%02X".format(byte) }
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }

    private fun collectFlows() {

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.prefsStateFlow.collectLatest { vmState ->
                when (vmState) {
                    is ViewModelState.LoadingState -> Unit
                    is ViewModelState.DataState -> vmState.data?.let { userPrefs ->
                        loginParentView?.setLogin(userPrefs.login)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.initEventsHelper.eventFlow.collectLatest { sharedEvent ->
                loginParentView?.setLoginViewProgressVisible(false)
                handleSharedEvent(sharedEvent)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            viewModel.initEventsHelper.stateFlow.collect { vmState ->
                when (vmState) {
                    is ViewModelState.LoadingState -> loginParentView?.setLoginViewProgressVisible(
                        true
                    )

                    is ViewModelState.DataState -> vmState.data?.let { initData ->
                        viewModel.saveDeviceIdPreferences(initData.deviceId)
                        loginParentView?.setLoginViewProgressVisible(false)
                        loginParentView?.displayPasswordView()
                        loginParentView?.fillPadViewWithData(initData.keyboard)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.accountDataEventsHelper.eventFlow.collectLatest { sharedEvent ->
                loginParentView?.let {
                    it.setLoginViewProgressVisible(false)
                    it.clearSecretDigits()
                    passwordIndexes.clear()
                }
                handleSharedEvent(sharedEvent)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            viewModel.accountDataEventsHelper.stateFlow.collect { vmState ->
                when (vmState) {
                    is ViewModelState.LoadingState -> Unit
                    is ViewModelState.DataState -> vmState.data?.let { accountData ->
                        activity?.finish()
                        val intent = Intent(requireActivity(), MainActivity::class.java)
                        intent.putExtra(ACCOUNT_DATA, accountData)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private val loginViewCallback: LoginViewCallback by lazy {
        LoginViewCallback { login ->
            viewModel.saveLoginPreferences(login)

            viewModel.callInitWebservice(
                getAndroidId(),
                viewModel.prefsStateData?.deviceId,
                login
            )
        }
    }

    private val passwordViewCallback: PasswordView.PasswordViewCallback by lazy {
        object : PasswordView.PasswordViewCallback {

            override fun onPadClickedAtIndex(index: Int) {
                if (passwordIndexes.size >= 6)
                    return

                passwordIndexes.add(index)
                loginParentView?.addSecretDigit()
            }

            override fun onClearButtonPressed() {
                passwordIndexes.clear()
                loginParentView?.clearSecretDigits()
            }

            override fun onConfirmButtonPressed() {
                if (passwordIndexes.isEmpty())
                    return
                viewModel.callConnectWebserviceWithIndexes(passwordIndexes)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        loginParentView = null
    }
}