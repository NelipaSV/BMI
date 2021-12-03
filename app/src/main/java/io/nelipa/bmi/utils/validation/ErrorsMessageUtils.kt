package io.nelipa.bmi.utils.validation

import android.content.res.Resources
import io.nelipa.bmi.R
import javax.inject.Inject

class ErrorsMessageUtils @Inject constructor(
    private val resources: Resources
) {
    private val stringResultInCaseOfError = "Generic error"
    private var errorsMap: Map<Int, Int> =
        mapOf(
            REQUIRED to R.string.error_required,
        )

    fun getErrorMessageByCode(code: Int, vararg args: String): String? {
        var errorResourceString = getStringByErrorCode(code)

        return if (errorResourceString?.isNotEmpty() == true) {
            if (isStringContainsPatternElements(errorResourceString)) {
                if (args.isNotEmpty()) {
                    errorResourceString = replacePatternWithArgsValues(errorResourceString, *args)
                }
                errorResourceString
            } else {
                errorResourceString
            }
        } else {
            stringResultInCaseOfError
        }
    }

    private fun isStringContainsPatternElements(stringToCheck: String): Boolean {
        return stringToCheck.contains("{") && stringToCheck.contains("}")
    }

    private fun replacePatternWithArgsValues(
        sourceStringToReplace: String,
        vararg args: String
    ): String? {
        var sourceString = sourceStringToReplace
        for (arg in args) {
            val indexPatternStart = sourceString.indexOf("{")
            val indexPatterEnd = sourceString.indexOf("}")
            val patternToReplace =
                sourceString.subSequence(indexPatternStart, indexPatterEnd + 1).toString()
            sourceString = sourceString.replace(patternToReplace, arg)
        }
        return sourceString
    }

    private fun getStringByErrorCode(code: Int): String? {
        return resources.getString(errorsMap[code] ?: 0)
    }
}