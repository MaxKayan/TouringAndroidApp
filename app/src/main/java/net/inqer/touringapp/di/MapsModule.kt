package net.inqer.touringapp.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object MapsModule {
//    @Provides
//    fun provideGpsMyLocationProvider(@ApplicationContext context: Context): GpsMyLocationProvider =
//            GpsMyLocationProvider(context)
}
