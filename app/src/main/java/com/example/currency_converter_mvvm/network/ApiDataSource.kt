package com.example.currency_converter_mvvm.network

import javax.inject.Inject

class ApiDataSource @Inject constructor(private val apiService: ApiService) {

    suspend fun getConvertedRate(access_key: String, from: String, to: String, amount: Double) =
        apiService.convertCurrency(access_key, from, to, amount)

}