package dev.zt64.sdrmapper

import android.app.Application
import dev.zt64.sdrmapper.di.managerModule
import dev.zt64.sdrmapper.di.viewModelModule
import dev.zt64.sdrmapper.domain.manager.SdrUsbManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class SdrMapper : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@SdrMapper)
            modules(
                viewModelModule,
                managerModule,
                module {
                    singleOf(::SdrUsbManager)
                }
            )
        }
    }
}