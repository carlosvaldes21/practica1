package com.carlosvaldes.practica1.network

import com.carlosvaldes.practica1.models.Car
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface CarsApi {


    @GET
    fun getCars(
        @Url url: String?
    ): Call<ArrayList<Car>>

    @GET("cars/{id}")
    fun getCar(
        @Path("id") id:String?
    ): Call<Car>
}