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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.serhii.apps.notes.R
import com.serhii.apps.notes.control.auth.types.AuthorizeType
import com.serhii.apps.notes.ui.data_model.AuthCredsModel
import com.serhii.apps.notes.ui.view_model.LoginViewModel
import com.serhii.core.utils.GoodUtils
import com.serhii.core.utils.GoodUtils.Companion.getText

/**
 * Fragment where user registers itself
 */
class RegistrationFragment : Fragment() {

    private lateinit var emailField: EditText
    private lateinit var titleField: TextView
    private lateinit var passwordField: EditText
    private lateinit var confirmPasswordField: EditText
    private lateinit var registerButton: Button

    private val keyEventActionDone = OnEditorActionListener { v, actionId, event ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            // We should specify ViewModelStoreOwner, because otherwise we get a different instance
            // of VM here. This will not be the same as we get in AuthorizationActivity.
            val viewModel: LoginViewModel by viewModels ({ requireActivity() })
            // Set data
            viewModel.setAuthValue(createModel(AuthorizeType.AUTH_REGISTRATION))
            return@OnEditorActionListener true
        }
        false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // We should specify ViewModelStoreOwner, because otherwise we get a different instance
        // of VM here. This will not be the same as we get in AuthorizationActivity.
        val viewModel: LoginViewModel by viewModels ({ requireActivity() })

        val view = initView(inflater, container)
        titleField.text = getString(R.string.title_reg)

        registerButton.setOnClickListener {
            viewModel.setAuthValue(createModel(AuthorizeType.AUTH_REGISTRATION))
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

    private fun createModel(type: AuthorizeType): AuthCredsModel {
        return AuthCredsModel(getText(emailField), getText(passwordField), getText(confirmPasswordField), type)
    }

    companion object {
        const val FRAGMENT_TAG = "RegisterFragmentTag"
    }
}