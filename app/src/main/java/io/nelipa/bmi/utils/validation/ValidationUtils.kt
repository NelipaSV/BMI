package io.nelipa.bmi.utils.validation

import android.content.res.Resources
import android.text.TextUtils
import android.util.Patterns
import io.nelipa.bmi.utils.validation.*
import javax.inject.Inject

class ValidationUtils @Inject constructor(
    private val errorsMessageUtils: ErrorsMessageUtils,
    private val resources: Resources
) {

    private fun getString(res: Int) = resources.getString(res)

    fun validateFieldOnRequired(validationField: ValidationField, value: String?): String? {
        return if (value.isNullOrBlank()) {
            errorsMessageUtils.getErrorMessageByCode(REQUIRED, getString(validationField.attrSpecNameRes))
        } else null
    }
}