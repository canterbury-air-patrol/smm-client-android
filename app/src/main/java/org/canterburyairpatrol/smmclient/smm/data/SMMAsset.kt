package org.canterburyairpatrol.smmclient.smm.data

import android.os.Parcel
import android.os.Parcelable

data class SMMAsset(
    val id: Int,
    val name: String,
    val type_id: Int,
    val type_name: String,
    val owner: String,
) : Parcelable {
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeInt(type_id)
        parcel.writeString(type_name)
        parcel.writeString(owner)
    }

    companion object CREATOR : Parcelable.Creator<SMMAsset> {
        override fun createFromParcel(parcel: Parcel?): SMMAsset {
            return SMMAsset(
                parcel?.readInt() ?: 0,
                parcel?.readString() ?: "",
                parcel?.readInt() ?: 0,
                parcel?.readString() ?: "",
                parcel?.readString() ?: ""
            )
        }

        override fun newArray(size: Int): Array<SMMAsset?> {
            return arrayOfNulls<SMMAsset?>(size)
        }
    }
}

data class SMMAssetResponse(
    val assets: List<SMMAsset>
)