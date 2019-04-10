package com.darthsanches.currencyconverter

data class RateResponse(val base: String, val date: String, val rates: LinkedHashMap<String,Float>)