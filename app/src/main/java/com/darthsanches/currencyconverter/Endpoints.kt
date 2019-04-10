package com.darthsanches.currencyconverter

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface Endpoints {

    @GET("latest")
    fun getRates(@Query("base") base:String): Single<RateResponse>
}