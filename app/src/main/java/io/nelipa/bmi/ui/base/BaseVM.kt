package io.nelipa.bmi.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class BaseVM : ViewModel() {

    private val isLoading = MutableSharedFlow<Boolean>(replay = 1)
    internal fun getIsLoading(): Flow<Boolean> = isLoading

    internal inline fun launchWithLoading(
        context: CoroutineContext = viewModelScope.coroutineContext,
        crossinline block: suspend CoroutineScope.() -> Unit
    ) = viewModelScope.launch(context) {
        isLoading.emit(true)
        block()
        isLoading.emit(false)
    }
}