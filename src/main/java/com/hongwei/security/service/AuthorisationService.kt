package com.hongwei.security.service

import com.hongwei.constants.*
import com.hongwei.security.model.AuthorisationRequest
import com.hongwei.security.model.AuthorisationResponse
import com.hongwei.security.model.AuthoriseObject
import org.apache.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import javax.servlet.http.HttpServletRequest

@Service
class AuthorisationService {
    private val logger = LogManager.getLogger(AuthorisationService::class.java)

    @Autowired
    private lateinit var securityConfigurations: SecurityConfigurations

    @Autowired
    private lateinit var cache: AuthorisationCache

    @Value("\${spring.jmx.default-domain}")
    private lateinit var applicationDomain: String

    @Throws(HttpClientErrorException::class, InternalServerError::class)
    fun authorise(jwt: String, method: String, requestUri: String, request: HttpServletRequest): Boolean {
        val authoriseObject: AuthoriseObject? = cache.getFromCache(jwt) ?: try {
            val uri = "${securityConfigurations.authorizationDomain}${securityConfigurations.authorizationEndpoint}"
            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_JSON;
            val response: ResponseEntity<AuthorisationResponse> =
                    RestTemplate().postForEntity(uri, HttpEntity(
                            AuthorisationRequest(jwt)
                    ), AuthorisationResponse::class.java)

            if (response.statusCode.is2xxSuccessful && response.body?.validated == true) {
                cache.update(jwt, response.body!!)
            } else null
        } catch (e: Unauthorized) {
            throw e
        } catch (e: HttpClientErrorException) {
            if (e.statusCode.value() == HttpStatus.UNAUTHORIZED.value()) {
                throw Unauthorized
            }
            throw e
        } catch (e: Exception) {
            if (e.message?.contains("JWT expired") == true) {
                throw AppTokenExpiredException
            }
            e.printStackTrace()
            throw InternalServerError
        }

        return authoriseObject?.run {
            if (validated) {
                checkPermission(userName, method, requestUri, request)
            } else false
        } ?: false
    }

    private fun AuthoriseObject.checkPermission(userName: String, method: String, uri: String,
                                                request: HttpServletRequest): Boolean {
        val normalisedUri = uri.replace("/$applicationDomain", "")
        if (uploadExercise && normalisedUri.startsWith("/upload.do", true)) {
            return when (method) {
                HttpMethod.POST.name -> true
                else -> false
            }
        }

        if ((photoEntry) && normalisedUri.startsWith("/photo/", true)) {
            request.setAttribute(Constants.Security.REQUEST_ATTRIBUTE_PHOTO_BROWSE_ALL, photoBrowseAll)
            request.setAttribute(Constants.Security.REQUEST_ATTRIBUTE_PHOTO_ALBUMS, photoBrowseByAlbum)
            return true
        }

        return false
    }
}