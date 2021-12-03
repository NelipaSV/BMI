package io.nelipa.bmi.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDestination
import androidx.navigation.NavDirections
import io.nelipa.bmi.ui.activities.main.MainDestination
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

abstract class BaseActivityVM: ViewModel() {

    private val eventNavigate = MutableSharedFlow<NavDirections>()
    internal fun subscribeForNavigationEvent(): Flow<NavDirections> = eventNavigate

    private val eventPopBack = MutableSharedFlow<MainDestination>()
    internal fun subscribeForPopBackEvent(): Flow<MainDestination> = eventPopBack

    private val eventPopBackTo = MutableSharedFlow<Int>()
    internal fun subscribeForPopBackToEvent(): Flow<Int> = eventPopBackTo

    internal fun navigateTo(currentDestination: NavDestination?, directionTo: NavDirections) {
        viewModelScope.launch {
            if (currentDestination != directionTo) {
                eventNavigate.emit(directionTo)
            }
        }
    }

    internal fun popBack(destination: MainDestination) {
        viewModelScope.launch {
            if (destination is MainDestination.TopDestination) {
                eventPopBack.emit(MainDestination.TopDestination)
            } else {
                destination.destinationId?.let { destinationId ->
                    eventPopBackTo.emit(destinationId)
                } ?: eventPopBack.emit(MainDestination.TopDestination)
            }
        }
    }
}