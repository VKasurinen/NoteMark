package com.vkasurinen.notemark.auth.domain

interface PatternValidator {
    fun matches(value: String): Boolean
}