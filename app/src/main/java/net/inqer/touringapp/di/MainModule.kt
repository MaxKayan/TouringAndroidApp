package net.inqer.touringapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import net.inqer.touringapp.data.local.AppDatabase
import net.inqer.touringapp.data.local.dao.TourRouteDao
import net.inqer.touringapp.data.models.TourRoute
import net.inqer.touringapp.data.repository.main.DefaultMainRepository
import net.inqer.touringapp.data.repository.main.MainRepository
import net.inqer.touringapp.di.qualifiers.ActiveTourRouteFlow
import net.inqer.touringapp.util.DispatcherProvider

@Module
@InstallIn(ViewModelComponent::class)
object MainModule {

    @ViewModelScoped
    @Provides
    fun provideMainRepository(repository: DefaultMainRepository): MainRepository = repository

    @ViewModelScoped
    @Provides
    fun provideTourRouteDao(database: AppDatabase): TourRouteDao = database.tourRouteDao()

    @ViewModelScoped
    @Provides
    @ActiveTourRouteFlow
    fun provideActiveRouteFlow(repository: MainRepository): Flow<TourRoute?> = repository.observeActiveRoute()
}