package dev.greecetripplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import dev.greecetripplanner.ui.GreeceTripTheme
import dev.greecetripplanner.ui.TripNavHost
import dev.greecetripplanner.ui.TripViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: TripViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val darkModeOverride by viewModel.darkModeOverride.collectAsState()
            val useDark = darkModeOverride ?: isSystemInDarkTheme()

            GreeceTripTheme(darkTheme = useDark) {
                val navController = rememberNavController()
                TripNavHost(navController = navController, viewModel = viewModel)
            }
        }
    }
}
