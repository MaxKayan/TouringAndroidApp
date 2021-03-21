package net.inqer.touringapp.hilt

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.inqer.touringapp.AppConfig
import net.inqer.touringapp.R
import net.inqer.touringapp.data.remote.RoutesApi
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Named("String1")
    @Provides
    fun provideTestString() = "This is an injected string - Max"

    @Singleton
    @Provides
    fun provideAppConfig(): AppConfig = AppConfig

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
}
