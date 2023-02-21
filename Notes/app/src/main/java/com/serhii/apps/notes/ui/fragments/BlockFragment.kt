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
import android.widget.TextView.OnEditorActionListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.serhii.apps.notes.R
import com.serhii.apps.notes.control.auth.types.AuthorizeType
import com.serhii.apps.notes.ui.data_model.AuthModel
import com.serhii.apps.notes.ui.utils.TextChecker
import com.serhii.apps.notes.ui.view_model.LoginViewModel
import com.serhii.core.security.Hash
import com.serhii.core.utils.GoodUtils.Companion.getText

/**
 * Fragment which block application
 */
class BlockFragment : Fragment() {

    private lateinit var accessKeyField: EditText
    private lateinit var loginViewModel: LoginViewModel

    private val keyEventActionDone = OnEditorActionListener { v, actionId, event ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            unlockApplication()
            return@OnEditorActionListener true
        }
        false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // Retrieve an instance of ViewModel
        loginViewModel = ViewModelProvider(requireActivity()).get(LoginViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View
    {
        return initView(inflater, container)
    }

    private fun initView(inflater: LayoutInflater, container: ViewGroup?): View {

        val view = inflater.inflate(R.layout.fragment_block_view, container, false)

        accessKeyField = view.findViewById(R.id.access_key)

        val ok = view.findViewById<Button>(R.id.btn_login)
        ok.isEnabled = false

        accessKeyField.addTextChangedListener(TextChecker(accessKeyField, ok))
        accessKeyField.setOnEditorActionListener(keyEventActionDone)

        ok.setOnClickListener { unlockApplication() }
        ok.visibility = View.VISIBLE

        view.findViewById<View>(R.id.email_layout).visibility = View.VISIBLE

        return view
    }

    private fun unlockApplication() {
        val hash = Hash()
        val authModel = AuthModel("", hash.hashMD5(getText(accessKeyField)), "", AuthorizeType.AUTH_UNLOCK)
        // For safety
        accessKeyField.setText("")
        // Set data
        loginViewModel.setAuthValue(authModel)
    }

    companion object {
        const val FRAGMENT_TAG = "BlockFragmentTag"
    }
}