package com.tecsup.stockmanager.ui.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.stockmanager.ui.detail.ProductDetailScreen
import com.tecsup.stockmanager.ui.form.ProductFormScreen
import com.tecsup.stockmanager.ui.list.ProductListScreen
import com.tecsup.stockmanager.ui.stats.StatsScreen
import com.tecsup.stockmanager.ui.theme.CoralLight
import com.tecsup.stockmanager.ui.theme.CoralPrimary
import com.tecsup.stockmanager.ui.theme.WarmWhite
import kotlinx.coroutines.delay

object Routes {
    const val SPLASH = "splash"
    const val LIST   = "list"
    const val DETAIL = "detail/{productoId}"
    const val FORM   = "form?productoId={productoId}"
    const val STATS  = "stats"

    fun detailRoute(id: Int) = "detail/$id"
    fun formRoute(id: Int? = null) = if (id != null) "form?productoId=$id" else "form"
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.SPLASH) {

        composable(Routes.SPLASH) {
            SplashScreen(onFinished = {
                navController.navigate(Routes.LIST) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }
            })
        }

        composable(Routes.LIST) {
            ProductListScreen(
                onNavigateToDetail = { navController.navigate(Routes.detailRoute(it)) },
                onNavigateToForm   = { navController.navigate(Routes.formRoute(it)) },
                onNavigateToStats  = { navController.navigate(Routes.STATS) }
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("productoId") { type = NavType.IntType })
        ) { back ->
            val id = back.arguments?.getInt("productoId") ?: return@composable
            ProductDetailScreen(
                productoId = id,
                onNavigateBack   = { navController.popBackStack() },
                onNavigateToEdit = { navController.navigate(Routes.formRoute(it)) }
            )
        }

        composable(
            route = Routes.FORM,
            arguments = listOf(navArgument("productoId") { type = NavType.IntType; defaultValue = -1 })
        ) { back ->
            val id = back.arguments?.getInt("productoId")?.takeIf { it != -1 }
            ProductFormScreen(productoId = id, onNavigateBack = { navController.popBackStack() })
        }

        composable(Routes.STATS) {
            StatsScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}

@Composable
private fun SplashScreen(onFinished: () -> Unit) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
        delay(1800L)
        onFinished()
    }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(700),
        label = "splash_fade"
    )

    Box(
        modifier = Modifier.fillMaxSize()
            .background(Brush.verticalGradient(listOf(CoralPrimary, CoralLight, Color(0xFFFFB347)))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(alpha)
        ) {
            // Ícono en caja blanca redondeada (estilo imagen de referencia)
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = WarmWhite.copy(alpha = 0.20f),
                modifier = Modifier.size(100.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Inventory2,
                        contentDescription = null,
                        tint = WarmWhite,
                        modifier = Modifier.size(56.dp)
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
            Text("StockManager", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = WarmWhite)
            Spacer(Modifier.height(6.dp))
            Text(
                "CONTROL DE INVENTARIO",
                fontSize = 11.sp, fontWeight = FontWeight.Bold,
                color = WarmWhite.copy(alpha = 0.75f),
                letterSpacing = 2.sp
            )
        }
    }
}