package com.hongwei.security.service

import com.google.gson.Gson
import com.hongwei.model.privilege.Privilege
import com.hongwei.security.model.AuthorisationResponse
import com.hongwei.security.model.AuthoriseObject
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthorisationCache {
    private val jwtToObjWeakHashMap = WeakHashMap<String, AuthoriseObject>()
    private val jwtToUserNameWeakHashMap = WeakHashMap<String, String>()

    fun bypassCache(jwt: String, response: AuthorisationResponse): AuthoriseObject? {
        return response.mapTo()
    }

    fun update(jwt: String, response: AuthorisationResponse): AuthoriseObject? {
        if (jwtToObjWeakHashMap.containsKey(jwt)) {
            return jwtToObjWeakHashMap[jwt]
        }

        val userName = response.userName
        if (jwtToUserNameWeakHashMap.containsKey(userName)) {
            val expiredJwt = jwtToUserNameWeakHashMap[userName]
            jwtToObjWeakHashMap.remove(expiredJwt)
        }

        val authoriseObject = response.mapTo()
        authoriseObject?.let {
            jwtToObjWeakHashMap[jwt] = it
            jwtToUserNameWeakHashMap[userName] = jwt
        }
        return authoriseObject
    }

    fun getFromCache(jwt: String): AuthoriseObject? = jwtToObjWeakHashMap[jwt]

    private fun AuthorisationResponse.mapTo() = try {
        val privilege = Gson().fromJson<Privilege>(this.privilegeJson, Privilege::class.java)
        AuthoriseObject(
                validated = this.validated == true,
                validatedUntil = this.validatedUntil,
                userName = this.userName!!,
                role = this.role!!,
                preferenceJson = this.preferenceJson ?: "",
                privilegeJson = this.privilegeJson ?: "",
                uploadExercise = privilege.entries.contains("uploadExercise"),
                photoEntry = privilege.entries.contains("photo"),
                photoBrowseAll = privilege.photo?.all ?: false,
                photoBrowseByAlbum = privilege.photo?.byAlbum ?: emptyList()
        )
    } catch (e: Exception) {
        null
    }
}