package io.nelipa.bmi.ui.activities.main

sealed class MainDestination(val destinationId: Int? = null) {
    object Destination: MainDestination(null)
    object TopDestination: MainDestination()
}