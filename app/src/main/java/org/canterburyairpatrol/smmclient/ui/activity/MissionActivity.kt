package org.canterburyairpatrol.smmclient.ui.activity

import android.os.Bundle
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
import androidx.compose.ui.Modifier
import org.canterburyairpatrol.smmclient.smm.data.SMMMission
import org.canterburyairpatrol.smmclient.ui.theme.SmmclientandroidTheme

class MissionActivity : ComponentActivity() {
    private var mission = SMMMission(0, "", "", "")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        setContent {
            SmmclientandroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        missionView(this@MissionActivity.mission)
                    }
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