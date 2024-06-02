package org.canterburyairpatrol.smmclient.ui.screen

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.preference.PreferenceManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.canterburyairpatrol.smmclient.ConnectionSingleton
import org.canterburyairpatrol.smmclient.R
import org.canterburyairpatrol.smmclient.data.SMMConnectionDetails
import org.canterburyairpatrol.smmclient.ui.activity.AssetSelectorActivity

class LoginScreen(private val context: Context) {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Content(onLoginSuccess: () -> Unit) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        var saveDetails by remember {
            mutableStateOf(
                sharedPreferences.getBoolean(
                    "rememberLogin",
                    false
                )
            )
        }
        var connectionDetails by remember {
            mutableStateOf(
                SMMConnectionDetails(
                    sharedPreferences.getString("serverURL", "") ?: "",
                    sharedPreferences.getString("username", "") ?: "",
                    sharedPreferences.getString("password", "") ?: ""
                )
            )
        }
        var errorMessage by remember { mutableStateOf("") }

        Row(Modifier.fillMaxHeight())
        {
            Column(
                modifier = Modifier.fillMaxWidth().align(Alignment.CenterVertically),
            ) {
                OutlinedTextField(
                    value = connectionDetails.serverURL,
                    onValueChange = {
                        connectionDetails = connectionDetails.copy(serverURL = it)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    label = { Text(stringResource(id = R.string.server_url)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = true,
                    singleLine = true
                )
                OutlinedTextField(
                    value = connectionDetails.username,
                    onValueChange = {
                        connectionDetails = connectionDetails.copy(username = it)
                    },
                    label = { Text(stringResource(R.string.username)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = true,
                    singleLine = true
                )
                OutlinedTextField(
                    value = connectionDetails.password,
                    onValueChange = {
                        connectionDetails = connectionDetails.copy(password = it)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation(),
                    label = { Text(stringResource(R.string.password)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = true,
                    singleLine = true
                )
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    onClick = {
                        GlobalScope.launch {
                            var connection = ConnectionSingleton.getInstance()
                            connection.setConnectionDetails(connectionDetails)
                            errorMessage = connection.connect()
                            if (errorMessage == "") {
                                val editor = sharedPreferences.edit()
                                if (saveDetails) {
                                    editor.putString("serverURL", connectionDetails.serverURL)
                                    editor.putString("username", connectionDetails.username)
                                    editor.putString("password", connectionDetails.password)
                                } else {
                                    editor.remove("serverURL")
                                    editor.remove("username")
                                    editor.remove("password")
                                }
                                editor.putBoolean("rememberLogin", saveDetails)
                                editor.commit()
                                var intent = Intent(context, AssetSelectorActivity::class.java)
                                context.startActivity(intent)
                                onLoginSuccess.invoke()
                            }
                        }
                    }) {
                    Text(stringResource(id = R.string.connect))
                }
                Text(errorMessage)
                Row(Modifier.fillMaxWidth()) {
                    Checkbox(checked = saveDetails,
                        onCheckedChange = {
                            saveDetails = it
                        })
                    Text(
                        stringResource(R.string.remember_login_details),
                        modifier = Modifier.align(Alignment.CenterVertically).weight(1f)
                    )
                    Button(onClick = {
                        val editor = sharedPreferences.edit()
                        editor.remove("serverURL")
                        editor.remove("username")
                        editor.remove("password")
                        editor.remove("rememberLogin")
                        editor.commit()
                        connectionDetails = SMMConnectionDetails("", "", "")
                    }) {
                        Text(stringResource(id = R.string.forget_login_details))
                    }
                }
            }
        }
    }
}
