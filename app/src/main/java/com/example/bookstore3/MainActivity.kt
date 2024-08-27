package com.example.bookstore3

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.bookstore3.ui.theme.Bookstore3Theme
import com.example.bookstore3.navigation.Navigation
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.auth
import androidx.lifecycle.lifecycleScope
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.launch


val supabase = createSupabaseClient(
    supabaseUrl = "https://gilbcsillejbghvnrwbw.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImdpbGJjc2lsbGVqYmdodm5yd2J3Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MjQzNzU5NTIsImV4cCI6MjAzOTk1MTk1Mn0.1EHEIFPZjv2y_G8jm6nDx_iq5gsclBYALHYw5S-Nx7c"
) {
    install(Postgrest)
    install(Storage)
}



class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        signInAnonymously()

        setContent {
            Bookstore3Theme {
                Navigation()
            }
        }
    }

    // Function to sign in anonymously
    private fun signInAnonymously() {
        lifecycleScope.launch {
            try {
                val user = supabase.auth.signInAnonymously()
                Log.d("Auth", "Signed in as anonymous user: ${user}")
            } catch (e: Exception) {
                Log.e("Auth", "Sign in error: ${e.message}")
            }
        }
    }
//    private fun signInAnonymously() {
//        lifecycleScope.launch {
//            try {
//                supabase.auth.signInAnonymously(captchaToken = "token")
//            } catch (e: Exception) {
//                Log.e("Auth", "Sign in error: ${e.message}")
//            }
//        }
//    }
}