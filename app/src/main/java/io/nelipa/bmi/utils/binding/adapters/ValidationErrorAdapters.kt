package io.nelipa.bmi.utils.binding.adapters

import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout
import io.nelipa.bmi.utils.validation.ValidationField

@BindingAdapter("app:errorText", "app:errorField")
fun setErrorMessage(
    view: TextInputLayout,
    errorsMap: Map<ValidationField?, String?>?,
    validationField: ValidationField?
) {
    view.isErrorEnabled = errorsMap != null && errorsMap.containsKey(validationField)
    view.error = errorsMap?.get(validationField)
}