package org.canterburyairpatrol.smmclient

import org.canterburyairpatrol.smmclient.data.SMMConnectionDetails
import org.canterburyairpatrol.smmclient.smm.SMMAPI
import org.canterburyairpatrol.smmclient.smm.SMMConnectionInstance

class ConnectionSingleton private constructor() {
    private lateinit var connectionDetails : SMMConnectionDetails
    private lateinit var API : SMMAPI

    fun setConnectionDetails(details: SMMConnectionDetails)
    {
        this.connectionDetails = details
    }

    fun getConnectionDetails() : SMMConnectionDetails
    {
        return this.connectionDetails
    }

    suspend fun connect() : String
    {
        try {
            this.API = (SMMConnectionInstance(this.connectionDetails).getAPI())
            return ""
        } catch (e: Exception) {
            return e.message ?: "Unknown error"
        }
    }
    fun getAPI() : SMMAPI {
        return this.API
    }
    companion object {
        @Volatile
        private var instance: ConnectionSingleton? = null

        fun getInstance(): ConnectionSingleton {
            return instance ?: synchronized(this) {
                instance ?: ConnectionSingleton().also { instance = it }
            }
        }
    }
}