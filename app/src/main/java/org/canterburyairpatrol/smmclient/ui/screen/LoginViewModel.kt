package org.canterburyairpatrol.smmclient.ui.screen

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.canterburyairpatrol.smmclient.ConnectionSingleton
import org.canterburyairpatrol.smmclient.data.SMMConnectionDetails

class LoginViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {

    var saveDetails by mutableStateOf(sharedPreferences.getBoolean("rememberLogin", false))
        private set

    var connectionDetails by mutableStateOf(
        SMMConnectionDetails(
            sharedPreferences.getString("serverURL", "") ?: "",
            sharedPreferences.getString("username", "") ?: "",
            sharedPreferences.getString("password", "") ?: ""
        )
    )
        private set

    var errorMessage by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun updateSaveDetails(save: Boolean) {
        saveDetails = save
    }

    fun updateServerURL(url: String) {
        connectionDetails = connectionDetails.copy(serverURL = url)
    }

    fun updateUsername(username: String) {
        connectionDetails = connectionDetails.copy(username = username)
    }

    fun updatePassword(password: String) {
        connectionDetails = connectionDetails.copy(password = password)
    }

    fun login(onLoginSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = ""
            try {
                val result = withContext(Dispatchers.IO) {
                    val connection = ConnectionSingleton.getInstance()
                    connection.setConnectionDetails(connectionDetails)
                    connection.connect()
                }
                if (result == "") {
                    sharedPreferences.edit().apply {
                        if (saveDetails) {
                            putString("serverURL", connectionDetails.serverURL)
                            putString("username", connectionDetails.username)
                            putString("password", connectionDetails.password)
                        } else {
                            remove("serverURL")
                            remove("username")
                            remove("password")
                        }
                        putBoolean("rememberLogin", saveDetails)
                        apply()
                    }
                    onLoginSuccess()
                } else {
                    errorMessage = result
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Unknown error"
            } finally {
                isLoading = false
            }
        }
    }

    fun forgetDetails() {
        sharedPreferences.edit().apply {
            remove("serverURL")
            remove("username")
            remove("password")
            remove("rememberLogin")
            apply()
        }
        connectionDetails = SMMConnectionDetails("", "", "")
        saveDetails = false
    }
}
