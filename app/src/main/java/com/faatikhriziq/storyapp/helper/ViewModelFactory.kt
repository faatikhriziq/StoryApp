package com.faatikhriziq.storyapp.helper

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import com.faatikhriziq.storyapp.data.repository.StoryRepository
import com.faatikhriziq.storyapp.data.repository.UserRepository
import com.faatikhriziq.storyapp.data.source.local.datastore.SettingPreferences
import com.faatikhriziq.storyapp.data.source.local.datastore.UserPreferences
import com.faatikhriziq.storyapp.di.Injection
import com.faatikhriziq.storyapp.ui.auth.LoginViewModel
import com.faatikhriziq.storyapp.ui.create.CreateStoryViewModel
import com.faatikhriziq.storyapp.ui.detail.DetailViewModel
import com.faatikhriziq.storyapp.ui.home.HomeViewModel
import com.faatikhriziq.storyapp.ui.map.MapViewModel

class ViewModelFactory(
    private val userPreferences: UserPreferences,
    private val settingPreferences: SettingPreferences,
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
    ) : NewInstanceFactory() {

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context) : ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    Injection.provideUserPreferences(context),
                    Injection.provideSettingPreferences(context),
                    Injection.provideUserRepository(),
                    Injection.provideStoryRepository(context)
                )
            }.also { instance = it }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userPreferences, settingPreferences, userRepository) as T
            }
            modelClass.isAssignableFrom(CreateStoryViewModel::class.java) -> {
                CreateStoryViewModel(userPreferences, storyRepository) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(userPreferences, storyRepository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(userPreferences, storyRepository) as T
            }
            modelClass.isAssignableFrom(MapViewModel::class.java) -> {
                MapViewModel(userPreferences, storyRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}