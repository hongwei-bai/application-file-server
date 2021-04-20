package com.hongwei.model.privilege

data class PhotoPrivilege(
        val accountExpires: Long = -1L,
        val all: Boolean? = false,
        val byAlbum: List<String>? = emptyList()
)