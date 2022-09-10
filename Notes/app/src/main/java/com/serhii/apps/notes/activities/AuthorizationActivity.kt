/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.serhii.apps.notes.R
import com.serhii.apps.notes.common.AppConstants
import com.serhii.apps.notes.control.NativeBridge
import com.serhii.apps.notes.control.idle_lock.InactivityManager.cancelAlarm
import com.serhii.apps.notes.control.types.AuthResult
import com.serhii.apps.notes.ui.dialogs.DialogHelper
import com.serhii.apps.notes.ui.fragments.BlockFragment
import com.serhii.apps.notes.ui.fragments.LoginFragment
import com.serhii.apps.notes.ui.fragments.RegisterFragment
import com.serhii.apps.notes.ui.view_model.LoginViewModel
import com.serhii.core.log.Log
import com.serhii.core.log.Log.Companion.info

class AuthorizationActivity : AppCompatActivity() {

    private var loginViewModel: LoginViewModel? = null
    private lateinit var fragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
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

    override fun onResume() {
        info(TAG, "onResume()")
        super.onResume()
        // Cancel inactivity timer. This activity is exception from idle lock timeout.
        cancelAlarm(this)
    }

    override fun onBackPressed() {
        info(TAG, "onBackPressed()")
        // Allow user to go back to the Login UI
        // if there is one fragment in the stack which means that Registration UI is displayed
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
                addFragment(blockFragment, null, false)
            } else {
                val loginFragment = LoginFragment.newInstance()
                addFragment(loginFragment, LoginFragment.FRAGMENT_TAG, false)
            }
        }
    }

    private fun setupObservers() {
        // Observer changes
        loginViewModel?.let {
            it.showRegistrationUISetFlag.observe(this) { shouldPerformAction -> // Do action if it is needed
                if (shouldPerformAction) {
                    info(TAG, "addFragment(), open Registration fragment")
                    addFragment(RegisterFragment(), RegisterFragment.FRAGMENT_TAG, true)
                }
            }
        }
    }

    private fun addFragment(fragment: Fragment, tag: String?, shouldSave: Boolean) {
        if (tag != null && fragmentManager.findFragmentByTag(tag) != null) {
            info(TAG, "addFragment(), fragment is already opened")
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
     * Called from native
     */
    fun onAuthorizationFinished() {
        // Close activity
        finish()
        info(TAG, "onAuthorize(), activity finished")
    }

    /**
     * Called from native
     */
    fun userRegistered() {
        info(TAG, "userRegistered()")
        // Close Registration UI
        onBackPressed()
        val loginFragment =
            fragmentManager.findFragmentByTag(LoginFragment.FRAGMENT_TAG) as LoginFragment?
        if (loginFragment != null) {
            loginFragment.onUserAccountCreated()
            val authorizeService = loginViewModel?.authorizeService
            authorizeService?.onUserRegistered(this)
        } else {
            Log.error(TAG, "userRegistered(), loginFragment is null")
        }
    }

    /**
     * Called from native
     */
    private fun showAlertDialog(type: Int) {
        info(TAG, "showAlertDialog(), type $type")
        var shouldShowDialog = false
        if (type == AuthResult.WRONG_PASSWORD.typeId) {
            val nativeBridge = NativeBridge()
            val currentLimit = nativeBridge.limitLeft
            // If limit is exceeded then need to block application
            if (currentLimit == 1) {
                // Block application
                nativeBridge.executeBlockApp()
                clearFragmentStack()
                addFragment(BlockFragment(), null, false)
                shouldShowDialog = true
                info(TAG, "showAlertDialog(), BB SS")
            } else {
                // Update password limit value
                nativeBridge.limitLeft = currentLimit - 1
                info(TAG, "showAlertDialog(), AA SS")
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
            System.loadLibrary(AppConstants.RUNTIME_LIBRARY)
        }
    }

}