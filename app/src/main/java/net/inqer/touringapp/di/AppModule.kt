package net.inqer.touringapp.di

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.inqer.touringapp.AppConfig
import net.inqer.touringapp.R
import net.inqer.touringapp.SettingsConstants.DEFAULT_URL
import net.inqer.touringapp.data.local.AppDatabase
import net.inqer.touringapp.data.models.TourRoute
import net.inqer.touringapp.data.remote.RoutesApi
import net.inqer.touringapp.di.qualifiers.ActiveTourRouteLiveData
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DateFormat
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val TAG = "AppModule"

    @Singleton
    @Provides
    fun provideAppConfig(
            @ApplicationContext context: Context,
            preferences: SharedPreferences
    ): AppConfig {
        val key = context.getString(R.string.main_server_address)
        val url = preferences.getString(key, DEFAULT_URL)

        return object : AppConfig {
            override val BASE_URL: String
                get() = url
                        ?: DEFAULT_URL
        }
    }

    @Singleton
    @Provides
    fun provideConverterFactory(): Converter.Factory = GsonConverterFactory.create()

    @Singleton
    @Provides
    fun provideRetrofitInstance(config: AppConfig, factory: Converter.Factory): Retrofit = Retrofit.Builder()
            .baseUrl(config.BASE_URL)
            .addConverterFactory(factory)
            .build()

    @Singleton
    @Provides
    fun provideRoutesApi(retrofit: Retrofit): RoutesApi = retrofit.create(RoutesApi::class.java)

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
            context.getSharedPreferences(
                    context.getString(R.string.main_preference_file_key), Context.MODE_PRIVATE
            )

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "touring-app-database")
                    .fallbackToDestructiveMigration()
                    .build()

    @Singleton
    @Provides
    fun provideDateFormat(@ApplicationContext context: Context): DateFormat = android.text.format.DateFormat.getDateFormat(context)

    @Singleton
    @Provides
    @ActiveTourRouteLiveData
    fun provideActiveTourRouteLiveData(database: AppDatabase): LiveData<TourRoute?> = database.tourRouteDao().observeActiveRoute().asLiveData()
}
