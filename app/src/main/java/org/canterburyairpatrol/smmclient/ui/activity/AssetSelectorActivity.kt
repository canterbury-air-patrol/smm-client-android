package org.canterburyairpatrol.smmclient.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.canterburyairpatrol.smmclient.data.SMMConnectionDetails
import org.canterburyairpatrol.smmclient.smm.SMMConnectionInstance
import org.canterburyairpatrol.smmclient.smm.data.SMMAsset

class AssetSelectorActivity : ComponentActivity() {
    private var connectionDetails = SMMConnectionDetails("", "", "")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
            // A surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Text(connectionDetails.username + "@" + connectionDetails.serverURL)
                this.AssetSelector()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AssetListItem(asset: SMMAsset) {
        ListItem(
            headlineContent = { Text(asset.name) },
            modifier = Modifier.fillMaxWidth()
        )
    }
    @Composable
    fun AssetSelector() {
        var assetList by remember { mutableStateOf(listOf<SMMAsset>()) }

        LaunchedEffect(assetList) {
            val api = (SMMConnectionInstance(connectionDetails).getAPI())
            assetList = (api.getAssetsMine().assets)
        }

        LazyColumn {
            items(assetList) { asset ->
                AssetListItem(asset = asset)
            }
        }
    }
}