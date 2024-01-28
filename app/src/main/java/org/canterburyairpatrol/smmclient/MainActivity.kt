package org.canterburyairpatrol.smmclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import org.canterburyairpatrol.smmclient.ui.screen.LoginScreen
import org.canterburyairpatrol.smmclient.ui.theme.SmmclientandroidTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SmmclientandroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    val loginScreen = LoginScreen(context = this)
                    loginScreen.Content {
                        // Handle login success event here if needed
                    }
                }
            }
        }
    }
}
