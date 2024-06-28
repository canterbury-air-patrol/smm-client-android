package org.canterburyairpatrol.smmclient.smm.data

import android.os.Parcel
import android.os.Parcelable

data class SMMMission(
    val id: Int,
    val name: String,
    val description: String,
    val creator: String,
    ) : Parcelable {
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(creator)
    }

    companion object CREATOR : Parcelable.Creator<SMMMission> {
        override fun createFromParcel(parcel: Parcel?): SMMMission {
            return SMMMission(
                parcel?.readInt() ?: 0,
                parcel?.readString() ?: "",
                parcel?.readString() ?: "",
                parcel?.readString() ?: "")
        }

        override fun newArray(size: Int): Array<SMMMission?> {
            return arrayOfNulls<SMMMission?>(size)
        }
    }
}

data class SMMMissionResponse(
    val missions: List<SMMMission>
)