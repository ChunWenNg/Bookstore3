package com.example.bookstore3.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val usernameFocusRequester = remember { FocusRequester() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF0D47A1), Color(0xFF42A5F5))
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)) {
                // SS BookStore heading

                Text(
                    "SS BookStore",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = Color.LightGray,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 25.dp),
                    textAlign = TextAlign.Center
                )

                // Username TextField
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username", color = Color.LightGray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(usernameFocusRequester), // Set the FocusRequester
                    textStyle = TextStyle(color = Color.LightGray),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Text
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password TextField
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.LightGray),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Password
                    ),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            if (username == "abcdefg" && password == "1234567") {
                                navController.navigate("book_list")
                            } else {
                                Toast.makeText(
                                    context,
                                    "Wrong username or password",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // Clear the text fields
                                username = ""
                                password = ""
                                // Request focus to the username field
                                focusManager.clearFocus()
                                usernameFocusRequester.requestFocus()
                            }
                        }
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Login Button
                Button(
                    onClick = {
                        if (username == "abcdefg" && password == "1234567") {
                            navController.navigate("book_list")
                        } else {
                            Toast.makeText(
                                context,
                                "Wrong username or password",
                                Toast.LENGTH_SHORT
                            ).show()
                            // Clear the text fields
                            username = ""
                            password = ""
                            // Request focus to the username field
                            focusManager.clearFocus()
                            usernameFocusRequester.requestFocus()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Login")
                }
            }
        }
    }

    // Initially request focus to username field when the screen loads
    LaunchedEffect(Unit) {
        usernameFocusRequester.requestFocus()
    }
}