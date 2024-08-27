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
import com.example.bookstore3.data.books
import com.example.bookstore3.data.updateBook
import com.example.bookstore3.data.readBook
import kotlinx.coroutines.launch
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.TextField
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditBookScreen(navController: NavController, bookId: String?) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var book by remember { mutableStateOf<books?>(null) }
    var bookTitle by remember { mutableStateOf(TextFieldValue()) }
    var bookAuthor by remember { mutableStateOf(TextFieldValue()) }
    var bookDesc by remember { mutableStateOf(TextFieldValue()) }

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
                // Initialize text fields with current book data
                book?.let { selectedBook ->
                    bookTitle = TextFieldValue(selectedBook.bookTitle)
                    bookAuthor = TextFieldValue(selectedBook.bookAuthor)
                    bookDesc = TextFieldValue(selectedBook.bookDesc)
                }
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
                        "Edit Book",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color.LightGray,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    // Modify the navigation call in EditBookScreen
                    Button(
                        onClick = {
                            // Save the edited book data
                            coroutineScope.launch {
                                updateBook(
                                    id = selectedBook.id,
                                    title = bookTitle.text,
                                    author = bookAuthor.text,
                                    desc = bookDesc.text
                                )
                                // Navigate back to BookDetailsScreen and clear the stack
                                navController.navigate("book_details/${selectedBook.id}") {
                                    popUpTo("edit_book/${selectedBook.id}") { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        }
                    ) {
                        Text("Done")
                    }

                }

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = bookTitle,
                    onValueChange = { bookTitle = it },
                    label = { Text("Title") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Text
                    ),
                    keyboardActions = KeyboardActions(onNext = { /* Move focus to the next text field */ }),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = bookAuthor,
                    onValueChange = { bookAuthor = it },
                    label = { Text("Author") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Text
                    ),
                    keyboardActions = KeyboardActions(onNext = { /* Move focus to the next text field */ }),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = bookDesc,
                    onValueChange = { bookDesc = it },
                    label = { Text("Description") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Text
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        // Save the book when done editing
                        coroutineScope.launch {
                            updateBook(
                                id = selectedBook.id, // Pass book ID
                                title = bookTitle.text,
                                author = bookAuthor.text,
                                desc = bookDesc.text
                            )
                            navController.navigate("book_details/${selectedBook.id}")
                        }
                    }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 150.dp)
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
