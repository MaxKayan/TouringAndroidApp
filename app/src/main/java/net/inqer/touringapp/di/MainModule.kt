package net.inqer.touringapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.coroutines.flow.Flow
import net.inqer.touringapp.data.local.AppDatabase
import net.inqer.touringapp.data.local.dao.TourRouteDao
import net.inqer.touringapp.data.models.TourRoute
import net.inqer.touringapp.data.repository.main.DefaultMainRepository
import net.inqer.touringapp.data.repository.main.MainRepository
import net.inqer.touringapp.di.qualifiers.ActiveTourRouteFlow

@Module
@InstallIn(ViewModelComponent::class, ServiceComponent::class)
object MainModule {

    @Provides
    fun provideMainRepository(repository: DefaultMainRepository): MainRepository = repository

    @Provides
    fun provideTourRouteDao(database: AppDatabase): TourRouteDao = database.tourRouteDao()

    @Provides
    @ActiveTourRouteFlow
    fun provideActiveRouteFlow(repository: MainRepository): Flow<TourRoute?> =
        repository.observeActiveRoute()
}