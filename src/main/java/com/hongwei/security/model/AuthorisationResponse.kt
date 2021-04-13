package com.hongwei.security.model

class AuthorisationResponse {
    val validated: Boolean? = null
    val validatedUntil: Long = LONG_TERM
    val userName: String? = null
    val role: String? = null
    val preferenceJson: String? = null
    val privilegeJson: String? = null
}