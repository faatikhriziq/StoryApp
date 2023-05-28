package com.faatikhriziq.storyapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.faatikhriziq.storyapp.data.remote.request.NewStoryRequest
import com.faatikhriziq.storyapp.data.remote.response.MessageResponse
import com.faatikhriziq.storyapp.data.remote.retrofit.ApiService
import com.faatikhriziq.storyapp.data.source.local.entity.StoryEntity
import com.faatikhriziq.storyapp.data.source.local.paging.StoryRemoteMediator
import com.faatikhriziq.storyapp.data.source.local.room.StoryDao
import com.faatikhriziq.storyapp.data.source.local.room.StoryDatabase
import com.faatikhriziq.storyapp.helper.AppExecutors
import com.faatikhriziq.storyapp.data.repository.Result

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase,
    private val storyDao: StoryDao,
) {

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(
            apiService: ApiService,
            storyDatabase: StoryDatabase,
            storyDao: StoryDao,
            appExecutors: AppExecutors
        ) : StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, storyDatabase, storyDao)
            }.also { instance = it }
    }

    fun addNewStory(newStoryRequest: NewStoryRequest) : LiveData<Result<MessageResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                if (newStoryRequest.token.isNotBlank()) {
                    val responseBody = apiService.addNewStory(
                        "Bearer ${newStoryRequest.token}",
                        newStoryRequest.description,
                        newStoryRequest.photo
                    )
                    emit(Result.Success(responseBody))
                } else {
                    val responseBody = apiService.addNewStory(
                        newStoryRequest.description,
                        newStoryRequest.photo
                    )
                    emit(Result.Success(responseBody))
                }
            } catch (e: Exception) {
                Log.d("StoryRepository", "addNewStory: ${e.message.toString()}")
                emit(Result.Error(e.message.toString()))
            }
        }

    fun getAllStories(token: String) : LiveData<PagingData<StoryEntity>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStories()
            }
        ).liveData
    }

    fun getAllStoriesWithLocation(token: String): LiveData<Result<List<StoryEntity>>> =
        liveData {
            emit(Result.Loading)
            try {
                val responseBody = apiService.getAllStoriesWithLocation("Bearer $token")
                val stories = responseBody.listStory
                emit(Result.Success(stories))
            } catch (e: Exception) {
                Log.d(
                    "StoryRepository",
                    "getAllStoriesWithLocation: ${e.message.toString()}")
                emit(Result.Error(e.message.toString()))
            }
        }

    fun getDetailStory(token: String, id: String) : LiveData<Result<StoryEntity>> =
        liveData {
            var error = false
            emit(Result.Loading)
            try {
                val responseBody = apiService.getDetailStory("Bearer $token", id)
                emit(Result.Success(responseBody.story))
            } catch (e: Exception) {
                Log.d("StoryRepository", "getDetailStory: ${e.message.toString()}")
                emit(Result.Error(e.message.toString()))
                error = true
            } finally {
                if (error) {
                    val localData: LiveData<Result<StoryEntity>> = storyDao.getDetailStory(id).map {
                        Result.Success(it)
                    }
                    emitSource(localData)
                }
            }
        }

    fun setStoryBookmark(story: StoryEntity, bookmarkState: Boolean) {
        story.isBookmarked = bookmarkState
        storyDao.updateStory(story)
    }

    fun getBookmarkedStories() : LiveData<List<StoryEntity>> = storyDao.getBookmarkedStories()
}