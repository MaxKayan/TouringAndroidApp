package net.inqer.touringapp.hilt

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import net.inqer.touringapp.R
import javax.inject.Named

@Module
@InstallIn(ViewModelComponent::class)
object MainModule {

    @ViewModelScoped
    @Provides
    @Named("String2")
    fun provideTestString2(
            @ApplicationContext context: Context,
            @Named("String1") testString1: String
    ) = "${context.getString(R.string.string_to_inject)} - $testString1"

}