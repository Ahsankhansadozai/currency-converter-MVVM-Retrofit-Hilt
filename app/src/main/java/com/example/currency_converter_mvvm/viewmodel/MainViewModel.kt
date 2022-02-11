package com.example.currency_converter_mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currency_converter_mvvm.helper.Resource
import com.example.currency_converter_mvvm.helper.SingleLiveEvent
import com.example.currency_converter_mvvm.model.ApiResponse
import com.example.currency_converter_mvvm.model.Rates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val mainRepo: MainRepo) : ViewModel() {
    //cached
    private val _data = SingleLiveEvent<Resource<ApiResponse>>()
    private val rates = MutableLiveData<HashMap<String, Rates>>()

    //public
    val data = _data
    val convertedRate = MutableLiveData<Double>()

    //Public function to get the result of conversion

    fun getConvertedData(access_key: String, from: String, to: String, amount: Double) {
        viewModelScope.launch {
            mainRepo.getConvertedData(access_key = access_key, from, to, amount).collect {
                data.value = it
            }
        }
    }


}