package com.andresilva.greecetripplanner.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.andresilva.greecetripplanner.ui.screens.plan.PlanScreen
import com.andresilva.greecetripplanner.ui.screens.show.ShowScreen

object Routes {
    const val PLAN = "plan"
    const val SHOW = "show"
}

@Composable
fun TripNavHost(navController: NavHostController) {
    val viewModel: TripViewModel = hiltViewModel()

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
            )
        }
    }
}
