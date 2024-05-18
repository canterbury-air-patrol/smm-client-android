package org.canterburyairpatrol.smmclient.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.canterburyairpatrol.smmclient.ConnectionSingleton
import org.canterburyairpatrol.smmclient.smm.data.SMMMission
import org.canterburyairpatrol.smmclient.ui.theme.SmmclientandroidTheme

class MissionActivity : ComponentActivity() {
    private var mission = SMMMission(0, "", "", "")
    private lateinit var handler: Handler
    private lateinit var locationRunnable: Runnable
    private var userName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val connectionSingleton = ConnectionSingleton.getInstance()
        val connectionDetails = connectionSingleton.getConnectionDetails()
        userName = connectionDetails.username

        val receivedIntent = intent
        if (receivedIntent != null && receivedIntent.hasExtra("missionDetails")) {
            val receivedMission = receivedIntent.getParcelableExtra<SMMMission>("missionDetails")
            mission = SMMMission(
                receivedMission?.id ?: 0,
                receivedMission?.name ?: "",
                receivedMission?.description ?: "",
                receivedMission?.creator ?: ""
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
                        missionView(this@MissionActivity.mission)
                        positionTracker()
                    }
                }
            }
        }
    }

    @Composable
    fun positionTracker() {
        var currentLat by remember { mutableStateOf("unknown") }
        var currentLon by remember { mutableStateOf("unknown") }
        var tracking by remember { mutableStateOf(false) }
        Column() {
            if (tracking) {
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
            } else {
                Button({
                    if (ActivityCompat.checkSelfPermission(
                            this@MissionActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this@MissionActivity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            1
                        )
                        ActivityCompat.requestPermissions(
                            this@MissionActivity,
                            arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                            2
                        )
                    }
                    if (ActivityCompat.checkSelfPermission(
                            this@MissionActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        locationRunnable = object : Runnable {
                            override fun run() {
                                if (ActivityCompat.checkSelfPermission(
                                        this@MissionActivity,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                        this@MissionActivity,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    ) != PackageManager.PERMISSION_GRANTED
                                ) {
                                    return
                                }
                                var fusedLocationClient =
                                    LocationServices.getFusedLocationProviderClient(this@MissionActivity)
                                fusedLocationClient.lastLocation.addOnCompleteListener(this@MissionActivity,
                                    OnCompleteListener { task: Task<Location?> ->
                                        if (task.isSuccessful && task.result != null) {
                                            val location: Location = task.result!!
                                            currentLat = toDMm(location.latitude, true)
                                            currentLon = toDMm(location.longitude, false)
                                            val connectionSingleton =
                                                ConnectionSingleton.getInstance()
                                            val api = connectionSingleton.getAPI()
                                            GlobalScope.launch {
                                                api.sendUserMissionPosition(
                                                    mission.id,
                                                    this@MissionActivity.userName,
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
fun missionView(mission: SMMMission) {
    Column(
        Modifier.fillMaxWidth()
    ) {
        Row() {
            Text("Mission: ${mission.name}")
        }
        Row() {
            Text("Description: ${mission.description}")
        }
    }
}