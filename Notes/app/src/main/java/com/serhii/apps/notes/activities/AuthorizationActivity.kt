/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.serhii.apps.notes.R
import com.serhii.apps.notes.common.App
import com.serhii.apps.notes.control.EventService
import com.serhii.apps.notes.control.NativeBridge
import com.serhii.apps.notes.control.auth.types.AuthResult
import com.serhii.apps.notes.ui.dialogs.DialogHelper
import com.serhii.apps.notes.ui.fragments.BlockFragment
import com.serhii.apps.notes.ui.fragments.LoginFragment
import com.serhii.apps.notes.ui.fragments.RegistrationFragment
import com.serhii.apps.notes.ui.view_model.LoginViewModel
import com.serhii.core.log.Log
import kotlinx.coroutines.launch

/**
 * Login activity for user authorization
 */
class AuthorizationActivity : AppBaseActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.info(TAG, "onCreate()")
        setLoggingTagForActivity(TAG)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorization)

        initNative()
        showLoginFragment(savedInstanceState)
        setupObservers()
    }

    override fun onBackPressed() {
        Log.info(TAG, "onBackPressed()")
        // When Registration fragment is displayed, back stack has 1 entry.
        // In this case, allow to go back. Otherwise if back stack doesn't have entries,
        // then move app to the background.
        if (supportFragmentManager.backStackEntryCount == 1) {
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
            if (NativeBridge.isAppBlocked) {
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
        loginViewModel.showRegistrationUI.observe(this) { shouldShowFragment ->
            if (shouldShowFragment) {
                Log.info(TAG, "addFragment(), adding Registration fragment")
                addFragment(RegistrationFragment(), RegistrationFragment.FRAGMENT_TAG, true)
            }
        }
    }

    private fun addFragment(fragment: Fragment, tag: String, shouldSave: Boolean) {
        if (supportFragmentManager.findFragmentByTag(tag) != null) {
            Log.info(TAG, "addFragment(), fragment $tag is already opened, return")
            return
        }
        val transaction = supportFragmentManager.beginTransaction()
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
        Log.info(TAG, "onAuthorize(), activity is finishing")
    }

    /**
     * Callback from native
     */
    fun userRegistered() {
        // Could be called from background thread
        lifecycleScope.launch(App.UI_DISPATCHER) {
            Log.info(TAG, "userRegistered() IN")
            // Close Registration UI
            onBackPressed()
            val loginFragment = supportFragmentManager.findFragmentByTag(LoginFragment.FRAGMENT_TAG) as LoginFragment?
            if (loginFragment != null) {
                loginFragment.onUserAccountCreated()
                EventService.onRegistrationDone(baseContext, lifecycleScope)
            } else {
                Log.error(TAG, "userRegistered(), loginFragment is null")
            }
        }
    }

    /**
     * Called from native to show a dialog
     */
    private fun showAlertDialog(type: Int) {
        Log.info(TAG, "showAlertDialog(), type $type")

        EventService.onErrorState(type) {
            Log.detail(TAG, "showAlertDialog(), show dialog")
            lifecycleScope.launch(App.UI_DISPATCHER) {
                DialogHelper.showDialog(type, this@AuthorizationActivity)
            }
        }

        if (type == AuthResult.WRONG_PASSWORD.typeId) {
            // If app gets blocked then show Block UI
            if (NativeBridge.isAppBlocked) {
                // The method is called from background thread.
                // So we need to move this call to UI thread
                lifecycleScope.launch(App.UI_DISPATCHER) {
                    Log.detail(TAG, "showAlertDialog(), add block fragment")
                    clearFragmentStack()
                    addFragment(BlockFragment(), BlockFragment.FRAGMENT_TAG, false)
                }
            }
        }
    }

    private fun clearFragmentStack() {
        val count = supportFragmentManager.backStackEntryCount
        for (i in 0 until count) {
            supportFragmentManager.popBackStack()
        }
    }

    /**
     * Native interface
     */
    private external fun initNative()

    companion object {
        private const val TAG = "AuthorizationActivity"
        init {
            System.loadLibrary(App.RUNTIME_LIBRARY)
        }
    }

}