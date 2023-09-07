package com.example.cartemplate2.provider

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.cartemplate2.model.AuthViewModel
import com.example.cartemplate2.repositories.AuthRepository

class AuthViewModelFactory(private val authRepository: AuthRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return AuthViewModel(authRepository) as T
    }
}