package com.faatikhriziq.storyapp.data.remote.retrofit

import com.faatikhriziq.storyapp.data.remote.request.LoginRequest
import com.faatikhriziq.storyapp.data.remote.request.RegisterRequest
import com.faatikhriziq.storyapp.data.remote.response.AllStoriesResponse
import com.faatikhriziq.storyapp.data.remote.response.DetailStoryResponse
import com.faatikhriziq.storyapp.data.remote.response.LoginResponse
import com.faatikhriziq.storyapp.data.remote.response.MessageResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): MessageResponse

    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @Multipart
    @POST("stories/guest")
    suspend fun addNewStory(
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part
    ): MessageResponse

    @Multipart
    @POST("stories")
    suspend fun addNewStory(
        @Header("Authorization") token: String,
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part
    ): MessageResponse

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): AllStoriesResponse

    @GET("stories?location=1")
    suspend fun getAllStoriesWithLocation(
        @Header("Authorization") token: String
    ): AllStoriesResponse

    @GET("stories/{id}")
    suspend fun getDetailStory(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): DetailStoryResponse
}