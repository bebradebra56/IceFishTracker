package com.icefishi.trackesof.hutrjk.presentation.di

import com.icefishi.trackesof.hutrjk.data.repo.IceFishTrackerRepository
import com.icefishi.trackesof.hutrjk.data.shar.IceFishTrackerSharedPreference
import com.icefishi.trackesof.hutrjk.data.utils.IceFishTrackerPushToken
import com.icefishi.trackesof.hutrjk.data.utils.IceFishTrackerSystemService
import com.icefishi.trackesof.hutrjk.domain.usecases.IceFishTrackerGetAllUseCase
import com.icefishi.trackesof.hutrjk.presentation.pushhandler.IceFishTrackerPushHandler
import com.icefishi.trackesof.hutrjk.presentation.ui.load.IceFishTrackerLoadViewModel
import com.icefishi.trackesof.hutrjk.presentation.ui.view.IceFishTrackerViFun
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val iceFishTrackerModule = module {
    factory {
        IceFishTrackerPushHandler()
    }
    single {
        IceFishTrackerRepository()
    }
    single {
        IceFishTrackerSharedPreference(get())
    }
    factory {
        IceFishTrackerPushToken()
    }
    factory {
        IceFishTrackerSystemService(get())
    }
    factory {
        IceFishTrackerGetAllUseCase(
            get(), get(), get()
        )
    }
    factory {
        IceFishTrackerViFun(get())
    }
    viewModel {
        IceFishTrackerLoadViewModel(get(), get(), get())
    }
}