package com.rivera.votainformado.data.model.auth

import com.google.gson.annotations.SerializedName

data class Region(
    val id: Int,
    @SerializedName("nombre_region")
    val nombreRegion: String
)