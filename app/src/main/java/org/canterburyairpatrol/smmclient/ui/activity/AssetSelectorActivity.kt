package org.canterburyairpatrol.smmclient.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import org.canterburyairpatrol.smmclient.data.SMMConnectionDetails

class AssetSelectorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var connectionDetails = SMMConnectionDetails("", "", "")

        val receivedIntent = intent
        if (receivedIntent != null && receivedIntent.hasExtra("connectionDetails"))
        {
            val receivedConnectionDetails = receivedIntent.getParcelableExtra<SMMConnectionDetails>("connectionDetails")
            connectionDetails = SMMConnectionDetails(
                receivedConnectionDetails?.serverURL ?: "",
                receivedConnectionDetails?.username ?: "",
                receivedConnectionDetails?.password ?: "")
        }

        setContent {
            Text(connectionDetails.username + "@" + connectionDetails.serverURL)
        }
    }
}