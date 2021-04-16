package com.hongwei.model.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface AlbumRepository : JpaRepository<Album, Long> {
    @Query("from Album g where g.path=:path")
    fun findAlbumByPath(@Param("path") path: String): Album?

    @Query("from Album g where g.name=:name")
    fun findAlbumByName(@Param("name") name: String): Album?
}