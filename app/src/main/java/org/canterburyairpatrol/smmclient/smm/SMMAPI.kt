package org.canterburyairpatrol.smmclient.smm

import org.canterburyairpatrol.smmclient.smm.data.SMMAssetResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
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
}