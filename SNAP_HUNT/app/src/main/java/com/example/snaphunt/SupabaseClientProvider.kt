package com.example.snaphunt

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest

fun provideSupabaseClient(): SupabaseClient {
    return createSupabaseClient(
        supabaseUrl = "https://rgfiyenbbbclsoaaoaqb.supabase.co",
        supabaseKey = "sb_publishable_z2spDn3i9gZl0aiytOETJw_Z0GflI2z"
    ) {
        install(Auth)
        install(Postgrest)
    }
}