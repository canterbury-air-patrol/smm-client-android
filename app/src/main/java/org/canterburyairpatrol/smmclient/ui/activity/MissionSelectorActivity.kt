package org.canterburyairpatrol.smmclient.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.canterburyairpatrol.smmclient.ConnectionSingleton
import org.canterburyairpatrol.smmclient.smm.data.SMMMission
import org.canterburyairpatrol.smmclient.ui.theme.SmmclientandroidTheme

class MissionSelectorActivity : ComponentActivity() {
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
                    Column {
                        Row {
                            Text(connectionDetails.username + "@" + connectionDetails.serverURL)
                        }
                        Row {
                            Button(onClick = {
                                startActivity(Intent(this@MissionSelectorActivity, AssetSelectorActivity::class.java))
                            }) {
                                Text("Assets")
                            }
                        }
                        Row (Modifier.weight(1f).align(Alignment.Start)) {
                            this@MissionSelectorActivity.MissionSelector()
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MissionListItem(mission: SMMMission, modifier: Modifier) {
        ListItem(
            headlineContent = { Text("" + mission.id + " " + mission.name) },
            modifier = modifier.fillMaxWidth()
        )
    }
    @Composable
    fun MissionSelector() {
        var missionList by remember { mutableStateOf(listOf<SMMMission>()) }

        LaunchedEffect(missionList) {
            val connectionSingleton = ConnectionSingleton.getInstance()
            val api = connectionSingleton.getAPI()
            missionList = (api.getMissionsActive().missions)
        }

        LazyColumn {
            items(missionList) { mission ->
                MissionListItem(mission = mission,
                    modifier = Modifier.clickable {
                        var intent = Intent(this@MissionSelectorActivity, MissionActivity::class.java)
                        intent.putExtra("missionDetails", mission)
                        startActivity(intent)
                    })
            }
        }
    }
}