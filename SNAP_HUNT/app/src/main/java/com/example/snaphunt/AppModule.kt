package com.example.snaphunt

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.example.snaphunt.data.repositories.authentication.AuthRepository
import com.example.snaphunt.data.repositories.user_settings.SettingsCloudRepository
import com.example.snaphunt.data.repositories.user_settings.SettingsRepository
import com.example.snaphunt.presentation.sign_in.AuthViewModel
import com.example.snaphunt.user_settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

val Context.dataStore by preferencesDataStore("theme")

val appModule = module {
    single {get<Context>().dataStore}
    single { provideSupabaseClient() }
    single { AuthRepository(androidContext(), get())}
    single { SettingsCloudRepository(get()) }

    single { SettingsRepository(get(), get(), get()) }
    viewModel {
        AuthViewModel(
            get(),
            get()
        )
    }
    viewModel { SettingsViewModel(get()) }

}