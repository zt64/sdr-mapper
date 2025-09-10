package dev.zt64.sdrmapper.di

import dev.zt64.sdrmapper.domain.manager.NotificationManager
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val managerModule = module {
    singleOf(::NotificationManager)
}