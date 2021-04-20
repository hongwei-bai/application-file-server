package com.hongwei.constants

object Constants {
    object Security {
        const val REQUEST_ATTRIBUTE_ACCOUNT_EXPIRES = "account_expires"
        const val REQUEST_ATTRIBUTE_PHOTO_BROWSE_ALL = "photo_browse_all"
        const val REQUEST_ATTRIBUTE_PHOTO_ALBUMS = "photo_browse_by_album"
    }

    object TimeUnit {
        const val MILLIS_PER_HOUR = 3600L * 1000
        const val MILLIS_PER_WEEK = MILLIS_PER_HOUR * 24 * 7
        const val MILLIS_PER_MONTH = MILLIS_PER_HOUR * 24 * 30
    }

    object Photo {
        const val PLACEHOLDER_DOMAIN = "{domain}"
        const val PLACEHOLDER_LOCATION = "{location}"
        const val PLACEHOLDER_WIDTH = "{width}"
        const val PLACEHOLDER_ALBUM = "{album}"
        const val PLACEHOLDER_FILENAME = "{fileName}"
        const val PLACEHOLDER_EXPIRES = "{expires}"
        const val PLACEHOLDER_HASH = "{hash}"
        const val PLACEHOLDER_URL = "{url}"

        val IMAGE_URL = "/$PLACEHOLDER_LOCATION$PLACEHOLDER_WIDTH/$PLACEHOLDER_ALBUM/$PLACEHOLDER_FILENAME"
        val IMAGE_FULL_URL = "$PLACEHOLDER_DOMAIN$PLACEHOLDER_URL" +
                "?expires=$PLACEHOLDER_EXPIRES&hash=$PLACEHOLDER_HASH"
        const val IMAGE_EXPIRES_IN_HOURS = 24
        val SUPPORT_IMAGE_FORMATS = listOf("jpg", "jpeg", "png", "webp", "gif")

        const val COVER_FILENAME = "$PLACEHOLDER_ALBUM.jpg"
        val APP_IMAGE_FULL_URL = "$PLACEHOLDER_DOMAIN/$PLACEHOLDER_LOCATION$PLACEHOLDER_WIDTH/photo/covers/$PLACEHOLDER_FILENAME"
    }
}