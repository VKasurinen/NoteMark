package com.vkasurinen.notemark.auth.data.repository

import android.util.Patterns
import com.vkasurinen.notemark.auth.domain.PatternValidator

object EmailPatternValidator: PatternValidator {

    override fun matches(value: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(value).matches()
    }
}