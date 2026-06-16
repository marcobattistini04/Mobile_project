package com.example.snaphunt

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.preferencesDataStore
import com.example.snaphunt.data.local.AppDatabase
import com.example.snaphunt.data.local.DatabaseProvider.getDatabase
import com.example.snaphunt.data.repositories.authentication.AuthRepository
import com.example.snaphunt.data.repositories.user_settings.SettingsCloudRepository
import com.example.snaphunt.data.repositories.user_settings.SettingsRepository
import com.example.snaphunt.image_recognition.ObjectDetectionViewModel
import com.example.snaphunt.image_recognition.ObjectDetector
import com.example.snaphunt.network.NetworkMonitor
import com.example.snaphunt.photos.PhotoGalleryViewModel
import com.example.snaphunt.photos.PhotoSyncViewModel
import com.example.snaphunt.photos.SyncManager
import com.example.snaphunt.presentation.sign_in.AuthViewModel
import com.example.snaphunt.user_settings.SettingsViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
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

    single { NetworkMonitor(get()) }

    viewModel {
        AuthViewModel(
            get(),
            get(),
            get()
        )


    }

    viewModel {SettingsViewModel(get()) }

    single { get<SupabaseClient>().storage }
    single { get<SupabaseClient>().postgrest }
    single {get<SupabaseClient>().auth}

    single { SyncManager(get(), get(), get()) }

    single { getDatabase(androidContext()) }

    single { get<AppDatabase>().pendingAttemptDao() }

    viewModel {PhotoSyncViewModel(get(), get(), get(), get())}
    viewModel { PhotoGalleryViewModel(get(), get(), get()) }

    single { ObjectDetector(androidContext()) }

    viewModel { ObjectDetectionViewModel(get()) }

}