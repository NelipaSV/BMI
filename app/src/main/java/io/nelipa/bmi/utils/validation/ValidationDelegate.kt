package io.nelipa.bmi.utils.validation

import javax.inject.Inject

class ValidationDelegate @Inject constructor(
    private val validationUtils: ValidationUtils
) {

    fun validateName(errorFieldsModel: ErrorFieldsModel, name: String?) {
        validationUtils.validateFieldOnRequired(ValidationField.NAME, name)?.let {
            errorFieldsModel.addError(ValidationField.NAME, it)
        }
    }
}