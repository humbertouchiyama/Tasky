package com.humberto.tasky.auth.domain

class UserDataValidator(
    private val patternValidator: PatternValidator
) {

    fun isValidEmail(email: String): Boolean {
        return patternValidator.matches(email.trim())
    }

    fun isValidFullName(fullName: String): Boolean {
        val hasMinLength = fullName.length >= MIN_FULL_NAME_LENGTH
        val hasMaxLength = fullName.length <= MAX_FULL_NAME_LENGTH
        return hasMinLength && hasMaxLength
    }

    fun validatePassword(password: String): PasswordValidationState {
        val hasMinLength = password.length >= MIN_PASSWORD_LENGTH
        val hasDigit = password.any { it.isDigit() }
        val hasLowerCaseCharacter = password.any { it.isLowerCase() }
        val hasUpperCaseCharacter = password.any { it.isUpperCase() }

        return PasswordValidationState(
            hasMinLength = hasMinLength,
            hasNumber = hasDigit,
            hasLowerCaseCharacter = hasLowerCaseCharacter,
            hasUpperCaseCharacter = hasUpperCaseCharacter
        )
    }

    companion object {
        const val MIN_FULL_NAME_LENGTH = 4
        const val MAX_FULL_NAME_LENGTH = 50
        const val MIN_PASSWORD_LENGTH = 9
    }
}