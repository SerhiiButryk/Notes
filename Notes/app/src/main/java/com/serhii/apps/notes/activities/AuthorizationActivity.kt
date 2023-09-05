/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.serhii.apps.notes.R
import com.serhii.apps.notes.common.AppDetails
import com.serhii.apps.notes.control.NativeBridge
import com.serhii.apps.notes.control.auth.types.AuthResult
import com.serhii.apps.notes.ui.dialogs.DialogHelper
import com.serhii.apps.notes.ui.fragments.BlockFragment
import com.serhii.apps.notes.ui.fragments.LoginFragment
import com.serhii.apps.notes.ui.fragments.RegistrationFragment
import com.serhii.apps.notes.ui.view_model.LoginViewModel
import com.serhii.core.log.Log
import com.serhii.core.log.Log.Companion.detail
import com.serhii.core.log.Log.Companion.info

/**
 * Login activity for user authorization
 */
class AuthorizationActivity : AppBaseActivity() {

    private var loginViewModel: LoginViewModel? = null
    private lateinit var fragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        setLoggingTagForActivity(TAG)

        info(TAG, "onCreate() IN")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorization)

        initNative()

        fragmentManager = supportFragmentManager
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        showLoginFragment(savedInstanceState)
        setupObservers()

        info(TAG, "onCreate() OUT")
    }

    override fun onBackPressed() {
        info(TAG, "onBackPressed()")
        // When Registration fragment is displayed, back stack has 1 entry.
        // In this case, allow to go back. Otherwise if back stack doesn't have entries,
        // then move app to background.
        if (fragmentManager.backStackEntryCount == 1) {
            super.onBackPressed()
        } else {
            moveTaskToBack(true)
        }
    }

    private fun showLoginFragment(savedInstanceState: Bundle?) {
        // Ensure that the fragment is added only once when the activity
        // is launched the first time. When configuration is changed the fragment
        // doesn't need to be added as it is restored from the 'savedInstanceState' Bundle
        if (savedInstanceState == null) {
            val nativeBridge = NativeBridge()
            if (nativeBridge.isAppBlocked) {
                val blockFragment = BlockFragment()
                addFragment(blockFragment, BlockFragment.FRAGMENT_TAG, false)
            } else {
                val loginFragment = LoginFragment.newInstance()
                addFragment(loginFragment, LoginFragment.FRAGMENT_TAG, false)
            }
        }
    }

    private fun setupObservers() {
        // Observer changes
        loginViewModel?.let { viewmodel ->
            viewmodel.showRegistrationUIFlag.observe(this) { shouldShowFragment ->
                if (shouldShowFragment) {
                    info(TAG, "addFragment(), adding Registration fragment")
                    addFragment(RegistrationFragment(), RegistrationFragment.FRAGMENT_TAG, true)
                }
            }
        }
    }

    private fun addFragment(fragment: Fragment, tag: String, shouldSave: Boolean) {
        if (fragmentManager.findFragmentByTag(tag) != null) {
            info(TAG, "addFragment(), fragment $tag is already opened, return")
            return
        }
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.main_layout, fragment, tag)
        transaction.setReorderingAllowed(true) // Needed for optimization
        if (shouldSave) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

    /**
     * Callback from native
     */
    fun onAuthorizationFinished() {
        // Close activity
        finish()
        info(TAG, "onAuthorize(), activity is finishing")
    }

    /**
     * Callback from native
     */
    fun userRegistered() {
        info(TAG, "userRegistered() IN")
        // Close Registration UI
        onBackPressed()
        val loginFragment = fragmentManager.findFragmentByTag(LoginFragment.FRAGMENT_TAG) as LoginFragment?
        if (loginFragment != null) {
            loginFragment.onUserAccountCreated()
            val authorizeService = loginViewModel?.authorizeService
            authorizeService?.onUserRegistered(this)
        } else {
            Log.error(TAG, "userRegistered(), loginFragment is null")
        }
    }

    /**
     * Called from native to show a dialog
     */
    private fun showAlertDialog(type: Int) {
        info(TAG, "showAlertDialog(), type $type")
        var shouldShowDialog = true
        if (type == AuthResult.WRONG_PASSWORD.typeId) {
            val nativeBridge = NativeBridge()
            val currentLimit = nativeBridge.limitLeft
            // If limit is exceeded then need to block application
            if (currentLimit == 1) {
                // Block application
                nativeBridge.executeBlockApp()
                clearFragmentStack()
                addFragment(BlockFragment(), BlockFragment.FRAGMENT_TAG, false)
                // Block Ui is going to be shown. So do not show dialog.
                shouldShowDialog = false
                detail(TAG, "showAlertDialog(), add block fragment")
            } else {
                // Update password limit value
                nativeBridge.limitLeft -= 1
                detail(TAG, "showAlertDialog(), updated limit")
            }
        }
        if (shouldShowDialog) {
            info(TAG, "showAlertDialog(), show dialog")
            DialogHelper.showAlertDialog(type, this)
        }
    }

    private fun clearFragmentStack() {
        val count = fragmentManager.backStackEntryCount
        for (i in 0 until count) {
            fragmentManager.popBackStack()
        }
    }

    /**
     * Native interface
     */
    private external fun initNative()

    companion object {
        private const val TAG = "AuthorizationActivity"
        init {
            System.loadLibrary(AppDetails.RUNTIME_LIBRARY)
        }
    }

}