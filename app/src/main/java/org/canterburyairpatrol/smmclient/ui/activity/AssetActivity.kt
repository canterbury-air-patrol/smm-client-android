package org.canterburyairpatrol.smmclient.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.canterburyairpatrol.smmclient.data.SMMConnectionDetails
import org.canterburyairpatrol.smmclient.smm.data.SMMAsset
import org.canterburyairpatrol.smmclient.ui.theme.SmmclientandroidTheme

class AssetActivity : ComponentActivity() {
    private var connectionDetails = SMMConnectionDetails("", "", "")
    private var asset = SMMAsset(0, "", 0, "", "")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val receivedIntent = intent
        if (receivedIntent != null && receivedIntent.hasExtra("connectionDetails")) {
            val receivedConnectionDetails =
                receivedIntent.getParcelableExtra<SMMConnectionDetails>("connectionDetails")
            connectionDetails = SMMConnectionDetails(
                receivedConnectionDetails?.serverURL ?: "",
                receivedConnectionDetails?.username ?: "",
                receivedConnectionDetails?.password ?: ""
            )
            val receivedAsset = receivedIntent.getParcelableExtra<SMMAsset>("assetDetails")
            asset = SMMAsset(
                receivedAsset?.id ?: 0,
                receivedAsset?.name ?: "",
                receivedAsset?.type_id ?: 0,
                receivedAsset?.type_name ?: "",
                receivedAsset?.owner ?: ""
            )
        }

        setContent {
            SmmclientandroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    this.assetDetails()
                }
            }
        }
    }

    @Composable
    fun assetDetails() {
        var asset = this.asset
        var connectDetails = this.connectionDetails
        Column {
            Text("${connectDetails.username} @ ${connectDetails.serverURL}")
            Text("${asset.name} (${asset.type_name})")
        }
    }
}