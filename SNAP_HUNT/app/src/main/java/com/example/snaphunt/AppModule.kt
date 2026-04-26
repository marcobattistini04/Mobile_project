package com.example.snaphunt

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.example.snaphunt.data.repositories.ThemeRepository
import com.example.snaphunt.ui.theme.ThemeViewModel
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModel

val Context.dataStore by preferencesDataStore("theme")

val appModule = module {
    single {get<Context>().dataStore}
    single { ThemeRepository(get()) }
    viewModel { ThemeViewModel(get()) }
}