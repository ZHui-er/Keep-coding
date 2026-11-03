package com.keepcoding.blehealth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.keepcoding.blehealth.ui.AppViewModel
import com.keepcoding.blehealth.ui.AppViewModelFactory
import com.keepcoding.blehealth.ui.BleHealthApp
import com.keepcoding.blehealth.ui.theme.BleHealthTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as BleHealthApplication).container
        setContent {
            BleHealthTheme {
                val appViewModel: AppViewModel = viewModel(
                    factory = AppViewModelFactory(
                        deviceRepository = container.deviceRepository,
                        healthRepository = container.healthRepository,
                        scenarioController = container.scenarioController,
                    ),
                )
                BleHealthApp(viewModel = appViewModel)
            }
        }
    }
}
