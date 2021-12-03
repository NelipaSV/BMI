package io.nelipa.bmi.application

import android.app.Application
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AppBMI: Application() {

    override fun onCreate() {
        super.onCreate()

        MobileAds.initialize(this)
    }
}