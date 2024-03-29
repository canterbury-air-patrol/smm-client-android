package org.canterburyairpatrol.smmclient.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
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
import org.canterburyairpatrol.smmclient.ConnectionSingleton
import org.canterburyairpatrol.smmclient.smm.data.SMMAsset
import org.canterburyairpatrol.smmclient.ui.theme.SmmclientandroidTheme

class AssetSelectorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val connectionSingleton = ConnectionSingleton.getInstance()
        val connectionDetails = connectionSingleton.getConnectionDetails()


        setContent {
            SmmclientandroidTheme {
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
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AssetListItem(asset: SMMAsset, modifier: Modifier) {
        ListItem(
            headlineContent = { Text(asset.name) },
            modifier = modifier.fillMaxWidth()
        )
    }
    @Composable
    fun AssetSelector() {
        var assetList by remember { mutableStateOf(listOf<SMMAsset>()) }

        LaunchedEffect(assetList) {
            val connectionSingleton = ConnectionSingleton.getInstance()
            val api = connectionSingleton.getAPI()
            assetList = (api.getAssetsMine().assets)
        }

        LazyColumn {
            items(assetList) { asset ->
                AssetListItem(asset = asset,
                    modifier = Modifier.clickable {
                        var intent = Intent(this@AssetSelectorActivity, AssetActivity::class.java)
                        intent.putExtra("assetDetails", asset)
                        startActivity(intent)
                    })
            }
        }
    }
}