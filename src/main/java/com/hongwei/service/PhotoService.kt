package com.hongwei.service

import com.hongwei.constants.AppDataConfigurations
import com.hongwei.constants.Constants.Photo.APP_IMAGE_FULL_URL
import com.hongwei.constants.Constants.Photo.COVER_FILENAME
import com.hongwei.constants.Constants.Photo.IMAGE_EXPIRES_IN_HOURS
import com.hongwei.constants.Constants.Photo.IMAGE_FULL_URL
import com.hongwei.constants.Constants.Photo.IMAGE_URL
import com.hongwei.constants.Constants.Photo.PLACEHOLDER_ALBUM
import com.hongwei.constants.Constants.Photo.PLACEHOLDER_DOMAIN
import com.hongwei.constants.Constants.Photo.PLACEHOLDER_EXPIRES
import com.hongwei.constants.Constants.Photo.PLACEHOLDER_FILENAME
import com.hongwei.constants.Constants.Photo.PLACEHOLDER_HASH
import com.hongwei.constants.Constants.Photo.PLACEHOLDER_LOCATION
import com.hongwei.constants.Constants.Photo.PLACEHOLDER_URL
import com.hongwei.constants.Constants.Photo.PLACEHOLDER_WIDTH
import com.hongwei.constants.Constants.Photo.SUPPORT_IMAGE_FORMATS
import com.hongwei.constants.Constants.TimeUnit.MILLIS_PER_HOUR
import com.hongwei.constants.Unauthorized
import com.hongwei.model.*
import com.hongwei.model.jpa.AlbumRepository
import com.hongwei.model.privilege.PhotoPrivilege
import com.hongwei.util.DateUtil
import com.hongwei.util.UrlEncoderUtil.encodeUrl
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Service
class PhotoService {
    private val logger: Logger = LogManager.getLogger(PhotoService::class.java)

    @Autowired
    private lateinit var appDataConfigurations: AppDataConfigurations

    @Autowired
    private lateinit var photoImageHashing: PhotoImageHashing

    @Autowired
    private lateinit var albumRepository: AlbumRepository

    fun getAlbumList(photoPrivilege: PhotoPrivilege): AlbumResponse {
        if (photoPrivilege.all != true && photoPrivilege.byAlbum.isNullOrEmpty()) {
            throw Unauthorized
        }

        val albums = mutableListOf<AlbumForApi>()
        val root = File(appDataConfigurations.imagesRoot)
        val coverRoot = File(appDataConfigurations.appImagesRoot)
        if (root.exists() && root.isDirectory) {
            root.listFiles().filter {
                it.exists() && it.isDirectory
            }.forEach {
                if (photoPrivilege.all == true || photoPrivilege.byAlbum?.contains(it.name) == true) {
                    val albumDb = albumRepository.findAlbumByPath(it.name)
                    val coverFileName = COVER_FILENAME.replace(PLACEHOLDER_ALBUM, it.name)
                    val cover = File(coverRoot, coverFileName)
                    var thumbnailFullUrl = ""
                    if (cover.exists() && cover.isFile) {
                        thumbnailFullUrl = APP_IMAGE_FULL_URL
                                .replace(PLACEHOLDER_DOMAIN, appDataConfigurations.imagesDomain)
                                .replace(PLACEHOLDER_LOCATION, appDataConfigurations.appImageLocation)
                                .replace(PLACEHOLDER_WIDTH, PhotoResolution.Thumbnail.width.toString())
                                .replace(PLACEHOLDER_FILENAME, encodeUrl(coverFileName))
                    }
                    albums.add(AlbumForApi(
                            name = albumDb?.name ?: it.name,
                            nameByPath = it.name,
                            description = albumDb?.description ?: "",
                            owner = albumDb?.owner ?: "",
                            thumbnail = thumbnailFullUrl
                    ))
                }
            }
        }
        return AlbumResponse(albums)
    }

    fun getPhotoListByFolder(folderName: String, resolution: String, photoPrivilege: PhotoPrivilege): PhotoResponse {
        if (photoPrivilege.all != true && photoPrivilege.byAlbum?.contains(folderName) != true) {
            throw Unauthorized
        }

        val res = PhotoResolution.parseFromString(resolution)
        val root = File(appDataConfigurations.imagesRoot + folderName)
        val images = mutableListOf<Photo>()
        if (root.exists() && root.isDirectory) {
            val files = root.listFiles()
            files.forEach {
                if (it.isFile && isSupportImageFormat(it.name)) {
                    images.add(Photo(
                            original = getEncodedFullUrl(folderName, it.name, res, photoPrivilege.accountExpires),
                            thumbnail = getEncodedFullUrl(folderName, it.name, PhotoResolution.Thumbnail, photoPrivilege.accountExpires)
                    ))
                }
            }
        }
        return PhotoResponse(folderName, res.name, images)
    }

    private fun getEncodedFullUrl(folderName: String, fileName: String, res: PhotoResolution = PhotoResolution.Thumbnail,
                                  accountExpires: Long): String {
        val url = IMAGE_URL.replace(PLACEHOLDER_LOCATION, appDataConfigurations.imageLocation)
                .replace(PLACEHOLDER_WIDTH, res.width.toString())
                .replace(PLACEHOLDER_ALBUM, folderName)
                .replace(PLACEHOLDER_FILENAME, fileName)
                .replace("//", "/")
        val encodedUrl = IMAGE_URL.replace(PLACEHOLDER_LOCATION, appDataConfigurations.imageLocation)
                .replace(PLACEHOLDER_WIDTH, res.width.toString())
                .replace(PLACEHOLDER_ALBUM, encodeUrl(folderName))
                .replace(PLACEHOLDER_FILENAME, encodeUrl(fileName))
                .replace("//", "/")
        val expires = getLatestTimeStamp(accountExpires, DateUtil.getFirstDayOfNextMonth())
        val hash = photoImageHashing.generateSecurePathHash(expires, url, appDataConfigurations.imageSecret)
        return IMAGE_FULL_URL.replace(PLACEHOLDER_DOMAIN, appDataConfigurations.imagesDomain)
                .replace(PLACEHOLDER_URL, encodedUrl)
                .replace(PLACEHOLDER_EXPIRES, expires.toString())
                .replace(PLACEHOLDER_HASH, hash)
    }

    private fun getLatestTimeStamp(tsCouldBeInvalid: Long, ts: Long): Long {
        if (tsCouldBeInvalid > 0) {
            return tsCouldBeInvalid.coerceAtMost(ts)
        }
        return ts
    }

    private fun isSupportImageFormat(fileName: String): Boolean =
            SUPPORT_IMAGE_FORMATS.contains(File(fileName).extension.toLowerCase())
}