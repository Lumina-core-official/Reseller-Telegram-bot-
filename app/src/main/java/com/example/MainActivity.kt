package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.CyberOpsApp
import com.example.ui.CyberOpsViewModel
import com.example.ui.theme.CyberOpsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CyberOpsTheme {
                val viewModel: CyberOpsViewModel = viewModel()
                CyberOpsApp(viewModel = viewModel)
            }
        }
    }
}
