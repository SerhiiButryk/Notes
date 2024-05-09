/*
 * Copyright 2023. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.google.android.material.textfield.TextInputLayout
import com.serhii.apps.notes.R
import com.serhii.apps.notes.activities.AppBaseActivity
import com.serhii.apps.notes.control.NativeBridge
import com.serhii.apps.notes.control.auth.types.AuthorizeType
import com.serhii.apps.notes.ui.data_model.createModel
import com.serhii.apps.notes.ui.view_model.LoginViewModel
import com.serhii.core.log.Log
import com.serhii.core.security.BiometricAuthenticator
import com.serhii.core.security.Crypto
import com.serhii.core.utils.GoodUtils
import com.serhii.core.utils.GoodUtils.Companion.getText
import javax.crypto.Cipher

/**
 * Fragment where user enters login creds
 */
class LoginFragment : BaseFragment(TAG) {
    
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loginButton: Button
    private lateinit var registerAccountBtn: Button
    private lateinit var titleLabel: TextView
    private lateinit var fingerprintBtn: Button
    private lateinit var emailLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var description: TextView

    private var biometricAuthManager: BiometricAuthenticator? = null

    // We should specify ViewModelStoreOwner, because otherwise we get a different instance
    // of VM here. This will not be the same as we get in AuthorizationActivity.
    private val viewModel: LoginViewModel by viewModels ({ requireActivity() })

    private val keyEventActionDone = OnEditorActionListener { v, actionId, event ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            if (getText(passwordField).isEmpty() || getText(emailField).isEmpty()) {
                Toast.makeText(context, getString(R.string.empty_login), Toast.LENGTH_LONG).show()
                return@OnEditorActionListener true
            }

            // Set data
            val authModel = createModel(
                GoodUtils.getText(emailField),
                GoodUtils.getText(passwordField),
                AuthorizeType.AUTH_PASSWORD_LOGIN)

            viewModel.proceedWithAuth(requireContext().applicationContext, authModel)

            // For safety
            passwordField.setText("")
            return@OnEditorActionListener true
        }
        false
    }

    private val authListener = object : BiometricAuthenticator.Listener {

        override fun onSuccess(cipher: Cipher) {
            Log.info(TAG, "BiometricAuthenticator::onSuccess()")

            val activity = requireActivity()
            val appBaseActivity = activity as? AppBaseActivity

            val authModel = createModel(cipher, AuthorizeType.AUTH_BIOMETRIC_LOGIN)
            viewModel.proceedWithAuth(requireContext().applicationContext, authModel) {
                appBaseActivity?.showMessage(it)
            }
        }

        override fun onFailure() {
            Log.info(TAG, "BiometricAuthenticator::onFailure()")
            GoodUtils.showToast(requireContext(), R.string.biometric_toast_message)
        }
    }

    override fun onAttach(context: Context) {
        Log.info(TAG, "onAttach()")
        super.onAttach(context)
        if (BiometricAuthenticator.biometricsAvailable(context)) {
            biometricAuthManager = BiometricAuthenticator()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.info(TAG, "onCreateView()")
        val view = initView(inflater, container)
        val userName = NativeBridge.userName

        // If user name is not empty then user has already registered.
        // Otherwise, we should ask for registration.
        if (userName.isNotEmpty()) {
            Log.info(TAG, "onCreateView() user exists")

            emailField.setText(userName)
            registerAccountBtn.visibility = View.GONE

            description.visibility = View.GONE

            if (passwordField.requestFocus()) {
                // Show keyboard
                GoodUtils.showKeyboard(requireContext(), passwordField as View)
            }

            loginButton.visibility = View.VISIBLE
            passwordLayout.visibility = View.VISIBLE
            emailLayout.visibility = View.VISIBLE

            fingerprintBtn.visibility = if (biometricAuthManager != null
                && biometricAuthManager!!.isReady()) {
                 View.VISIBLE
            } else {
                View.GONE
            }
        } else {
            Log.info(TAG, "onCreateView() user doesn't exist")

            registerAccountBtn.visibility = View.VISIBLE
            description.visibility = View.VISIBLE
            passwordLayout.visibility = View.GONE
            emailLayout.visibility = View.GONE
        }

        fingerprintBtn.setOnClickListener {
            biometricAuthManager?.init(requireContext(), this,
                getString(R.string.biometric_prompt_title),
                getString(R.string.biometric_prompt_subtitle),
                getString(android.R.string.cancel))
            biometricAuthManager?.authenticate(authListener)
        }

        titleLabel.text = getString(R.string.title_login)
        registerAccountBtn.setOnClickListener { viewModel.requestRegistrationUI() }

        loginButton.setOnClickListener(View.OnClickListener {
            if (getText(passwordField).isEmpty() || getText(emailField).isEmpty()) {
                Toast.makeText(context, getString(R.string.empty_login), Toast.LENGTH_LONG).show()
                return@OnClickListener
            }

            // Set data
            val authModel = createModel(
                GoodUtils.getText(emailField),
                GoodUtils.getText(passwordField),
                AuthorizeType.AUTH_PASSWORD_LOGIN)

            viewModel.proceedWithAuth(requireContext().applicationContext, authModel)

            // For safety
            passwordField.setText("")
        })

        passwordField.setOnEditorActionListener(keyEventActionDone)
        return view
    }

    fun onUserAccountCreated() {
        registerAccountBtn.visibility = View.GONE
        description.visibility = View.GONE
        emailField.setText(NativeBridge.userName)
        passwordField.requestFocus()
    }

    private fun initView(inflater: LayoutInflater, viewGroup: ViewGroup?): View {
        val view = inflater.inflate(R.layout.fragment_login_view, viewGroup, false)
        // Set references
        emailField = view.findViewById(R.id.input_email)
        passwordField = view.findViewById(R.id.input_password)
        loginButton = view.findViewById(R.id.btn_login)
        registerAccountBtn = view.findViewById(R.id.btn_register)
        titleLabel = view.findViewById(R.id.title)
        fingerprintBtn = view.findViewById(R.id.btn_login_biometric)
        description = view.findViewById(R.id.description)
        emailLayout = view.findViewById(R.id.email_layout)
        passwordLayout = view.findViewById(R.id.password_layout)
        return view
    }

    companion object {
        private const val TAG = "LoginFragment"
        const val FRAGMENT_TAG = "LoginFragmentTag"

        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }
}