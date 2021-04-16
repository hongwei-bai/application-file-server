package com.hongwei.model

data class PhotoResponse(
        val album: String,
        val res: String,
        val images: List<Photo>
)

data class Photo(
        val original: String,
        val thumbnail: String
)