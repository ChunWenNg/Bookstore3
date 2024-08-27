package com.example.bookstore3.screens

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.Image
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import coil.compose.rememberAsyncImagePainter
import com.example.bookstore3.data.books
import com.example.bookstore3.data.readBook
import com.example.bookstore3.data.supabase
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BookDetailsScreen(navController: NavController, bookId: String?) {
    val coroutineScope = rememberCoroutineScope()
    var book by remember { mutableStateOf<books?>(null) }
    var imageUrl by remember { mutableStateOf("") }

    // Handle the back button behavior
    BackHandler {
        navController.popBackStack()
    }

    // Fetch book details based on bookId
    LaunchedEffect(bookId) {
        coroutineScope.launch {
            book = bookId?.let { id ->
                readBook().firstOrNull { it.id == id }
            }
        }
    }


    // Fetch image URL after book is fetched
    LaunchedEffect(book?.bookImage) {
        coroutineScope.launch {
            book?.let {
                val fileName = getNewImageUrl(id = it.id)
                val bucket = supabase.storage.from("bookImage")
                val url = fileName?.let { bucket.createSignedUrl(path = it, expiresIn = 3.minutes) }
                Log.d("Image URL", "Fetched URL: $url")
                imageUrl = url ?: ""
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF0D47A1), Color(0xFF42A5F5))
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        book?.let { selectedBook ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Text("Back")
                    }

                    Text(
                        "Book Details",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color.LightGray,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = { navController.navigate("edit_book/${selectedBook.id}") }
                    ) {
                        Text("Edit Book")
                    }
                }

                // Display the image
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .size(150.dp)
                        .align(Alignment.CenterHorizontally),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = selectedBook.bookTitle,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = selectedBook.bookAuthor,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = selectedBook.bookDesc,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } ?: run {
            BasicText(
                text = "Loading...",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.LightGray)
            )
        }
    }
}

