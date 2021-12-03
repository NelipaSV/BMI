package io.nelipa.bmi.utils.validation

import androidx.annotation.StringRes
import io.nelipa.bmi.R

enum class ValidationField(@StringRes val attrSpecNameRes: Int) {
    NAME(R.string.field_name)
}