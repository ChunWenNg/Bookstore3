package com.example.bookstore3.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Count
import io.github.jan.supabase.storage.SignedUrl
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.util.UUID
import kotlin.time.Duration.Companion.minutes

val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            coerceInputValues = true // This should coerce nulls to default values
            isLenient = true
        })
    }
}

// Create the Supabase client
val supabase = createSupabaseClient(
    supabaseUrl = "https://gilbcsillejbghvnrwbw.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImdpbGJjc2lsbGVqYmdodm5yd2J3Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MjQzNzU5NTIsImV4cCI6MjAzOTk1MTk1Mn0.1EHEIFPZjv2y_G8jm6nDx_iq5gsclBYALHYw5S-Nx7c"
) {
    install(Postgrest)
    install(Storage)
//    install(Auth)  // Uncomment if you need authentication
}

@Serializable
data class books(
    val id: String = UUID.randomUUID().toString(),
    val bookTitle: String,
    val bookAuthor: String,
    val bookDesc: String,
    val date: String,
    val bookImage: String,
)

@Serializable
data class SignedUrl(
    val signedURL: String? = "" // Default to empty string if null
)


// CRUD Operations for Books

suspend fun createBook(book: books) {
    withContext(Dispatchers.IO) {
        supabase.from("books").insert(book.copy(id = UUID.randomUUID().toString()))
    }
}

suspend fun readBook(): List<books> {
    return withContext(Dispatchers.IO) {
        supabase
            .from("books")
            .select()
            .decodeList<books>()
    }
}

suspend fun updateBook(id: String, title: String, author: String, desc: String) {
    withContext(Dispatchers.IO) {
        supabase.from("books").update(
            mapOf(
                "bookTitle" to title,
                "bookAuthor" to author,
                "bookDesc" to desc
            )
        ) {
            filter {
                eq("id", id)
            }
        }
    }
}


suspend fun deleteBook(id: Int) {
    withContext(Dispatchers.IO) {
        supabase
            .from("books")
            .delete {
                filter {
                    eq("id", id)
                }
            }
    }
}

// Image and File Operations

suspend fun uploadImage(inputStream: InputStream?, fileName: String) {
    if (inputStream == null) return

    // Convert InputStream to ByteArray
    val byteArray = inputStream.readBytes()

    withContext(Dispatchers.IO) {
        val bucket = supabase.storage.from("bookImage")
        bucket.upload(fileName, byteArray, upsert = false)
    }
}

suspend fun downloadImage(url: String) {
    withContext(Dispatchers.IO) {
        val bucket = supabase.storage.from("bookImage")
        bucket.downloadAuthenticated(url)
    }
}

suspend fun listFilesInBucket(): List<String> {
    return withContext(Dispatchers.IO) {
        val bucket = supabase.storage.from("bookImage")
        val files = bucket.list()
        files.map { it.name }
    }
}

suspend fun deleteImage(fileName: String) {
    withContext(Dispatchers.IO) {
        val bucket = supabase.storage.from("bookImage")
        bucket.delete(fileName)
    }
}

suspend fun getImageUrl(bucketName: String, fileName: String): List<String> {
    return withContext(Dispatchers.IO) {
        val urls = supabase.storage.from(bucketName).createSignedUrls(20.minutes, fileName)
        urls.map { it.signedURL ?: "" } // Replace null with an empty string
    }
}

suspend fun getBookId(): Any {
    val count = supabase
        .from("books")
        .select(columns = Columns.list("id")) {
            count(Count.EXACT)
        }

    return count ?: -1
}
