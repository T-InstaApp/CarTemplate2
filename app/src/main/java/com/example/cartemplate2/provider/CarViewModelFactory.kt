package com.example.cartemplate2.provider

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.cartemplate2.model.CarViewModel
import com.example.cartemplate2.repositories.CarRepository

class CarViewModelFactory(private val carRepository: CarRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return CarViewModel(carRepository) as T
    }
}