package kpk.dev.presentation.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import kpk.dev.presentation.view.main.MainActivity

@Module
abstract class ActivityBuilder {
    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity
}