package com.hongwei.controller

import com.hongwei.model.AlbumResponse
import com.hongwei.model.PhotoPrivilegeMapper.map
import com.hongwei.model.PhotoResponse
import com.hongwei.service.PhotoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/photo")
class PhotoController {
    @Autowired
    private lateinit var photoService: PhotoService

    @GetMapping("/albums.do")
    fun getAlbumList(request: HttpServletRequest): ResponseEntity<AlbumResponse> {
        return ResponseEntity.ok(photoService.getAlbumList(map(request)))
    }

    @GetMapping("/photos.do")
    fun getPhotoList(album: String, resolution: String, request: HttpServletRequest): ResponseEntity<PhotoResponse> {
        return ResponseEntity.ok(photoService.getPhotoListByFolder(album, resolution, map(request)))
    }
}