package org.canterburyairpatrol.smmclient.smm

import org.canterburyairpatrol.smmclient.smm.data.SMMAssetDetails
import org.canterburyairpatrol.smmclient.smm.data.SMMAssetResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SMMAPI {
    @POST("accounts/login/")
    @FormUrlEncoded
    suspend fun login(
        @Field("login") username: String,
        @Field("password") password: String
    )

    @GET("accounts/login/")
    suspend fun getLoginPage()

    @GET("assets/mine/json/")
    suspend fun getAssetsMine(): SMMAssetResponse

    @GET("assets/{assetName}/details/")
    suspend fun getAssetDetails(@Path(value = "assetName") assetName: String): SMMAssetDetails

    @POST("data/assets/{assetName}/position/add/")
    @FormUrlEncoded
    suspend fun sendAssetPosition(
        @Path(value = "assetName") assetName: String,
        @Field("lat") latitude: Double,
        @Field("lon") longitude: Double,
        @Field("fix") fix: Int,
        @Field("alt") altitude: Int,
        @Field("heading") heading: Int)
}