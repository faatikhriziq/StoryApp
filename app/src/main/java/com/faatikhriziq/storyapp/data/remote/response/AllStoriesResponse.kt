package com.faatikhriziq.storyapp.data.remote.response

import com.faatikhriziq.storyapp.data.source.local.entity.StoryEntity
import com.google.gson.annotations.SerializedName

data class AllStoriesResponse(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("listStory")
    val listStory: List<StoryEntity>
)