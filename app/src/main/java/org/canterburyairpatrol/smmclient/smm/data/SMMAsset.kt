package org.canterburyairpatrol.smmclient.smm.data

data class SMMAsset(
    val id: Int,
    val name: String,
    val type_id: Int,
    val type_name: String,
    val owner: String,
)

data class SMMAssetResponse(
    val assets: List<SMMAsset>
)