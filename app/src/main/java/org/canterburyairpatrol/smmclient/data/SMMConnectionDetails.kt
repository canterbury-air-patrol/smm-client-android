package org.canterburyairpatrol.smmclient.data

import android.os.Parcel
import android.os.Parcelable

data class SMMConnectionDetails (
    val serverURL: String,
    val username: String,
    val password: String,
) : Parcelable {
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(serverURL)
        parcel.writeString(username)
        parcel.writeString(password)
    }

    companion object CREATOR : Parcelable.Creator<SMMConnectionDetails> {
        override fun createFromParcel(parcel: Parcel?): SMMConnectionDetails {
            return SMMConnectionDetails(
                parcel?.readString() ?: "",
                parcel?.readString() ?: "",
                parcel?.readString() ?: ""
            )
        }

        override fun newArray(size: Int): Array<SMMConnectionDetails?> {
            return arrayOfNulls<SMMConnectionDetails?>(size)
        }
    }
}
