package com.rivera.votainformado.data.model.auth

import com.google.gson.annotations.SerializedName

data class RegisterRequest (
    val dni: String,
    @SerializedName("region_id")
    val regionId: String,
    val password: String
)