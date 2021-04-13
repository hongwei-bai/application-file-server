package com.hongwei.service

import com.hongwei.constants.AppDataConfigurations
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
import com.hongwei.model.AlbumResponse
import com.hongwei.model.PhotoResolution
import com.hongwei.model.PhotoResponse
import com.hongwei.model.privilege.PhotoPrivilege
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*

@Service
class PhotoService {
    private val logger: Logger = LogManager.getLogger(PhotoService::class.java)

    @Autowired
    private lateinit var appDataConfigurations: AppDataConfigurations

    fun getAlbumList(photoPrivilege: PhotoPrivilege): AlbumResponse {
        if (photoPrivilege.all != true && photoPrivilege.byAlbum.isNullOrEmpty()) {
            throw Unauthorized
        }

        val albums = mutableListOf<String>()
        val thumbnails = mutableListOf<String>()
        val root = File(appDataConfigurations.imagesRoot)
        if (root.exists() && root.isDirectory) {
            root.listFiles().filter {
                it.exists() && it.isDirectory
            }.forEach {
                if (photoPrivilege.all == true || photoPrivilege.byAlbum?.contains(it.name) == true) {
                    albums.add(it.name)
                }
            }
        }
        return AlbumResponse(albums, thumbnails)
    }

    fun getPhotoListByFolder(folderName: String, resolution: String, photoPrivilege: PhotoPrivilege): PhotoResponse {
        if (photoPrivilege.all != true && photoPrivilege.byAlbum?.contains(folderName) != true) {
            throw Unauthorized
        }

        val res = PhotoResolution.parseFromString(resolution)
        val root = File(appDataConfigurations.imagesRoot + folderName)
        val images = mutableListOf<String>()
        val thumbnails = mutableListOf<String>()
        if (root.exists() && root.isDirectory) {
            val files = root.listFiles()
            files.forEach {
                if (it.isFile && isSupportImageFormat(it.name)) {
                    images.add(getEncodedFullUrl(folderName, it.name, res))
                    thumbnails.add(getEncodedFullUrl(folderName, it.name, PhotoResolution.Thumbnail))
                }
            }
        }
        return PhotoResponse(folderName, res.name, images, thumbnails)
    }

    private fun getEncodedFullUrl(folderName: String, fileName: String, res: PhotoResolution): String {
        val url = IMAGE_URL.replace(PLACEHOLDER_LOCATION, appDataConfigurations.imageLocation)
                .replace(PLACEHOLDER_WIDTH, res.width.toString())
                .replace(PLACEHOLDER_ALBUM, folderName)
                .replace(PLACEHOLDER_FILENAME, fileName)
        val expires = System.currentTimeMillis() + IMAGE_EXPIRES_IN_HOURS * MILLIS_PER_HOUR
        val hash = generateSecurePathHash(expires, url, appDataConfigurations.imageSecret)
        return IMAGE_FULL_URL.replace(PLACEHOLDER_DOMAIN, appDataConfigurations.imagesDomain)
                .replace(PLACEHOLDER_URL, url)
                .replace(PLACEHOLDER_EXPIRES, expires.toString())
                .replace(PLACEHOLDER_HASH, hash)
    }

    private fun generateSecurePathHash(expires: Long, url: String, secret: String): String {
        val input = "$expires$url $secret"
        val hash = md5(input)
        val base64String = Base64.getEncoder().encodeToString(hash.toByteArray())
        return base64String.replace("/=/g", "")
                .replace("/+/g", "-")
                .replace("///g", "_")
    }

    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

    private fun isSupportImageFormat(fileName: String): Boolean =
            SUPPORT_IMAGE_FORMATS.contains(File(fileName).extension.toLowerCase())
}