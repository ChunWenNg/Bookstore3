package com.example.bookstore3.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bookstore3.data.createBook
import com.example.bookstore3.data.books
import com.example.bookstore3.data.uploadImage
import com.example.bookstore3.data.getImageUrl
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import com.example.bookstore3.data.getBookId


//@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
//@Composable
//fun CreateBookScreen(navController: NavController) {
//    var bookImageUri by remember { mutableStateOf<Uri?>(null) }
//    var bookName by remember { mutableStateOf("") }
//    var bookAuthor by remember { mutableStateOf("") }
//    var bookDescription by remember { mutableStateOf("") }
//    val context = LocalContext.current
//    val scrollState = rememberScrollState()
//    val creationDate = remember { SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()) }
//
//    // Image picker and camera launchers
//    val imagePickerLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri: Uri? -> bookImageUri = uri }
//
//    val cameraLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.TakePicturePreview()
//    ) { bitmap ->
//        bitmap?.let {
//            val uri = Uri.parse(MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, null, null))
//            bookImageUri = uri
//        }
//    }
//
//    Column(modifier = Modifier.fillMaxSize()) {
//        TopAppBar(
//            title = { Text("Create a New Book", color = Color.White) },
//            actions = {
//                IconButton(onClick = { navController.popBackStack() }) {
//                    Icon(
//                        painter = painterResource(id = android.R.drawable.ic_menu_revert),
//                        contentDescription = "Back",
//                        tint = Color.White
//                    )
//                }
//            },
//            colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFF0D47A1))
//        )
//
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(
//                    Brush.linearGradient(
//                        colors = listOf(Color(0xFF0D47A1), Color(0xFF42A5F5)),
//                        start = Offset.Infinite,
//                        end = Offset.Zero
//                    )
//                )
//                .padding(16.dp)
//                .verticalScroll(scrollState)
//        ) {
//            Column(modifier = Modifier.fillMaxSize()) {
//
//                // Inline ImageBox Composable Code
//                val context = LocalContext.current
//
//                Box(
//                    modifier = Modifier
//                        .size(150.dp)
//                        .background(Color.Gray)
//                        .clickable {
//                            val options = arrayOf("Photo Library", "Take a Picture", "Cancel")
//                            val builder = android.app.AlertDialog.Builder(context)
//                            builder.setTitle("Select an Option")
//                            builder.setItems(options) { dialog, which ->
//                                when (which) {
//                                    0 -> imagePickerLauncher.launch("image/*")
//                                    1 -> cameraLauncher.launch()
//                                    2 -> dialog.dismiss()
//                                }
//                            }
//                            builder.show()
//                        },
//                    contentAlignment = Alignment.Center
//                ) {
//                    if (bookImageUri != null) {
//                        val imageBitmap: ImageBitmap =
//                            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, bookImageUri!!)).asImageBitmap()
//                        Image(
//                            bitmap = imageBitmap,
//                            contentDescription = "Book Image",
//                            contentScale = ContentScale.Crop,
//                            modifier = Modifier.fillMaxSize()
//                        )
//                    } else {
//                        Text(text = "Tap to add image", color = Color.White)
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                TextField(
//                    value = bookName,
//                    onValueChange = { bookName = it },
//                    label = { Text("Book Name", color = Color.White) },
//                    modifier = Modifier.fillMaxWidth()
//                )
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                TextField(
//                    value = bookAuthor,
//                    onValueChange = { bookAuthor = it },
//                    label = { Text("Author", color = Color.White) },
//                    modifier = Modifier.fillMaxWidth()
//                )
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                TextField(
//                    value = bookDescription,
//                    onValueChange = { bookDescription = it },
//                    label = { Text("Description", color = Color.White) },
//                    modifier = Modifier.fillMaxWidth()
//                )
//
//                Spacer(modifier = Modifier.height(24.dp))
//
//                Button(
//                    onClick = {
//                        // Launch a coroutine to handle image upload and book creation
//                        LaunchedEffect(bookImageUri) {
//                            if (bookImageUri != null) {
//                                // Upload image
//                                val fileName = "book_${System.currentTimeMillis()}"
//                                val imageFile = File(bookImageUri!!.path!!)
//                                uploadImage(imageFile, fileName)
//
//                                // Get image URL
//                                val imageUrl = getImageUrl("bookImage", fileName)
//
//                                // Create the new book entry
//                                val newBook = books(
//                                    id = 0, // Placeholder
//                                    bookTitle = bookName,
//                                    bookAuthor = bookAuthor,
//                                    bookDesc = bookDescription,
//                                    date = creationDate,
//                                    bookImage = imageUrl.first().signedURL
//                                )
//
//                                createBook(newBook)
//                                Toast.makeText(context, "Book Created", Toast.LENGTH_SHORT).show()
//                                navController.navigate("book_list")
//                            } else {
//                                Toast.makeText(context, "Please select an image", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    },
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Text("Save Book")
//                }
//            }
//        }
//    }
//}


@Composable
fun ImageBox(
    bookImageUri: Uri?,
    imagePickerLauncher: ManagedActivityResultLauncher<String, Uri?>,
    cameraLauncher: ManagedActivityResultLauncher<Void?, Bitmap?>
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .size(150.dp)
            .background(Color.Gray)
            .clickable {
                val options = arrayOf("Photo Library", "Take a Picture", "Cancel")
                val builder = android.app.AlertDialog.Builder(context)
                builder.setTitle("Select an Option")
                builder.setItems(options) { dialog, which ->
                    when (which) {
                        0 -> imagePickerLauncher.launch("image/*")
                        1 -> cameraLauncher.launch()
                        2 -> dialog.dismiss()
                    }
                }
                builder.show()
            },
        contentAlignment = Alignment.Center
    ) {
        if (bookImageUri != null) {
            val imageBitmap: ImageBitmap =
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, bookImageUri!!))
                    .asImageBitmap()
            Image(
                bitmap = imageBitmap,
                contentDescription = "Book Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(text = "Tap to add image", color = Color.White)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CreateBookScreen(navController: NavController) {
    var bookImageUri by remember { mutableStateOf<Uri?>(null) }
    var bookName by remember { mutableStateOf("") }
    var bookAuthor by remember { mutableStateOf("") }
    var bookDescription by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val creationDate = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    // Image picker and camera launchers
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> bookImageUri = uri }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            val uri = Uri.parse(MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, null, null))
            bookImageUri = uri
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Create a New Book", color = Color.White) },
            actions = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_revert),
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFF0D47A1))
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF0D47A1), Color(0xFF42A5F5)),
                        start = Offset.Infinite,
                        end = Offset.Zero
                    )
                )
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // ImageBox Composable Function
                ImageBox(
                    bookImageUri = bookImageUri,
                    imagePickerLauncher = imagePickerLauncher,
                    cameraLauncher = cameraLauncher
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = bookName,
                    onValueChange = { bookName = it },
                    label = { Text("Book Name", color = Color.Black) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = bookAuthor,
                    onValueChange = { bookAuthor = it },
                    label = { Text("Author", color = Color.Black) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = bookDescription,
                    onValueChange = { bookDescription = it },
                    label = { Text("Description", color = Color.Black) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            // Trigger Save action when Enter is pressed on the description field
                            coroutineScope.launch {
                                if (bookImageUri != null) {
                                    try {
                                        val inputStream = context.contentResolver.openInputStream(bookImageUri!!)
                                        if (inputStream != null) {
                                            val fileName = "book_${System.currentTimeMillis()}.jpg"

                                            // Upload image
                                            uploadImage(inputStream, fileName)

                                            // Get image URL
                                            val imageUrl = getImageUrl("bookImage", fileName).firstOrNull()

                                            // Get the next book ID
                                            val newBookId = (getBookId() as? Int) ?: 0

                                            // Create the new book entry
                                            val newBook = books(
                                                id = newBookId.toString(),  // Ensure id is a String
                                                bookTitle = bookName,
                                                bookAuthor = bookAuthor,
                                                bookDesc = bookDescription,
                                                date = creationDate,
                                                bookImage = fileName
                                            )

                                            createBook(newBook)
                                            Toast.makeText(context, "Book Created", Toast.LENGTH_SHORT).show()
                                            navController.navigate("book_list")
                                        } else {
                                            Toast.makeText(context, "Failed to open image", Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "Please select an image", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            if (bookImageUri != null) {
                                try {
                                    val inputStream = context.contentResolver.openInputStream(bookImageUri!!)
                                    if (inputStream != null) {
                                        val fileName = "book_${System.currentTimeMillis()}.jpg"

                                        // Upload image
                                        uploadImage(inputStream, fileName)

                                        // Get image URL
                                        val imageUrl = getImageUrl("bookImage", fileName).firstOrNull()
                                        // Get the next book ID
                                        val newBookId = (getBookId() as? Int) ?: 0

                                        // Create the new book entry
                                        val newBook = books(
                                            id = newBookId.toString(),  // Ensure id is a String
                                            bookTitle = bookName,
                                            bookAuthor = bookAuthor,
                                            bookDesc = bookDescription,
                                            date = creationDate,
                                            bookImage = fileName ?: ""
                                        )

                                        createBook(newBook)
                                        Toast.makeText(context, "Book Created", Toast.LENGTH_SHORT).show()
                                        navController.navigate("book_list")
                                    } else {
                                        Toast.makeText(context, "Failed to open image", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "Please select an image", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Book")
                }
            }
        }
    }
}
