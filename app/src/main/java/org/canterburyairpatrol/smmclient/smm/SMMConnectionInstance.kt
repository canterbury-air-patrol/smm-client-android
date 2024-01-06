package org.canterburyairpatrol.smmclient.smm

import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import org.canterburyairpatrol.smmclient.data.SMMConnectionDetails
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.CookieStore
import java.net.HttpCookie

fun getCookieByName(store: CookieStore, name: String): HttpCookie? {
    for (cookie in store.cookies) {
        if (cookie.name.equals(name))
        {
            return cookie
        }
    }
    return null;
}
class SMMConnectionInstance {
    private val api: SMMAPI
    private val connectionDetails : SMMConnectionDetails
    private var loggedIn : Boolean = false
    constructor(connectionDetails: SMMConnectionDetails)
    {
        this.connectionDetails = connectionDetails
        val cookieManager = CookieManager()
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        val okHttpClient = OkHttpClient.Builder()
            .cookieJar(JavaNetCookieJar(cookieManager))
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("X-CSRFToken", getCookieByName(cookieManager.cookieStore, "csrftoken")?.value ?: "")
                    .build()
                chain.proceed(request)
            }
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(connectionDetails.serverURL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        this.api = retrofit.create(SMMAPI::class.java)
    }

    suspend fun getAPI() : SMMAPI
    {
        if (!this.loggedIn) {
            this.api.getLoginPage()
            this.api.login(this.connectionDetails.username, this.connectionDetails.password)
            this.loggedIn = true
        }
        return this.api
    }
}