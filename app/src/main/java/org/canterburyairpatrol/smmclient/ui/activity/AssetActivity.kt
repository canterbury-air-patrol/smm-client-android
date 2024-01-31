package org.canterburyairpatrol.smmclient.ui.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.canterburyairpatrol.smmclient.ConnectionSingleton
import org.canterburyairpatrol.smmclient.smm.data.SMMAsset
import org.canterburyairpatrol.smmclient.smm.data.SMMAssetCommand
import org.canterburyairpatrol.smmclient.smm.data.SMMAssetDetails
import org.canterburyairpatrol.smmclient.ui.theme.SmmclientandroidTheme
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.TimeZone

class AssetActivity : ComponentActivity() {
    private var asset = SMMAsset(0, "", 0, "", "")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val receivedIntent = intent
        if (receivedIntent != null && receivedIntent.hasExtra("assetDetails")) {
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
                    assetView(this.asset)
                }
            }
        }
    }
}

@Composable
fun assetView(asset: SMMAsset) {

    val context = LocalContext.current as ComponentActivity
    var assetDetails by remember { mutableStateOf(SMMAssetDetails(0, "", "", SMMAssetCommand("", "", "", "", 0.0, 0.0), 0, "", 0, 0)) }

    suspend fun updateAssetDetails() {
        val connectionSingleton = ConnectionSingleton.getInstance()
        val api = connectionSingleton.getAPI()
        assetDetails = (api.getAssetDetails(asset.name))
    }

    LaunchedEffect(assetDetails) {
        updateAssetDetails()
    }

    DisposableEffect(Unit) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY)
            {
                onDispose { }
            }
        }
        val handler = Handler(Looper.getMainLooper())

        val updateIntervalMillis = 10000L // 10s

        val runnable = object : Runnable {
            override fun run() {
                GlobalScope.launch {
                    withContext(Dispatchers.Main) {
                        updateAssetDetails()
                    }
                }
                handler.postDelayed(this, updateIntervalMillis)
            }
        }

        context.lifecycle.addObserver(lifecycleObserver)
        handler.postDelayed(runnable, updateIntervalMillis)

        onDispose {
            context.lifecycle.removeObserver(lifecycleObserver)
            handler.removeCallbacks(runnable)
        }
    }

    Column(
        Modifier.fillMaxWidth()
    ) {
        Row() {
            assetDetails(asset)
        }
        Row() {
            assetMissionDetails(assetDetails)
        }
        Row() {
            assetSearchDetails(assetDetails)
        }
        Row() {
            assetInstructions(assetDetails.last_command)
        }
    }
}

@Composable
fun assetDetails(asset: SMMAsset) {
    val connectionSingleton = ConnectionSingleton.getInstance()
    val connectionDetails = connectionSingleton.getConnectionDetails()
    Column {
        Text("${connectionDetails.username} @ ${connectionDetails.serverURL}")
        Text("${asset.name} (${asset.type_name})")
    }
}

@Composable
fun assetMissionDetails(assetDetails: SMMAssetDetails)
{
    Column {
        Text("Current Mission: ${assetDetails.mission_name} (${assetDetails.mission_id})")
    }
}

@Composable
fun assetSearchDetails(assetDetails: SMMAssetDetails)
{
    Column {
        Text("Current Search ${assetDetails.current_search_id}")
        Text("Queued Search ${assetDetails.queued_search_id}")
    }
}

@Composable
fun assetInstructions(command: SMMAssetCommand)
{
    var timestamp: String = ""
    try {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val date = formatter.parse(command.issued)
        val newFormatter = SimpleDateFormat("HH:mm dd-MM-yyyy")
        newFormatter.timeZone = TimeZone.getDefault()
        timestamp = newFormatter.format(date)
    } catch (e: ParseException) {
    }
    Column {
        Text("Issued: ${timestamp}")
        Text("Instruction Type: ${command.action_txt}")
        Text("Reason: ${command.reason}")
        if (command.action == "GOTO")
        {
            Text("Latitude: ${command.latitude}")
            Text("Longitude: ${command.longitude}")
        }
    }
}