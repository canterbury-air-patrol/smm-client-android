package org.canterburyairpatrol.smmclient.smm.data

data class SMMAssetCommand(
    val action: String,
    val action_txt: String,
    val reason: String,
    val issued: String,
    val latitude: Double,
    val longitude: Double,
)