package com.example.cartemplate2.provider

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.cartemplate2.repositories.HomeRepository
import com.example.cartemplate2.model.HomeViewModel
@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory(private val homeRepository: HomeRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return HomeViewModel(homeRepository) as T
    }
}