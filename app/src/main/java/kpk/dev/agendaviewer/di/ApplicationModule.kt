package kpk.dev.agendaviewer.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class ApplicationModule {

    @Provides
    @Named("app_context")
    fun provideApplicationContext(application: Application): Context = application.applicationContext

}