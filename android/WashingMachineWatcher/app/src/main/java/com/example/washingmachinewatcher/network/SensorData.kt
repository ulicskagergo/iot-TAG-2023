package com.example.washingmachinewatcher.network

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

class SensorData {
    @Serializable
    data class SensorModel(
        val results: Array<RawSensorData>
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as SensorModel

            if (!results.contentEquals(other.results)) return false

            return true
        }

        override fun hashCode(): Int {
            return results.contentHashCode()
        }
    }

    @Serializable
    data class RawSensorData(
        val ts: String,
        val data: Int = -1
    )

    private val client = OkHttpClient()

    fun getDataWithQuery(query: String): SensorModel? {
        val requestBody = RequestBody.create(MediaType.parse("application/json"), query)
        val request = Request.Builder()
            .url("https://bmewashingmachine.azureiotcentral.com/api/query?api-version=2022-10-31-preview")
            .addHeader(
                "Authorization",
                "TODO api key"
            )
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()
        return if (response.code() == 200 && response.body() != null) {
            Json.decodeFromString<SensorModel>(response.body()!!.string())
        } else {
            null
        }
    }
}
