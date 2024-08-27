package com.example.bookstore3.screens

import android.util.Log
import android.widget.Toast
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
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import com.example.bookstore3.data.books
import com.example.bookstore3.data.readBook
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import coil.compose.rememberAsyncImagePainter
import com.example.bookstore3.data.deleteBook
import com.example.bookstore3.data.supabase
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BookListingScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var bookList by remember { mutableStateOf<List<books>>(emptyList()) }

    // Handle the back button behavior
    BackHandler {
        Toast.makeText(context, "Please use the logout button to exit.", Toast.LENGTH_SHORT).show()
    }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            // Fetch all books from the database
            bookList = readBook()
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
                        navController.navigate("login") {
                            popUpTo("book_list") { inclusive = true }
                        }
                    }
                ) {
                    Text("Logout")
                }

                Text(
                    "SS BookStore",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = Color.LightGray,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = { navController.navigate("create_book") }
                ) {
                    Text("+")
                }
            }

            if (bookList.isEmpty()) {
                BasicText(
                    text = "No books available. Add more books!",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.LightGray)
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    bookList.forEach { book ->
                        BookItem(book = book, navController = navController, bookList = bookList) { updatedList ->
                            bookList = updatedList
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun BookItem(book: books, navController: NavController, bookList: List<books>, onBookListUpdated: (List<books>) -> Unit) {
    val swipeableState = rememberSwipeableState(0)
    val coroutineScope = rememberCoroutineScope()
    var imageUrlResult by remember { mutableStateOf("") }

    LaunchedEffect(book.bookImage) {
        coroutineScope.launch {
            val fileName = getNewImageUrl(id = book.id)
            val bucket = supabase.storage.from("bookImage")
            val url = fileName?.let { bucket.createSignedUrl(path = it, expiresIn = 3.minutes) }
            Log.d("Image URL", "Fetched URL: $url")
            imageUrlResult = url ?: ""  // Correctly update imageUrlResult
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.Transparent)
            .border(
                width = 1.dp,
                color = Color.Black.copy(alpha = 0.5f),
                shape = RectangleShape
            )
            .padding(8.dp)
            .pointerInput(Unit) {
                detectTapGestures { _ ->
                    navController.navigate("book_details/${book.id}")
                }
            }
            .swipeable(
                state = swipeableState,
                anchors = mapOf(0f to 0, 1f to 1),
                thresholds = { _, _ -> FractionalThreshold(0.5f) },
                orientation = Orientation.Horizontal
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .align(Alignment.CenterStart)
        ) {
            if (swipeableState.offset.value > 0) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            deleteBook(book.id)
                            val bucket = supabase.storage.from("bookImage")
                            bucket.delete(imageUrlResult)
                            val updatedList = readBook()
                            onBookListUpdated(updatedList)
                        }
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Delete")
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {

                if (imageUrlResult.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUrlResult),
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Placeholder or default image if the URL is empty
                    Image(
                        painter = rememberAsyncImagePainter("https://via.placeholder.com/80"), // You can replace this with a real placeholder URL
                        contentDescription = "Default Image",
                        modifier = Modifier.size(80.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = book.bookTitle,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = book.bookAuthor,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White
                    )
                )
                Text(
                    text = book.date,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White
                    )
                )
            }
        }
    }
}


@Serializable
data class BookImage(
    val bookImage: String?
)


suspend fun getNewImageUrl(id: String): String? {
    val response = supabase
        .from("books")
        .select(columns = Columns.list("bookImage")) {
            filter {
                eq("id", id) // Correctly filter where the id column matches the provided id
            }
        }
        .decodeSingle<BookImage>()

    return response.bookImage
}