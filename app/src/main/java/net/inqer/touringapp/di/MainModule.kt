package net.inqer.touringapp.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import net.inqer.touringapp.R
import net.inqer.touringapp.data.local.AppDatabase
import net.inqer.touringapp.data.local.dao.TourRouteDao
import net.inqer.touringapp.data.repository.main.DefaultMainRepository
import net.inqer.touringapp.data.repository.main.MainRepository
import net.inqer.touringapp.util.DispatcherProvider
import javax.inject.Named

@Module
@InstallIn(ViewModelComponent::class)
object MainModule {

    @ViewModelScoped
    @Provides
    fun provideMainRepository(repository: DefaultMainRepository): MainRepository = repository


    @ViewModelScoped
    @Provides
    fun provideDispatchers(): DispatcherProvider = object : DispatcherProvider {
        override val main: CoroutineDispatcher
            get() = Dispatchers.Main
        override val io: CoroutineDispatcher
            get() = Dispatchers.IO
        override val default: CoroutineDispatcher
            get() = Dispatchers.Main
        override val unconfined: CoroutineDispatcher
            get() = Dispatchers.Unconfined

    }

    @ViewModelScoped
    @Provides
    fun provideTourRouteDao(database: AppDatabase): TourRouteDao = database.tourRouteDao()
}