package kpk.dev.model.di

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class ContentModule {

    @Provides
    @Singleton
    internal fun provideContentResolver(@Named("app_context") applicationContext: Context) = applicationContext.contentResolver
}