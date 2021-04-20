package com.hongwei.util

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object UrlEncoderUtil {
    fun encodeUrl(string: String): String =
            URLEncoder.encode(string, StandardCharsets.UTF_8.toString()).replace("+", "%20")
}