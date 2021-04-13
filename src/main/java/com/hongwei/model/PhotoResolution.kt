package com.hongwei.model

enum class PhotoResolution(val width: Int) {
    Raw(-1), High(1080), Medium(720), Low(480), Thumbnail(320);

    companion object {
        fun parseFromString(string: String): PhotoResolution = when (string.toLowerCase()) {
            Thumbnail.name.toLowerCase() -> Thumbnail
            High.name.toLowerCase() -> High
            Medium.name.toLowerCase() -> Medium
            Low.name.toLowerCase() -> Low
            Raw.name.toLowerCase() -> Raw
            else -> Thumbnail
        }
    }
}