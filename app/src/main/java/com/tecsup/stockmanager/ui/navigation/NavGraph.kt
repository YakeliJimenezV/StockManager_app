package com.tecsup.stockmanager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.stockmanager.StockManagerApp
import com.tecsup.stockmanager.ui.auth.AuthScreen
import com.tecsup.stockmanager.ui.detail.ProductDetailScreen
import com.tecsup.stockmanager.ui.form.ProductFormScreen
import com.tecsup.stockmanager.ui.list.ProductListScreen
import com.tecsup.stockmanager.ui.stats.StatsScreen

object Routes {
    const val AUTH = "auth"
    const val LIST = "list"
    const val DETAIL = "detail/{productoId}"
    const val FORM = "form?productoId={productoId}"
    const val STATS = "stats"

    fun detailRoute(id: Int) = "detail/$id"
    fun formRoute(id: Int? = null) =
        if (id != null) "form?productoId=$id" else "form"
}

@Composable
fun NavGraph(productoIdDesdeNotificacion: Int? = null) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val app = context.applicationContext as StockManagerApp

    val startDestination = remember {
        if (app.authRepository.haySession) Routes.LIST else Routes.AUTH
    }

    LaunchedEffect(productoIdDesdeNotificacion) {
        if (productoIdDesdeNotificacion != null && app.authRepository.haySession) {
            navController.navigate(Routes.detailRoute(productoIdDesdeNotificacion))
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.AUTH) {
            AuthScreen(
                onLoginExitoso = {
                    navController.navigate(Routes.LIST) {
                        popUpTo(Routes.AUTH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LIST) {
            ProductListScreen(
                onNavigateToDetail = { id ->
                    navController.navigate(Routes.detailRoute(id))
                },
                onNavigateToForm = { id ->
                    navController.navigate(Routes.formRoute(id))
                },
                onNavigateToStats = {
                    navController.navigate(Routes.STATS)
                },
                onCerrarSesion = {
                    navController.navigate(Routes.AUTH) {
                        popUpTo(Routes.LIST) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(
                navArgument("productoId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("productoId")
                ?: return@composable
            ProductDetailScreen(
                productoId = id,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { editId ->
                    navController.navigate(Routes.formRoute(editId))
                }
            )
        }

        composable(
            route = Routes.FORM,
            arguments = listOf(
                navArgument("productoId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("productoId")
                ?.takeIf { it != -1 }
            ProductFormScreen(
                productoId = id,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.STATS) {
            StatsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}