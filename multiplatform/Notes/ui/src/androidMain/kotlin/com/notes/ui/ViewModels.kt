package com.notes.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import api.Platform

const val TAG = "ViewModels"

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.getViewModel(
    navController: NavController,
    factory: ViewModelProvider.Factory? = null,
): T {
    val parentNavGraph = destination.parent
    if (parentNavGraph == null) {
        Platform().logger.loge("$TAG::getViewModel: can't create view model")
        throw RuntimeException("Parent nav graph is null. Might be unexpected.")
    }

    val route = parentNavGraph.route
    if (route == null) {
        Platform().logger.logd("$TAG::getViewModel: no route, use default arguments")
        // If a factory was passed, use it here too as a fallback
        return if (factory != null) viewModel<T>(factory = factory) else viewModel<T>()
    }

    val parentEntry = remember(this) { navController.getBackStackEntry(route) }
    val vm = if (factory != null) {
        viewModel<T>(parentEntry, factory = factory)
    } else {
        viewModel<T>(parentEntry)
    }

    Platform().logger.logd("$TAG::getViewModel: created = $vm")
    return vm
}