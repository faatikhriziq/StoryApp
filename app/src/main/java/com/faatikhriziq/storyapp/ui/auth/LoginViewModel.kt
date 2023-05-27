package com.faatikhriziq.storyapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.faatikhriziq.storyapp.data.remote.request.LoginRequest
import com.faatikhriziq.storyapp.data.remote.request.RegisterRequest
import com.faatikhriziq.storyapp.data.repository.UserRepository
import com.faatikhriziq.storyapp.data.source.local.datastore.SettingPreferences
import com.faatikhriziq.storyapp.data.source.local.datastore.UserPreferences
import com.faatikhriziq.storyapp.data.source.local.entity.UserEntity
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userPreferences: UserPreferences,
    private val settingPreferences: SettingPreferences,
    private val userRepository: UserRepository
) : ViewModel() {

    fun register(registerRequest: RegisterRequest) = userRepository.register(registerRequest)

    fun login(loginRequest: LoginRequest) = userRepository.login(loginRequest)

    fun setLogin(user: UserEntity) { viewModelScope.launch { userPreferences.setLogin(user) } }

    fun getLogin() : LiveData<UserEntity> = userPreferences.getLogin().asLiveData()

    fun deleteLogin() { viewModelScope.launch { userPreferences.deleteLogin() } }

}