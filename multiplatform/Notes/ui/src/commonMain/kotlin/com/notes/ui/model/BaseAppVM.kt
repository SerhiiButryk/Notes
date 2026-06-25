package com.notes.ui.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.coroutines.CoroutineScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.Platform
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val tag = "BaseAppVM"

@Immutable
data class LoadingDialogState(
    val show: Boolean = false,
)

// This annotation could be redundant as
// the class is already stable, because all properties are stable.
// However, keep it for clarity.
@Stable
class DialogState(
    val title: String,
    val subtitle: String,
    val onConfirm: (() -> Unit)? = null
)

open class BaseAppVM(
    // For test support
    scopeOverride: CoroutineScope? = null
) : ViewModel() {

    protected val scope: CoroutineScope = scopeOverride ?: viewModelScope

    // Dialog ui state
    protected val _dialogState = MutableStateFlow<DialogState?>(null)
    val dialogState = _dialogState.asStateFlow()

    private val _dialogLoadingState = MutableStateFlow(LoadingDialogState())
    val dialogLoadingState = _dialogLoadingState.asStateFlow()

    protected suspend fun showDialog(
        title: String,
        subtitle: String,
        onConfirm: (() -> Unit)? = null
    ) {
        Platform().logger.logi("$tag::showDialog()")
        _dialogState.emit(
            DialogState(title = title, subtitle = subtitle, onConfirm = onConfirm)
        )
    }

    protected suspend fun showLoadingDialog() {
        _dialogLoadingState.emit(LoadingDialogState(true))
    }

    protected suspend fun dismissLoadingDialog() {
        _dialogLoadingState.emit(LoadingDialogState(false))
    }

    fun dismissDialog() {
        Platform().logger.logi("$tag::dismissDialog()")
        scope.launch {
            _dialogState.emit(null)
        }
    }

    override fun onCleared() {
        Platform().logger.logi("$tag::onCleared()")
        dismissDialog()
        scope.launch {
            dismissLoadingDialog()
        }
    }
}