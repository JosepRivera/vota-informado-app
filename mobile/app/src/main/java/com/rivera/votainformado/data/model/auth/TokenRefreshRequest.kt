package com.rivera.votainformado.data.model.auth

import com.google.gson.annotations.SerializedName

data class TokenRefreshRequest(
    @SerializedName("refresh")
    val refresh: String
)

