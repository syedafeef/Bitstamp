package com.afeef.cryptoapi

import android.app.Application
import com.afeef.cryptoapi.db.AppDatabase
import com.afeef.cryptoapi.network.BitStampApiService
import com.afeef.cryptoapi.network.interceptor.ConnectivityInterceptor
import com.afeef.cryptoapi.network.interceptor.ConnectivityInterceptorImpl
import com.afeef.cryptoapi.viewmodel.MainActivityViewModel
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import org.kodein.di.generic.provider

class CryptoApplicationClass :Application(), KodeinAware {

    override val kodein = Kodein.lazy {

        import(androidXModule(this@CryptoApplicationClass))

        bind<ConnectivityInterceptor>() with singleton { ConnectivityInterceptorImpl(instance()) }

        bind() from singleton { BitStampApiService(instance()) }

        bind() from provider { MainActivityViewModel(instance()) }

    }
}