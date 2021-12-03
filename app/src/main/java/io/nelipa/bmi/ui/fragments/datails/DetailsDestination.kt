package io.nelipa.bmi.ui.fragments.datails

import com.google.android.gms.ads.interstitial.InterstitialAd

sealed class DetailsDestination {
    class ToResults(val name: String, val bmiNumber: String, val bmiResult: String): DetailsDestination()
    object ClearAd: DetailsDestination()
    object FailToLoadAd: DetailsDestination()
    class AdLoaded(val ad: InterstitialAd): DetailsDestination()
    object ShowAd: DetailsDestination()
}