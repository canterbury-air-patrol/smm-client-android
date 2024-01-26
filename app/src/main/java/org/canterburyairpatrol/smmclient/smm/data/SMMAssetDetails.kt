package org.canterburyairpatrol.smmclient.smm.data

data class SMMAssetDetails(
    val asset_id: Int,
    val name: String,
    val owner: String,
    val last_command: SMMAssetCommand,
    val mission_id: Int,
    val mission_name: String,
    val current_search_id: Int,
    val queued_search_id: Int)