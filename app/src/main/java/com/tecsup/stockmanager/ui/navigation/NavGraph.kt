package com.tecsup.stockmanager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.stockmanager.ui.detail.ProductDetailScreen
import com.tecsup.stockmanager.ui.form.ProductFormScreen
import com.tecsup.stockmanager.ui.list.ProductListScreen
import com.tecsup.stockmanager.ui.stats.StatsScreen

// Rutas de navegación como constantes
object Routes {
    const val LIST = "list"
    const val DETAIL = "detail/{productoId}"
    const val FORM = "form?productoId={productoId}"
    const val STATS = "stats"

    fun detailRoute(id: Int) = "detail/$id"
    fun formRoute(id: Int? = null) = if (id != null) "form?productoId=$id" else "form"
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LIST
    ) {
        // Pantalla principal — Lista
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
                }
            )
        }

        // Detalle de producto
        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("productoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("productoId") ?: return@composable
            ProductDetailScreen(
                productoId = id,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { editId ->
                    navController.navigate(Routes.formRoute(editId))
                }
            )
        }

        // Formulario crear/editar
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

        // Estadísticas
        composable(Routes.STATS) {
            StatsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}