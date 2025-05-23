package com.notes.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import api.PlatformAPIs.logger

const val TAG = "ViewModels"

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.getViewModel(navController: NavController): T {
    val parentNavGraph = destination.parent
    if (parentNavGraph == null) {
        logger.loge("$TAG::getViewModel: can't create view model")
        throw RuntimeException("Parent nav graph is null. Might be unexpected.")
    }

    val route = parentNavGraph.route
    if (route == null) {
        logger.logi("$TAG::getViewModel: no route, use default arguments")
        return viewModel<T>()
    }

    val parentEntry = remember(this) { navController.getBackStackEntry(route) }
    val vm = viewModel<T>(parentEntry)
    logger.logi("$TAG::getViewModel: created = $vm")
    return vm
}
