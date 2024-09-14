package com.finance.trade_learn.models

import android.os.Parcel
import android.os.Parcelable
import com.finance.trade_learn.models.coin_gecko.CoinDetail
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1

data class WrapResponse<T>(
    @SerializedName("isSuccess") var success: Boolean? = null,
    @SerializedName("data") val data: T? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("error") var error:ErrorResponse? = null,
    //@SerializedName("info") var info: String? = null,
    @SerializedName("dateTime") var dateTime: String? = null
)

data class ErrorResponse(var code: Int?, var name: String?, var message: String?) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(code)
        parcel.writeString(name)
        parcel.writeString(message)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ErrorResponse> {
        override fun createFromParcel(parcel: Parcel): ErrorResponse {
            return ErrorResponse(parcel)
        }

        override fun newArray(size: Int): Array<ErrorResponse?> {
            return arrayOfNulls(size)
        }
    }
}

fun handleResponse(data: List<CoinDetail>?): Response<WrapResponse<List<CoinDetail>?>> {
    val response: WrapResponse<List<CoinDetail>?> = WrapResponse(
        success = true,
        data = data,
        message = null,
        error = ErrorResponse(null, null, null),
        dateTime = System.currentTimeMillis().toString()
    )

    return Response.success(response)
}
