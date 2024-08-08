/*
 * Copyright 2023. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.fragment.app.viewModels
import com.serhii.apps.notes.R
import com.serhii.apps.notes.control.auth.types.UIRequestType
import com.serhii.apps.notes.ui.data_model.createModel
import com.serhii.apps.notes.ui.state_holders.LoginViewModel
import com.serhii.core.security.BiometricAuthenticator
import com.serhii.core.utils.GoodUtils
import com.serhii.core.utils.GoodUtils.Companion.getText

/**
 * Fragment where user registers itself
 */
class RegistrationFragment : BaseFragment("RegistrationFragment") {

    private lateinit var emailField: EditText
    private lateinit var titleField: TextView
    private lateinit var passwordField: EditText
    private lateinit var confirmPasswordField: EditText
    private lateinit var registerButton: Button

    private val keyEventActionDone = OnEditorActionListener { v, actionId, event ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            onProceed()
            return@OnEditorActionListener true
        }
        false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = initView(inflater, container)
        titleField.text = getString(R.string.title_reg)

        registerButton.setOnClickListener {
            onProceed()
        }

        if (emailField.requestFocus()) {
            GoodUtils.showKeyboard(requireContext(), emailField)
        }

        confirmPasswordField.setOnEditorActionListener(keyEventActionDone)
        return view
    }

    private fun initView(inflater: LayoutInflater, viewGroup: ViewGroup?): View {
        val view = inflater.inflate(R.layout.fragment_registration_view, viewGroup, false)

        // Set references
        emailField = view.findViewById(R.id.usr_email)
        titleField = view.findViewById(R.id.title)
        passwordField = view.findViewById(R.id.usr_password)
        confirmPasswordField = view.findViewById(R.id.confirm_password)
        registerButton = view.findViewById(R.id.btn_register)

        view.findViewById<View>(R.id.textInputLayout).visibility = View.VISIBLE
        view.findViewById<View>(R.id.textInputLayout2).visibility = View.VISIBLE
        view.findViewById<View>(R.id.textInputLayout3).visibility = View.VISIBLE

        registerButton.visibility = View.VISIBLE
        return view
    }

    private fun onProceed() {

        // We should specify ViewModelStoreOwner, because otherwise we get a different instance
        // of VM here. This will not be the same as we get in AuthorizationActivity.
        val viewModel: LoginViewModel by viewModels ({ requireActivity() })

        var biometricAuthenticator: BiometricAuthenticator? = null

        if (BiometricAuthenticator.biometricsAvailable(requireContext())) {
            biometricAuthenticator = BiometricAuthenticator()
            biometricAuthenticator.init(requireContext(), this,
                getString(R.string.biometric_prompt_title),
                getString(R.string.biometric_prompt_subtitle),
                getString(android.R.string.cancel))
        }

        val authModel = createModel(
            getText(emailField),
            getText(passwordField),
            getText(confirmPasswordField),
            UIRequestType.REGISTRATION
        )

//        viewModel.proceedWithRegistration(authModel, biometricAuthenticator, requireActivity())

        // For safety
        passwordField.setText("")
        confirmPasswordField.setText("")
    }

    companion object {
        const val FRAGMENT_TAG = "RegisterFragmentTag"
    }
}