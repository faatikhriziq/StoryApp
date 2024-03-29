package com.faatikhriziq.storyapp.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.faatikhriziq.storyapp.data.repository.StoryRepository
import com.faatikhriziq.storyapp.data.source.local.datastore.UserPreferences
import com.faatikhriziq.storyapp.data.source.local.entity.StoryEntity
import com.faatikhriziq.storyapp.data.source.local.entity.UserEntity
import kotlinx.coroutines.launch

class DetailViewModel(
    private val userPreferences: UserPreferences,
    private val storyRepository: StoryRepository
    ) : ViewModel() {

    fun getLogin() : LiveData<UserEntity> = userPreferences.getLogin().asLiveData()


    fun getDetailStory(token: String, id: String) = storyRepository.getDetailStory(token, id)

    fun saveStory(storyEntity: StoryEntity) {
        viewModelScope.launch {
            storyRepository.setStoryBookmark(storyEntity, true)
        }
    }

    fun deleteStory(story: StoryEntity) {
        viewModelScope.launch {
            storyRepository.setStoryBookmark(story, false)
        }
    }
}