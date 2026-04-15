package org.canterburyairpatrol.smmclient.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.canterburyairpatrol.smmclient.R

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit
) {
    val connectionDetails = viewModel.connectionDetails
    val saveDetails = viewModel.saveDetails
    val errorMessage = viewModel.errorMessage
    val isLoading = viewModel.isLoading

    Row(Modifier.fillMaxHeight()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterVertically),
        ) {
            OutlinedTextField(
                value = connectionDetails.serverURL,
                onValueChange = { viewModel.updateServerURL(it) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                label = { Text(stringResource(id = R.string.server_url)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                singleLine = true
            )
            OutlinedTextField(
                value = connectionDetails.username,
                onValueChange = { viewModel.updateUsername(it) },
                label = { Text(stringResource(R.string.username)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                singleLine = true
            )
            OutlinedTextField(
                value = connectionDetails.password,
                onValueChange = { viewModel.updatePassword(it) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                label = { Text(stringResource(R.string.password)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                singleLine = true
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                enabled = !isLoading,
                onClick = {
                    viewModel.login(onLoginSuccess)
                }) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(id = R.string.connect))
                }
            }
            if (errorMessage.isNotEmpty()) {
                Text(errorMessage)
            }
            Row(Modifier.fillMaxWidth()) {
                Checkbox(
                    checked = saveDetails,
                    onCheckedChange = { viewModel.updateSaveDetails(it) },
                    enabled = !isLoading
                )
                Text(
                    stringResource(R.string.remember_login_details),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(1f)
                )
                Button(
                    onClick = { viewModel.forgetDetails() },
                    enabled = !isLoading
                ) {
                    Text(stringResource(id = R.string.forget_login_details))
                }
            }
        }
    }
}
