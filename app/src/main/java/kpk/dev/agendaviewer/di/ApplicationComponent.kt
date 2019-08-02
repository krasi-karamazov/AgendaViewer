package kpk.dev.agendaviewer.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import kpk.dev.agendaviewer.AgendaViewerApplication
import kpk.dev.model.di.ContentModule
import kpk.dev.presentation.di.ActivityBuilder
import kpk.dev.presentation.di.DisposableModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class, ActivityBuilder::class, DisposableModule::class, ApplicationModule::class, ContentModule::class])
interface ApplicationComponent {
    @Component.Builder
    interface Builder{
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): ApplicationComponent
    }

    fun inject(application: AgendaViewerApplication)

}