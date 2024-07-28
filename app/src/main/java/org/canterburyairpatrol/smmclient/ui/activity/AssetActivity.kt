package org.canterburyairpatrol.smmclient.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
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
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.canterburyairpatrol.smmclient.ConnectionSingleton
import org.canterburyairpatrol.smmclient.smm.data.SMMAsset
import org.canterburyairpatrol.smmclient.smm.data.SMMAssetCommand
import org.canterburyairpatrol.smmclient.smm.data.SMMAssetDetails
import org.canterburyairpatrol.smmclient.ui.theme.SmmclientandroidTheme
import java.math.RoundingMode
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.TimeZone

fun toDMm(v: Double, lat: Boolean) : String
{
    val negative = v < 0
    var point = v
    if (negative) {
        point = point * -1.0
    }
    val degrees = point.toInt()
    val minutes = ((point - degrees) * 60.0).toBigDecimal().setScale(4, RoundingMode.HALF_DOWN).toDouble()
    var direction : String

    if (lat)
    {
        if (negative)
        {
            direction = "S"
        }
        else
        {
            direction = "N"
        }
    }
    else
    {
        if (negative)
        {
            direction = "W"
        }
        else
        {
            direction = "E"
        }
    }

    return "$degrees\u00B0 $minutes $direction"
}

class AssetActivity : ComponentActivity() {
    private var asset = SMMAsset(0, "", 0, "", "")
    private lateinit var handler: Handler
    private lateinit var locationRunnable: Runnable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val receivedIntent = intent
        if (receivedIntent != null && receivedIntent.hasExtra("bundle")) {
            val bundle = receivedIntent.getBundleExtra("bundle")
            val receivedAsset = bundle?.getParcelable<SMMAsset>("assetDetails")
            asset = SMMAsset(
                receivedAsset?.id ?: 0,
                receivedAsset?.name ?: "",
                receivedAsset?.type_id ?: 0,
                receivedAsset?.type_name ?: "",
                receivedAsset?.owner ?: ""
            )
        }
        handler = Handler()

        setContent {
            SmmclientandroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        assetView(this@AssetActivity.asset)
                        positionTracker()
                    }
                }
            }
        }
    }

    @Composable
    fun positionTracker()
    {
        var currentLat by remember { mutableStateOf("unknown") }
        var currentLon by remember { mutableStateOf("unknown") }
        var tracking by remember { mutableStateOf(false) }
        Column () {
            if (tracking)
            {
                Button(onClick = {
                    handler.removeCallbacks(locationRunnable)
                    tracking = false
                }) {
                    Text("Stop")
                }
                Row() {
                    Text("Lon: $currentLon, ")
                    Text("Lat: $currentLat")
                }
            }
            else
            {
                Button ({
                    if (ActivityCompat.checkSelfPermission(
                            this@AssetActivity,
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this@AssetActivity,
                            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                            1
                        )
                        ActivityCompat.requestPermissions(
                            this@AssetActivity,
                            arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                            2
                        )
                    }
                    if (ActivityCompat.checkSelfPermission(
                            this@AssetActivity,
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    )
                    {
                        locationRunnable = object : Runnable {
                            override fun run() {
                                if (ActivityCompat.checkSelfPermission(
                                        this@AssetActivity,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                        this@AssetActivity,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    ) != PackageManager.PERMISSION_GRANTED
                                ) {
                                    return
                                }
                                var fusedLocationClient =
                                    LocationServices.getFusedLocationProviderClient(this@AssetActivity)
                                fusedLocationClient.lastLocation.addOnCompleteListener(this@AssetActivity,
                                    OnCompleteListener { task: Task<Location?> ->
                                        if (task.isSuccessful && task.result != null) {
                                            val location: Location = task.result!!
                                            currentLat = toDMm(location.latitude, true)
                                            currentLon = toDMm(location.longitude, false)
                                            val connectionSingleton = ConnectionSingleton.getInstance()
                                            val api = connectionSingleton.getAPI()
                                            GlobalScope.launch {
                                                api.sendAssetPosition(
                                                    asset.id,
                                                    location.latitude,
                                                    location.longitude,
                                                    (if (location.hasAltitude()) 3 else 2),
                                                    location.altitude.toInt(),
                                                    location.bearing.toInt()
                                                )
                                            }
                                        }
                                    })
                                // Run again in 1s
                                handler.postDelayed(this, 1000L)
                                tracking = true
                            }
                        }

                    handler.post(locationRunnable)
                    }
                })
                {
                    Text("Start Tracking")
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
        assetDetails = (api.getAssetDetails(asset.id))
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