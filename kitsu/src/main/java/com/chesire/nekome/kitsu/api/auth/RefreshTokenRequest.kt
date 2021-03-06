package com.chesire.nekome.kitsu.api.auth

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Request object for requesting an updated access token.
 */
@JsonClass(generateAdapter = true)
data class RefreshTokenRequest(
    @Json(name = "refresh_token")
    val refreshToken: String,
    @Json(name = "grant_type")
    val grantType: String = "refresh_token"
)
