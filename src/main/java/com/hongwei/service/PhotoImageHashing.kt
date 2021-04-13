package com.hongwei.service

import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.util.*

@Service
class PhotoImageHashing {
    fun generateSecurePathHash(expires: Long, url: String, secret: String): String {
        val input = "$expires$url $secret"
        val md5Binary = md5Binary(input)
        var base64String = Base64.getEncoder().encodeToString(md5Binary)
        while (base64String.contains("=")) {
            base64String = base64String.replace("=", "")
        }
        while (base64String.contains("+")) {
            base64String = base64String.replace("+", "-")
        }
        while (base64String.contains("/")) {
            base64String = base64String.replace("/", "_")
        }
        return base64String
    }

    private fun md5Binary(input: String): ByteArray {
        val md = MessageDigest.getInstance("MD5")
        return md.digest(input.toByteArray())
    }
}