package com.notes.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController

const val TAG = "ViewModels"

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.getViewModel(navController: NavController): T {

    val parentNavGraph = destination.parent
    if (parentNavGraph == null) {
        Log.e(TAG, "getViewModel: can't create view model")
        throw RuntimeException("Parent nav graph is null. Might be unexpected.")
    }

    val route = parentNavGraph.route
    if (route == null) {
        Log.i(TAG, "getViewModel: no route, use default arguments")
        return viewModel()
    }

    val parentEntry = remember(this) { navController.getBackStackEntry(route) }
    val vm = hiltViewModel<T>(parentEntry)
    Log.i(TAG, "getViewModel: created = $vm")
    return vm
}