package org.delcom.module

import org.delcom.repositories.IPlantRepository
import org.delcom.repositories.PlantRepository
import org.delcom.services.PlantService
import org.delcom.services.ProfileService
import org.delcom.services.WatchService
import org.delcom.repositories.IWatchRepository
import org.delcom.repositories.WatchRepository
import org.koin.dsl.module


val appModule = module {
    // Plant Repository
    single<IPlantRepository> {
        PlantRepository()
    }

    single<IWatchRepository> {
        WatchRepository()
    }


    // Plant Service
    single {
        PlantService(get())
    }

    single {
        WatchService(get())
    }

    // Profile Service
    single {
        ProfileService()
    }
}