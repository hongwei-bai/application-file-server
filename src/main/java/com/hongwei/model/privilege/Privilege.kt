package com.hongwei.model.privilege

data class Privilege(
        val entries: List<String>,
        val photo: PhotoPrivilege? = PhotoPrivilege(),
        val uploadExercise: Boolean? = false
)