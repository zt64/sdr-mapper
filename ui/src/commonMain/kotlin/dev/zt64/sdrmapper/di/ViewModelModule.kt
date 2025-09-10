package dev.zt64.sdrmapper.di

import dev.zt64.sdrmapper.ui.viewmodel.HomeViewModel
import dev.zt64.sdrmapper.ui.viewmodel.IntroViewModel
import dev.zt64.sdrmapper.ui.viewmodel.MapViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::IntroViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::MapViewModel)
}