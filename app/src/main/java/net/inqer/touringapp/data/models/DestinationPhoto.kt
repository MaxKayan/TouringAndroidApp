package net.inqer.touringapp.data.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class DestinationPhoto(
    @PrimaryKey
    @SerializedName("pk")
    val id: Long,
    @SerializedName("destination")
    val destinationId: Int,

    @SerializedName("image")
    val url: String,

//    val created_at: String,
//    val updated_at: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readInt(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeInt(destinationId)
        parcel.writeString(url)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DestinationPhoto> {
        override fun createFromParcel(parcel: Parcel): DestinationPhoto {
            return DestinationPhoto(parcel)
        }

        override fun newArray(size: Int): Array<DestinationPhoto?> {
            return arrayOfNulls(size)
        }
    }
}