package dev.greecetripplanner.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.greecetripplanner.ui.screens.map.MapScreen
import dev.greecetripplanner.ui.screens.plan.PlanScreen
import dev.greecetripplanner.ui.screens.show.ShowScreen

object Routes {
    const val PLAN = "plan"
    const val SHOW = "show"
    const val MAP = "map"
}

@Composable
fun TripNavHost(navController: NavHostController, viewModel: TripViewModel) {
    NavHost(navController = navController, startDestination = Routes.PLAN) {
        composable(Routes.PLAN) {
            PlanScreen(
                viewModel = viewModel,
                onSwitchToShow = {
                    viewModel.setMode(AppMode.SHOW)
                    navController.navigate(Routes.SHOW) {
                        launchSingleTop = true
                    }
                },
                onSwitchToMap = {
                    navController.navigate(Routes.MAP) {
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(Routes.SHOW) {
            ShowScreen(
                viewModel = viewModel,
                onSwitchToPlan = {
                    viewModel.setMode(AppMode.PLAN)
                    navController.navigate(Routes.PLAN) {
                        launchSingleTop = true
                        popUpTo(Routes.PLAN) { inclusive = true }
                    }
                },
                onSwitchToMap = {
                    navController.navigate(Routes.MAP) {
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(Routes.MAP) {
            MapScreen(
                viewModel = viewModel,
                onSwitchToPlan = {
                    viewModel.setMode(AppMode.PLAN)
                    navController.navigate(Routes.PLAN) {
                        launchSingleTop = true
                        popUpTo(Routes.PLAN) { inclusive = true }
                    }
                },
                onSwitchToShow = {
                    viewModel.setMode(AppMode.SHOW)
                    navController.navigate(Routes.SHOW) {
                        launchSingleTop = true
                    }
                },
            )
        }
    }
}
