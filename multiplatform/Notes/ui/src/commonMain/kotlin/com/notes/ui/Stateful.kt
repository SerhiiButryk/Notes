package com.notes.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notes.ui.model.BaseAppVM

@Composable
fun AlertDialogStateful(viewModel: BaseAppVM) {
    val dialogState = viewModel.dialogState.collectAsState()
    val dialogValue = dialogState.value
    if (dialogValue != null) {
        AlertDialogUI(
            onDismissRequest = { viewModel.dismissDialog() },
            onConfirmation = {
                viewModel.dismissDialog()
                dialogValue.onConfirm?.invoke()
            },
            dialogTitle = dialogValue.title,
            dialogText = dialogValue.subtitle,
        )
    }
}

@Composable
fun LoadingDialogStateful(viewModel: BaseAppVM) {
    val dialogLoadingState = viewModel.dialogLoadingState.collectAsStateWithLifecycle()
    LoadingDialog(dialogLoadingState.value.show)
}
