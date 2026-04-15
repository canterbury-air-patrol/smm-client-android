package org.canterburyairpatrol.smmclient

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.preference.PreferenceManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import org.canterburyairpatrol.smmclient.ui.activity.MainSelectorActivity
import org.canterburyairpatrol.smmclient.ui.screen.LoginScreen
import org.canterburyairpatrol.smmclient.ui.screen.LoginViewModel
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
                    val viewModel: LoginViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return LoginViewModel(getEncryptedSharedPreferences()) as T
                            }
                        }
                    )
                    LoginScreen(
                        viewModel = viewModel,
                        onLoginSuccess = {
                            val intent = Intent(this@MainActivity, MainSelectorActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    )
                }
            }
        }
    }

    private fun getEncryptedSharedPreferences(): SharedPreferences {
        val masterKey = MasterKey.Builder(this)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val sharedPreferences = EncryptedSharedPreferences.create(
            this,
            "secret_shared_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val oldPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (oldPrefs.contains("serverURL") || oldPrefs.contains("username") ||
            oldPrefs.contains("password") || oldPrefs.contains("rememberLogin")
        ) {
            sharedPreferences.edit().apply {
                putString("serverURL", oldPrefs.getString("serverURL", ""))
                putString("username", oldPrefs.getString("username", ""))
                putString("password", oldPrefs.getString("password", ""))
                putBoolean("rememberLogin", oldPrefs.getBoolean("rememberLogin", false))
                apply()
            }
            oldPrefs.edit(commit = false) {
                remove("serverURL")
                remove("username")
                remove("password")
                remove("rememberLogin")
            }
        }

        return sharedPreferences
    }
}
