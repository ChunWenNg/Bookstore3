package com.example.bookstore3.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bookstore3.screens.*

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("book_list") { BookListingScreen(navController) }
        composable("create_book") { CreateBookScreen(navController) }
        composable("book_details/{bookId}") { backStackEntry ->
            BookDetailsScreen(navController, backStackEntry.arguments?.getString("bookId"))
        }
        composable("edit_book/{bookId}") { backStackEntry ->
            EditBookScreen(navController, backStackEntry.arguments?.getString("bookId"))
        }
    }
}
