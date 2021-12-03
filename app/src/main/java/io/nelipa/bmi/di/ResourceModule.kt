package io.nelipa.bmi.di

import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
class ResourcesModule {

    @Provides
    fun provideResources(@ApplicationContext context: Context): Resources {
        return context.resources
    }
}