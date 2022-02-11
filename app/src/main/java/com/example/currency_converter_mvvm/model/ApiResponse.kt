package com.example.currency_converter_mvvm.model

data class ApiResponse(
    val amount: String,
    val base_currency_code: String,
    val base_currency_name: String,
    var rates: HashMap<String, Rates> = HashMap(),
    val status: String,
    val updated_date: String
)