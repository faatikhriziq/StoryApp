package com.faatikhriziq.storyapp.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.faatikhriziq.storyapp.data.repository.StoryRepository
import com.faatikhriziq.storyapp.data.source.local.datastore.UserPreferences
import com.faatikhriziq.storyapp.data.source.local.entity.UserEntity
import kotlinx.coroutines.launch

class MapViewModel(
    private val userPreferences: UserPreferences,
    private val storyRepository: StoryRepository
): ViewModel() {

    fun getLogin(): LiveData<UserEntity> = userPreferences.getLogin().asLiveData()


    fun getAllStoriesWithLocation(token: String) =
        storyRepository.getAllStoriesWithLocation(token)
}