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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.textfield.TextInputLayout
import com.serhii.apps.notes.R
import com.serhii.apps.notes.control.NativeBridge
import com.serhii.apps.notes.control.auth.BiometricAuthManager
import com.serhii.apps.notes.control.auth.BiometricAuthManager.OnAuthenticateListener
import com.serhii.apps.notes.control.auth.types.AuthorizeType
import com.serhii.apps.notes.ui.data_model.AuthCredsModel
import com.serhii.apps.notes.ui.view_model.LoginViewModel
import com.serhii.core.log.Log.Companion.info
import com.serhii.core.security.Hash
import com.serhii.core.utils.GoodUtils
import com.serhii.core.utils.GoodUtils.Companion.getText

/**
 * Fragment where user enters login creds
 */
class LoginFragment : Fragment() {
    
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loginButton: Button
    private lateinit var registerAccountBtn: Button
    private lateinit var titleLabel: TextView
    private lateinit var fingerprintBtn: Button
    private lateinit var emailLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var description: TextView
    private val biometricAuthManager = BiometricAuthManager()
    private var isFingerprintAvailable = false
    private val nativeBridge = NativeBridge()

    private val keyEventActionDone = OnEditorActionListener { v, actionId, event ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            if (getText(passwordField).isEmpty() || getText(emailField).isEmpty()) {
                Toast.makeText(context, getString(R.string.empty_login), Toast.LENGTH_LONG).show()
                return@OnEditorActionListener true
            }
            // We should specify ViewModelStoreOwner, because otherwise we get a different instance
            // of VM here. This will not be the same as we get in AuthorizationActivity.
            val viewModel: LoginViewModel by viewModels ({ requireActivity() })
            // Set data
            viewModel.setAuthValue(createModel(AuthorizeType.AUTH_PASSWORD_LOGIN))
            return@OnEditorActionListener true
        }
        false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (biometricAuthManager.canAuthenticate(context) && biometricAuthManager.hasFingerPrint(context)) {
            biometricAuthManager.initBiometricSettings(context, this)
            isFingerprintAvailable = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        info(TAG, "onCreateView()")
        val view = initView(inflater, container)
        val userName = nativeBridge.userName

        // If user name is not empty then user is already registered.
        // Otherwise, we should ask for registration.
        if (userName.isNotEmpty()) {
            info(TAG, "onCreateView() user exists")

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

            if (isFingerprintAvailable) {
                fingerprintBtn.visibility = View.VISIBLE
            } else {
                fingerprintBtn.visibility = View.GONE
            }
        } else {
            info(TAG, "onCreateView() user doesn't exist")

            registerAccountBtn.visibility = View.VISIBLE
            description.visibility = View.VISIBLE
            passwordLayout.visibility = View.GONE
            emailLayout.visibility = View.GONE
        }

        fingerprintBtn.setOnClickListener { biometricAuthManager.authenticate() }

        biometricAuthManager.setOnAuthenticateSuccess(object : OnAuthenticateListener {
            override fun onSuccess() {
                // We should specify ViewModelStoreOwner, because otherwise we get a different instance
                // of VM here. This will not be the same as we get in AuthorizationActivity.
                val viewModel: LoginViewModel by viewModels ({ requireActivity() })
                // Set data
                viewModel.setAuthValue(createEmptyModel(AuthorizeType.AUTH_BIOMETRIC_LOGIN))
            }
        })

        // We should specify ViewModelStoreOwner, because otherwise we get a different instance
        // of VM here. This will not be the same as we get in AuthorizationActivity.
        val viewModel: LoginViewModel by viewModels ({ requireActivity() })

        titleLabel.text = getString(R.string.title_login)
        registerAccountBtn.setOnClickListener { viewModel.requestRegistrationUI() }

        loginButton.setOnClickListener(View.OnClickListener {
            if (getText(passwordField).isEmpty() || getText(emailField).isEmpty()) {
                Toast.makeText(context, getString(R.string.empty_login), Toast.LENGTH_LONG).show()
                return@OnClickListener
            }

            // Set data
            viewModel.setAuthValue(createModel(AuthorizeType.AUTH_PASSWORD_LOGIN))
        })

        passwordField.setOnEditorActionListener(keyEventActionDone)
        return view
    }

    fun onUserAccountCreated() {
        registerAccountBtn.visibility = View.GONE
        description.visibility = View.GONE
        emailField.setText(nativeBridge.userName)
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

    private fun createModel(type: AuthorizeType): AuthCredsModel {
        val hash = Hash()
        val authModel = AuthCredsModel(
            getText(emailField),
            hash.hashMD5(getText(passwordField)),
            "", type
        )
        // For safety
        passwordField.setText("")
        return authModel
    }

    private fun createEmptyModel(type: AuthorizeType): AuthCredsModel {
        return AuthCredsModel("", "", "", type)
    }

    companion object {
        private const val TAG = "LoginFragment"
        const val FRAGMENT_TAG = "LoginFragmentTag"

        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }
}