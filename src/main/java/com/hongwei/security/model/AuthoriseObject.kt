package com.hongwei.security.model

data class AuthoriseObject(
        val validated: Boolean = false,
        val validatedUntil: Long,
        val userName: String,
        val role: String,
        val preferenceJson: String,
        val privilegeJson: String,
        val uploadExercise: Boolean = false,
        val photoEntry: Boolean = false,
        val photoBrowseAll: Boolean = false,
        val photoBrowseByAlbum: List<String> = emptyList()
)

const val LONG_TERM = -1L