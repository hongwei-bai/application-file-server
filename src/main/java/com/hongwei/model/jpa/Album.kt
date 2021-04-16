package com.hongwei.model.jpa

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Album(
        @Id
        @GeneratedValue
        var id: Long? = null,

        @Column(nullable = false)
        val path: String? = null,

        @Column(nullable = true)
        val owner: String? = null,

        @Column(nullable = true)
        val name: String? = null,

        @Column(nullable = true)
        val description: String? = null
)
