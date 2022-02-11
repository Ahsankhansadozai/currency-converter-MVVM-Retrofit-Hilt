package com.example.currency_converter_mvvm.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.currency_converter_mvvm.R
import com.example.currency_converter_mvvm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding?=null
    private val binding = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.AppTheme_NoActionBar)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)





    }
}