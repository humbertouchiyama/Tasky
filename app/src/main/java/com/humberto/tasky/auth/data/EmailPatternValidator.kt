package com.humberto.tasky.auth.data

import android.util.Patterns
import com.humberto.tasky.auth.domain.PatternValidator

object EmailPatternValidator: PatternValidator {
    override fun matches(value: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(value).matches()
    }
}