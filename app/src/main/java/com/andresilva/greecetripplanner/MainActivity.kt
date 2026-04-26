package com.andresilva.greecetripplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.andresilva.greecetripplanner.ui.GreeceTripTheme
import com.andresilva.greecetripplanner.ui.TripNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GreeceTripTheme {
                val navController = rememberNavController()
                TripNavHost(navController = navController)
            }
        }
    }
}
