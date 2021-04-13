package com.hongwei.model

data class PhotoResponse(
        val album: String,
        val res: String,
        val images: List<String>,
        val thumbnails: List<String>
)