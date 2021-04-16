package com.hongwei.model

data class AlbumResponse(
        val albums: List<AlbumForApi>
)

data class AlbumForApi(
        val name: String = "",
        val nameByPath: String,
        val description: String = "",
        val owner: String = "",
        val thumbnail: String
)