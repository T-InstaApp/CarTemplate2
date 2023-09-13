package com.example.cartemplate2

import android.app.Application
import com.example.cartemplate2.network.MyApi
import com.example.cartemplate2.network.NetworkConnectionInterceptor
import com.example.cartemplate2.provider.AuthViewModelFactory
import com.example.cartemplate2.provider.CarViewModelFactory
import com.example.cartemplate2.provider.HomeViewModelFactory
import com.example.cartemplate2.repositories.AuthRepository
import com.example.cartemplate2.repositories.CarRepository
import com.example.cartemplate2.repositories.HomeRepository
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class Application : Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        import(androidXModule(this@Application))

        bind() from singleton { NetworkConnectionInterceptor(instance()) }
        bind() from singleton { MyApi(instance(), applicationContext) }
        bind() from singleton { HomeRepository(instance()) }
        bind() from singleton { CarRepository(instance()) }
        bind() from singleton { AuthRepository(instance()) }
        //Providers
        bind() from provider { HomeViewModelFactory(instance()) }
        bind() from provider { CarViewModelFactory(instance()) }
        bind() from provider { AuthViewModelFactory(instance()) }
    }
}