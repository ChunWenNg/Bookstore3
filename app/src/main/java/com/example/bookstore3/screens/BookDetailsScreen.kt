package com.example.bookstore3.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import com.example.bookstore3.data.books
import com.example.bookstore3.data.readBook
import com.example.bookstore3.data.getImageUrl
import kotlinx.coroutines.launch
import androidx.compose.foundation.Image
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BookDetailsScreen(navController: NavController, bookId: String?) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var book by remember { mutableStateOf<books?>(null) }
    var imageUrl by remember { mutableStateOf("") }

    // Handle the back button behavior
    BackHandler {
        navController.popBackStack()
    }

    LaunchedEffect(bookId) {
        coroutineScope.launch {
            bookId?.let {
                // Fetch the book details from the database
                val bookId = it
                book = readBook().find { it.id == bookId }
                // Fetch the book image URL
//                book?.let { selectedBook ->
//                    val urls = getImageUrl("bookImage", selectedBook.bookImage)
//                    imageUrl = urls.firstOrNull() ?: ""
//                }
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

                // Display the book image
//                Image(
//                    painter = rememberAsyncImagePainter(imageUrl),
//                    contentDescription = null,
//                    modifier = Modifier
//                        .size(150.dp)
//                        .align(Alignment.CenterHorizontally),
//                    contentScale = ContentScale.Crop
//                )
//
//                Spacer(modifier = Modifier.height(16.dp))

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
