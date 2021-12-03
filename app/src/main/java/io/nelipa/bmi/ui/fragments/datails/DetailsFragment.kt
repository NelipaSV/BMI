package io.nelipa.bmi.ui.fragments.datails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenResumed
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.interstitial.InterstitialAd
import dagger.hilt.android.AndroidEntryPoint
import io.nelipa.bmi.R
import io.nelipa.bmi.databinding.FragmentDetailsBinding
import io.nelipa.bmi.ext.hideKeyboard
import io.nelipa.bmi.models.Gender
import io.nelipa.bmi.ui.base.BaseFragment
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class DetailsFragment : BaseFragment() {

    private var binding: FragmentDetailsBinding? = null
    private lateinit var genders: Array<String>

    private val detailsVM: DetailsVM by viewModels()

    private var mInterstitialAd: InterstitialAd? = null
    private lateinit var adRequest: AdRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adRequest = AdRequest.Builder().build()
        lifecycleScope.launchWhenStarted { loadAd() }

        subscribeToFieldsErrors()
        subscribeToLoading()
        subscribeToDetailsDestination()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setClickListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun subscribeToFieldsErrors() {
        detailsVM.errorFields()
            .onEach { errorModel ->
                whenResumed {
                    binding?.errors = errorModel
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun subscribeToDetailsDestination() {
        detailsVM.getDetailsDestination()
            .onEach { destination ->
                whenResumed {
                    when (destination) {
                        is DetailsDestination.ToResults -> {
                            navigate(
                                DetailsFragmentDirections.actionDetailsFragmentToResultFragment(
                                    destination.name,
                                    destination.bmiNumber,
                                    destination.bmiResult
                                )
                            )
                        }
                        DetailsDestination.ClearAd -> mInterstitialAd = null
                        DetailsDestination.FailToLoadAd -> {
                            mInterstitialAd = null
                            loadAd()
                        }
                        is DetailsDestination.AdLoaded -> {
                            mInterstitialAd = destination.ad
                            mInterstitialAd?.fullScreenContentCallback =
                                detailsVM.fullScreenContentCallback
                        }
                        DetailsDestination.ShowAd -> {
                            if (mInterstitialAd != null) {
                                mInterstitialAd?.show(requireActivity())
                            } else {
                                loadAd()
                            }
                        }
                    }
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun subscribeToLoading() {
        detailsVM.getIsLoading()
            .onEach { loading ->
                whenResumed {
                    binding?.btnCalculate?.isEnabled = loading.not()
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun setupUI() {
        genders = resources.getStringArray(R.array.genders)

        binding?.apply {
            viewToolbar.tvTitle.text = getString(R.string.title_details)

            npWeight.apply {
                maxValue = 300
                minValue = 30
                value = 45
            }

            npHeight.apply {
                maxValue = 250
                minValue = 50
                value = 150
            }

            npGender.apply {
                setFormatter { position ->
                    genders[position]
                }
                displayedValues = genders
                maxValue = genders.size - 1
                minValue = 0
            }
        }
    }

    private fun setClickListeners() {
        binding?.apply {
            viewToolbar.ivBackArrow.setOnClickListener {
                activity?.finish()
            }

            clDetails.setOnClickListener {
                etName.hideKeyboard()
                etName.clearFocus()
            }

            btnCalculate.setOnClickListener {
                val weight = npWeight.value
                val height = npHeight.value
                val gender = Gender.toGender(npGender.value)
                detailsVM.calculateBMI(
                    etName.text?.toString(),
                    weight,
                    height,
                    gender
                )
            }
        }
    }

    private fun loadAd() {
        context?.let { context ->
            InterstitialAd.load(
                context, "ca-app-pub-3940256099942544/1033173712",
                adRequest, detailsVM.interstitialAdLoadCallback
            )
        }
    }
}