package com.hongwei.security.model

import com.google.gson.Gson
import com.hongwei.model.response.GenericResponseBody

private const val GENERIC_UNAUTHORIZED_CODE = 0
private const val GENERIC_UNAUTHORIZED_CAUSE = "AUTHORISATION FAILURE"

private const val TOKEN_EXPIRED_CODE = 1
private const val TOKEN_EXPIRED_CAUSE = "TOKEN EXPIRED"

private const val NO_PERMISSION_CODE = 2
private const val NO_PERMISSION_CAUSE = "NO PERMISSION"

object SubCodeResponseFactory {
    fun genericUnauthorizedError(): String =
            Gson().toJson(GenericResponseBody(
                    subCode = GENERIC_UNAUTHORIZED_CODE,
                    message = GENERIC_UNAUTHORIZED_CAUSE
            ))

    fun tokenExpired(): String =
            Gson().toJson(GenericResponseBody(
                    subCode = TOKEN_EXPIRED_CODE,
                    message = TOKEN_EXPIRED_CAUSE
            ))

    fun noPermission(): String =
            Gson().toJson(GenericResponseBody(
                    subCode = NO_PERMISSION_CODE,
                    message = NO_PERMISSION_CAUSE
            ))
}