package com.hongwei.model

import com.hongwei.constants.Constants
import com.hongwei.model.privilege.PhotoPrivilege
import javax.servlet.http.HttpServletRequest

object PhotoPrivilegeMapper {
    fun map(request: HttpServletRequest): PhotoPrivilege = PhotoPrivilege(
            accountExpires = request.getAttribute(Constants.Security.REQUEST_ATTRIBUTE_ACCOUNT_EXPIRES) as Long,
            all = request.getAttribute(Constants.Security.REQUEST_ATTRIBUTE_PHOTO_BROWSE_ALL) as Boolean,
            byAlbum = request.getAttribute(Constants.Security.REQUEST_ATTRIBUTE_PHOTO_ALBUMS) as List<String>
    )
}