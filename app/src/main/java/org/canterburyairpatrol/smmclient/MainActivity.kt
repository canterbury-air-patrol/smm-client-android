package org.canterburyairpatrol.smmclient

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import org.canterburyairpatrol.smmclient.data.SMMConnectionDetails
import org.canterburyairpatrol.smmclient.ui.activity.AssetSelectorActivity
import org.canterburyairpatrol.smmclient.ui.screen.LoginScreen
import org.canterburyairpatrol.smmclient.ui.theme.SmmclientandroidTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var connectionDetails by remember { mutableStateOf(SMMConnectionDetails("", "", "")) }

            SmmclientandroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column() {
                        OutlinedTextField(
                            value = connectionDetails.serverURL,
                            onValueChange = { connectionDetails = connectionDetails.copy(serverURL = it) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                            label = { Text(stringResource(id = R.string.server_url)) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = true,
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = connectionDetails.username,
                            onValueChange = { connectionDetails = connectionDetails.copy(username = it) },
                            label = { Text(stringResource(R.string.username)) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = true,
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = connectionDetails.password,
                            onValueChange = { connectionDetails = connectionDetails.copy(password = it) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            visualTransformation = PasswordVisualTransformation(),
                            label = { Text(stringResource(R.string.password)) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = true,
                            singleLine = true
                        )
                        Button(onClick = {
                            startActivity(Intent(this@MainActivity, AssetSelectorActivity::class.java))
                        }) {
                            Text(stringResource(id = R.string.connect))
                        }
                    }
                }
            }
        }
    }
}