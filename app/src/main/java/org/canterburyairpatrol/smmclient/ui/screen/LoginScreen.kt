package org.canterburyairpatrol.smmclient.ui.screen

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import org.canterburyairpatrol.smmclient.R
import org.canterburyairpatrol.smmclient.data.SMMConnectionDetails
import org.canterburyairpatrol.smmclient.ui.activity.AssetSelectorActivity

class LoginScreen(private val context: Context) {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Content(onLoginSuccess: () -> Unit) {
        var connectionDetails by remember { mutableStateOf(SMMConnectionDetails("", "", "")) }

        Column(modifier = Modifier.fillMaxSize()) {
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
                // Perform login logic here
                // For simplicity, assume successful login
                val intent = Intent(context, AssetSelectorActivity::class.java)
                intent.putExtra("connectionDetails", connectionDetails)
                context.startActivity(intent)
                onLoginSuccess.invoke()
            }) {
                Text(stringResource(id = R.string.connect))
            }
        }
    }
}
