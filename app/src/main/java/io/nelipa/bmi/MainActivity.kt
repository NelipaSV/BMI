package io.nelipa.bmi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import io.nelipa.bmi.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSpannableText()
        setClickListeners()
    }

    private fun setSpannableText() {
        val index = "18.61"
        val ss = SpannableString(index)
        ss.setSpan(
            RelativeSizeSpan(2f),
            0, index.indexOf('.'),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding?.tvBodyMassIndexNumber?.text = ss
    }

    private fun setClickListeners() {
        binding?.apply {
            btnRate.setOnClickListener {  }
            btnShare.setOnClickListener {  }
        }
    }
}