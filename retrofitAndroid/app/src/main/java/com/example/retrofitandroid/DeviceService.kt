package com.example.retrofitandroid

import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path
import okhttp3.ResponseBody

interface DeviceService {
    @GET(Constants.OBJECTS_PATH)
    suspend fun getAllDevices(): List<Device>

    @POST(Constants.OBJECTS_PATH)
    suspend fun addDevice(@Body device: DeviceRequest): Device

    @DELETE("${Constants.OBJECTS_PATH}/{id}")
    suspend fun deleteDevice(@Path("id") id: String): ResponseBody
}
