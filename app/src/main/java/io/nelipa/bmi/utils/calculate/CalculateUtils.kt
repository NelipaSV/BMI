package io.nelipa.bmi.utils.calculate

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.nelipa.bmi.R
import io.nelipa.bmi.models.Gender
import java.lang.RuntimeException
import java.util.*
import javax.inject.Inject

class CalculateUtils @Inject constructor(
    @ApplicationContext private val context: Context
){

    fun calculateBMI(
        weightKg: Int,
        heightCm: Int,
        gender: Gender
    ) : Float {
        val m2 = (heightCm.toFloat() / 100) * 2
        val bmi = weightKg / m2
        return bmi

    }

    fun convertBMIToResultString(bmi: Float) : String {
        return try {
            String.format(Locale.ENGLISH, "%.2f", bmi)
        } catch (e: RuntimeException) {
            ""
        }
    }

    fun getBMIResult(bmi: Float): String {
        return when {
            bmi < 18.5 -> context.getString(R.string.underweight)
            bmi > 25.0 -> context.getString(R.string.overweight)
            else -> context.getString(R.string.normal)
        }
    }
}