package org.canterburyairpatrol.smmclient.smm

import org.canterburyairpatrol.smmclient.smm.data.SMMAssetDetails
import org.canterburyairpatrol.smmclient.smm.data.SMMAssetResponse
import org.canterburyairpatrol.smmclient.smm.data.SMMMissionResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface SMMAPI {
    @POST("accounts/login/")
    @FormUrlEncoded
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<Unit>

    @GET("accounts/login/")
    suspend fun getLoginPage()

    @Headers("Accept: application/json")
    @GET("assets/")
    suspend fun getAssetsMine(): SMMAssetResponse

    @Headers("Accept: application/json")
    @GET("assets/{assetId}/")
    suspend fun getAssetDetails(@Path(value = "assetId") assetId: Int): SMMAssetDetails

    @POST("data/assets/{assetId}/position/add/")
    @FormUrlEncoded
    suspend fun sendAssetPosition(
        @Path(value = "assetId") assetName: Int,
        @Field("lat") latitude: Double,
        @Field("lon") longitude: Double,
        @Field("fix") fix: Int,
        @Field("alt") altitude: Int,
        @Field("heading") heading: Int)

    @GET("mission/list/?only=active")
    suspend fun getMissionsActive(): SMMMissionResponse

    @POST("mission/{missionId}/data/user/{userName}/position/add/")
    @FormUrlEncoded
    suspend fun sendUserMissionPosition(
        @Path(value = "missionId") missionId: Int,
        @Path(value = "userName") username: String,
        @Field("lat") latitude: Double,
        @Field("lon") longitude: Double,
        @Field("fix") fix: Int,
        @Field("alt") altitude: Int,
        @Field("heading") heading: Int)
}