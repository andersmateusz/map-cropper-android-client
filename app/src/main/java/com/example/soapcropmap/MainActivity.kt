package com.example.soapcropmap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.soapcropmap.view.BoxSelectionScreen
import com.example.soapcropmap.view.InputPointsFormScreen
import com.example.soapcropmap.ui.theme.SoapCropMapTheme
import com.example.soapcropmap.view.MainScreen

class MainActivity : ComponentActivity() {

    private val model: ViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SoapCropMapTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = ScreenType.HOME.name) {
                        composable(ScreenType.HOME.name) { MainScreen(navController, model) }
                        composable(ScreenType.PIXEL.name) { InputPointsFormScreen(
                            unitType = UnitType.PIXEL,
                            viewModel = model,
                            navController = navController,
                        ) }
                        composable(ScreenType.GPS.name) { InputPointsFormScreen(
                            unitType = UnitType.COORDINATES,
                            viewModel = model,
                            navController = navController,
                        ) }
                        composable(ScreenType.BOX.name) { BoxSelectionScreen(
                            nacController = navController,
                            model = model,
                        )
                        }
                    }
                }
            }
        }
    }
}

