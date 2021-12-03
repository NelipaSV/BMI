package io.nelipa.bmi.ui.fragments.datails

import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import io.nelipa.bmi.models.Gender
import io.nelipa.bmi.ui.base.BaseVM
import io.nelipa.bmi.utils.calculate.CalculateUtils
import io.nelipa.bmi.utils.validation.ErrorFieldsModel
import io.nelipa.bmi.utils.validation.ValidationDelegate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsVM @Inject constructor(
    private val validationDelegate: ValidationDelegate,
    private val calculateUtils: CalculateUtils
) : BaseVM() {

    private lateinit var userName: String
    private lateinit var userBmiNumber: String
    private lateinit var userBmiResult: String

    private val errorFields = MutableStateFlow(ErrorFieldsModel())
    fun errorFields() = errorFields as Flow<ErrorFieldsModel>

    private val detailsDestination = MutableSharedFlow<DetailsDestination>()
    fun getDetailsDestination(): Flow<DetailsDestination> = detailsDestination

    val interstitialAdLoadCallback = object : InterstitialAdLoadCallback() {
        override fun onAdFailedToLoad(adError: LoadAdError) {
            viewModelScope.launch {
                detailsDestination.emit(DetailsDestination.FailToLoadAd)
            }
        }

        override fun onAdLoaded(interstitialAd: InterstitialAd) {
            viewModelScope.launch {
                detailsDestination.emit(DetailsDestination.AdLoaded(interstitialAd))
            }
        }
    }

    val fullScreenContentCallback = object : FullScreenContentCallback() {
        override fun onAdDismissedFullScreenContent() {
            viewModelScope.launch {
                detailsDestination.emit(
                    DetailsDestination.ToResults(
                        userName,
                        userBmiNumber,
                        userBmiResult
                    )
                )
            }
        }

        override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
            viewModelScope.launch {
                detailsDestination.emit(DetailsDestination.FailToLoadAd)
            }
        }

        override fun onAdShowedFullScreenContent() {
            viewModelScope.launch {
                detailsDestination.emit(DetailsDestination.ClearAd)
            }
        }
    }

    fun calculateBMI(name: String?, weight: Int, height: Int, gender: Gender) {
        launchWithLoading {
            val errorFieldsModel = ErrorFieldsModel()
            validationDelegate.validateName(errorFieldsModel, name)
            errorFields.emit(errorFieldsModel)
            if (errorFieldsModel.isValid) {
                val bmi = calculateUtils.calculateBMI(weight, height, gender)
                val bmiNumber = calculateUtils.convertBMIToResultString(bmi)
                val bmiResult = calculateUtils.getBMIResult(bmi)
                name?.let {
                    userName = it
                    userBmiNumber = bmiNumber
                    userBmiResult = bmiResult
                    detailsDestination.emit(DetailsDestination.ShowAd)
                }
            }
        }
    }
}