package com.example.currency_converter_mvvm.view

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.util.Currency
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.currency_converter_mvvm.R
import com.example.currency_converter_mvvm.databinding.ActivityMainBinding
import com.example.currency_converter_mvvm.helper.Resource
import com.example.currency_converter_mvvm.helper.utility.EndPoints
import com.example.currency_converter_mvvm.helper.utility.Utility
import com.example.currency_converter_mvvm.model.Rates
import com.example.currency_converter_mvvm.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    //Declare all variables
    //Selected country string, default is Afghanistan, since its the first country listed in the spinner
    private var selectedItem1: String? = "AFN"
    private var selectedItem2: String? = "AFN"

    //ViewModel
    private val mainViewModel: MainViewModel by viewModels()


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState)

        //Make status bar transparent
        Utility.makeStatusBarTransparent(this)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Initialize both Spinner
        hIniSpinners()

        //Listen to click events
        hSetUpClickListener()


    }


    /**
     * This method does everything required for handling spinner (Dropdown list) - showing list of countries, handling click events on items selected.*
     */

    @RequiresApi(Build.VERSION_CODES.N)
    private fun hIniSpinners() {

        //get first spinner country reference in view
        val spinner1 = binding.spnFirstCountry

        //set items in the spinner i.e a list of all countries
        spinner1.setItems(hGetAllCountries())

        //hide key board when spinner shows (For some weird reasons, this isn't so effective as I am using a custom Material Spinner)
        spinner1.setOnClickListener {
            Utility.hideKeyboard(this)
        }

        //Handle selected item, by getting the item and storing the value in a  variable - selectedItem1
        spinner1.setOnItemSelectedListener { _, position, id, item ->
            //Set the currency code for each country as hint
            val countryCode = hGetCountryCode(item.toString())
            val currencySymbol = hGetSymbol(countryCode)
            selectedItem1 = currencySymbol
            binding.txtFirstCurrencyName.text = selectedItem1
        }


        //get second spinner country reference in view
        val spinner2 = binding.spnSecondCountry

        //hide key board when spinner shows
        spinner1.setOnClickListener {
            Utility.hideKeyboard(this)
        }

        //set items on second spinner i.e - a list of all countries
        spinner2.setItems(hGetAllCountries())


        //Handle selected item, by getting the item and storing the value in a  variable - selectedItem2,
        spinner2.setOnItemSelectedListener { view, position, id, item ->
            //Set the currency code for each country as hint
            val countryCode = hGetCountryCode(item.toString())
            val currencySymbol = hGetSymbol(countryCode)
            selectedItem2 = currencySymbol
            binding.txtSecondCurrencyName.setText(selectedItem2)
        }

    }


    /**
     * A method for getting a country's currency symbol from the country's code
     * e.g USA - USD
     */

    @RequiresApi(Build.VERSION_CODES.N)
    private fun hGetSymbol(countryCode: String?): String? {
        val availableLocales = Locale.getAvailableLocales()
        for (i in availableLocales.indices) {
            if (availableLocales[i].country == countryCode
            ) return Currency.getInstance(availableLocales[i]).currencyCode
        }
        return ""
    }


    /**
     * A method for getting a country's code from the country name
     * e.g Nigeria - NG
     */

    private fun hGetCountryCode(countryName: String) =
        Locale.getISOCountries().find { Locale("", it).displayCountry == countryName }


    /**
     * A method for getting all countries in the world - about 256 or so
     */

    private fun hGetAllCountries(): ArrayList<String> {

        val locales = Locale.getAvailableLocales()
        val countries = ArrayList<String>()
        for (locale in locales) {
            val country = locale.displayCountry
            if (country.trim { it <= ' ' }.isNotEmpty() && !countries.contains(country)) {
                countries.add(country)
            }
        }
        countries.sort()

        return countries
    }

    /**
     * A method for handling click events in the UI
     */

    private fun hSetUpClickListener() {

        //Convert button clicked - check for empty string and internet then do the conersion
        binding.btnConvert.setOnClickListener {

            //check if the input is empty
            val numberToConvert = binding.etFirstCurrency.text.toString()

            if (numberToConvert.isEmpty() || numberToConvert == "0") {
                Snackbar.make(
                    binding.mainLayout,
                    "Input a value in the first text field, result will be shown in the second text field",
                    Snackbar.LENGTH_LONG
                )
                    .withColor(ContextCompat.getColor(this, R.color.dark_red))
                    .setTextColor(ContextCompat.getColor(this, R.color.white))
                    .show()
            }

            //check if internet is available
            else if (!Utility.isNetworkAvailable(this)) {
                Snackbar.make(
                    binding.mainLayout,
                    "You are not connected to the internet",
                    Snackbar.LENGTH_LONG
                )
                    .withColor(ContextCompat.getColor(this, R.color.dark_red))
                    .setTextColor(ContextCompat.getColor(this, R.color.white))
                    .show()
            }

            //carry on and convert the value
            else {
                hDoConversion()
            }
        }


        //handle clicks of other views
        binding.txtContact.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            val data: Uri = Uri.parse("mailto:ahsan.bsse3200@iiu.edu.pk?subject=Hello")
            intent.data = data
            startActivity(intent)
        }

    }

    /**
     * A method that does the conversion by communicating with the API - fixer.io based on the data inputed
     * Uses viewModel and flows
     */

    private fun hDoConversion() {

        //hide keyboard
        Utility.hideKeyboard(this)

        //make progress bar visible
        binding.prgLoading.visibility = View.VISIBLE

        //make button invisible
        binding.btnConvert.visibility = View.GONE

        //Get the data inputted
        val apiKey = EndPoints.API_KEY
        val from = selectedItem1.toString()
        val to = selectedItem2.toString()
        val amount = binding.etFirstCurrency.text.toString().toDouble()

        //do the conversion
        mainViewModel.getConvertedData(apiKey, from, to, amount)

        //observe for changes in UI
        observeUi()

    }

    /**
     * Using coroutines flow, changes are observed and responses gotten from the API
     *
     */

    @SuppressLint("SetTextI18n")
    private fun observeUi() {
        mainViewModel.data.observe(this) { result ->

            when (result.status) {
                Resource.Status.SUCCESS -> {
                    if (result.data?.status == "success") {

                        val map: Map<String, Rates>

                        map = result.data.rates


                        map.keys.forEach {
                            val rateForAmount = map[it]?.rate_for_amount

                            mainViewModel.convertedRate.value = rateForAmount

                            //format the result obtained e.g 1000 = 1,000
                            val formattedString =
                                String.format("%,.2f", mainViewModel.convertedRate.value)

                            //set the value in the second edit text field
                            binding.etSecondCurrency.setText(formattedString)

                        }


                        //stop progress bar
                        binding.prgLoading.visibility = View.GONE
                        //show button
                        binding.btnConvert.visibility = View.VISIBLE
                    } else if (result.data?.status == "fail") {
                        val layout = binding.mainLayout
                        Snackbar.make(
                            layout,
                            "Ooops! something went wrong, Try again",
                            Snackbar.LENGTH_LONG
                        )
                            .withColor(ContextCompat.getColor(this, R.color.dark_red))
                            .setTextColor(ContextCompat.getColor(this, R.color.white))
                            .show()

                        //stop progress bar
                        binding.prgLoading.visibility = View.GONE
                        //show button
                        binding.btnConvert.visibility = View.VISIBLE
                    }
                }
                Resource.Status.ERROR -> {

                    val layout = binding.mainLayout
                    Snackbar.make(
                        layout,
                        "Oopps! Something went wrong, Try again",
                        Snackbar.LENGTH_LONG
                    )
                        .withColor(ContextCompat.getColor(this, R.color.dark_red))
                        .setTextColor(ContextCompat.getColor(this, R.color.white))
                        .show()
                    //stop progress bar
                    binding.prgLoading.visibility = View.GONE
                    //show button
                    binding.btnConvert.visibility = View.VISIBLE
                }

                Resource.Status.LOADING -> {
                    //stop progress bar
                    binding.prgLoading.visibility = View.VISIBLE
                    //show button
                    binding.btnConvert.visibility = View.GONE
                }
            }
        }
    }

    /**
     * Method for changing the background color of snackBars
     */

    private fun Snackbar.withColor(@ColorInt colorInt: Int): Snackbar {
        this.view.setBackgroundColor(colorInt)
        return this
    }

}