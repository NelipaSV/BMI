package io.nelipa.bmi.ui.fragments.result

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import dagger.hilt.android.AndroidEntryPoint
import io.nelipa.bmi.R
import io.nelipa.bmi.databinding.FragmentResultBinding
import io.nelipa.bmi.ui.base.BaseFragment
import io.nelipa.bmi.utils.bitmap.ScreenshotUtils
import javax.inject.Inject

@AndroidEntryPoint
class ResultFragment: BaseFragment()  {

    private var binding: FragmentResultBinding? = null

    private val args: ResultFragmentArgs by navArgs()

    @Inject
    lateinit var screenshotUtils: ScreenshotUtils

    private var currentNativeAd: NativeAd? = null
    private val ADMOB_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenStarted { refreshAd() }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentResultBinding.inflate(inflater, container, false)
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
        currentNativeAd?.destroy()
    }

    private fun setupUI() {
        binding?.apply {
            viewToolbar.tvTitle.text = getString(R.string.title_result)
            tvBodyMassIndexNumber.text = setSpannableBMINumber(args.bmi)
            tvBodyMassIndexDescription.text = getString(R.string.hello_name, args.name, args.result)
        }
    }

    private fun setClickListeners() {
        binding?.apply {
            viewToolbar.ivBackArrow.setOnClickListener {
                popFragment()
            }

            btnRate.setOnClickListener {
                openRate()
            }
            btnShare.setOnClickListener {
                shareScreen()
            }
        }
    }

    private fun setSpannableBMINumber(bmi: String) : SpannableString {
        val ss = SpannableString(bmi)
        ss.setSpan(
            RelativeSizeSpan(2f),
            0, bmi.indexOf('.'),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return ss
    }

    private fun openRate() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.codium.bmicalculator&hl=uk&gl=US"))
        startActivity(browserIntent)
    }

    private fun shareScreen() {
        val rootView = activity?.window?.decorView?.rootView
        rootView?.let {
            val bitmapScreen = screenshotUtils.takeScreenshot(it)
            bitmapScreen?.let { bitmap ->
                val fileToShare = screenshotUtils.saveBitmap(bitmap, activity?.localClassName)
                fileToShare?.let{
                    val sharingIntent = Intent(Intent.ACTION_SEND)
                    sharingIntent.type = "image/*"
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, fileToShare)
                    startActivity(Intent.createChooser(sharingIntent, "Share via"))
                }
            }
        }
    }

    private fun refreshAd() {

        val builder = AdLoader.Builder(requireActivity(), ADMOB_AD_UNIT_ID)

        builder.forNativeAd { nativeAd ->
            if (requireActivity().isDestroyed || requireActivity().isFinishing || requireActivity().isChangingConfigurations) {
                nativeAd.destroy()
                return@forNativeAd
            }
            currentNativeAd?.destroy()
            currentNativeAd = nativeAd
            displayNativeAd(nativeAd)
        }

        val videoOptions = VideoOptions.Builder()
            .build()

        val adOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .build()

        builder.withNativeAdOptions(adOptions)

        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                Log.d("AdListener", "loadAdError $loadAdError")
            }
        }).build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun displayNativeAd(ad: NativeAd) {
        binding?.adView?.callToActionView = binding?.adView?.findViewById(R.id.btn_ad)

        binding?.tvAdHeadline?.text = ad.headline
        binding?.tvAdDescription?.text = ad.body
        binding?.btnAd?.text = ad.callToAction

        if (ad.icon == null) {
            binding?.ivAdPhoto?.visibility = View.GONE
        } else {
            binding?.ivAdPhoto?.setImageDrawable(
                ad.icon?.drawable
            )
            binding?.ivAdPhoto?.visibility = View.VISIBLE
        }
        binding?.adView?.setNativeAd(ad)
    }
}