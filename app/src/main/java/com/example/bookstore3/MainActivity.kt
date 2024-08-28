package com.example.bookstore3

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

    private val permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Check and request permissions
        checkAndRequestPermissions()

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
                Log.d("Auth", "Signed in as anonymous user: $user")
            } catch (e: Exception) {
                Log.e("Auth", "Sign in error: ${e.message}")
            }
        }
    }

    // Function to check and request permissions
    private fun checkAndRequestPermissions() {
        val notGrantedPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (notGrantedPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, notGrantedPermissions.toTypedArray(), 0)
        }
    }
}
