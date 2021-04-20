package net.inqer.touringapp.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider

@Module
@InstallIn(ViewModelComponent::class)
object MapsModule {
    @Provides
    fun provideGpsMyLocationProvider(@ApplicationContext context: Context): GpsMyLocationProvider =
            GpsMyLocationProvider(context)
}