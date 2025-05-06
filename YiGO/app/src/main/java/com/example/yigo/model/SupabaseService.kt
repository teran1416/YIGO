package com.example.yigo.model

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage

object SupabaseService {

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = "https://gvnnxnwwnxsnwdrliinh.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imd2bm54bnd3bnhzbndkcmxpaW5oIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDU5MzE2NTksImV4cCI6MjA2MTUwNzY1OX0.qZxtVTUhaQJ7c85zOJzOIYPzj_HGJiAltxm9UAVc0Ag"
        ) {
            install(io.github.jan.supabase.auth.Auth)
            install(io.github.jan.supabase.postgrest.Postgrest)
            install(io.github.jan.supabase.storage.Storage)
        }
    }

    val auth get() = client.auth
    val database get() = client.postgrest
    val storage get() = client.storage
}