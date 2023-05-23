package com.faatikhriziq.storyapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class LoginResultResponse(
    @field:SerializedName("userId")
    val userId: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("token")
    val token: String
)