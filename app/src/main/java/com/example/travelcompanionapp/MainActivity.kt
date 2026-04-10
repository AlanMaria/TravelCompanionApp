package com.example.travelcompanionapp

import android.icu.util.Currency
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerFrom: Spinner
    private lateinit var spinnerTo: Spinner
    private lateinit var etInput: EditText
    private lateinit var btnConvert: Button
    private lateinit var tvResult: TextView

    private val categories = arrayOf(
        "Currency",
        "Fuel Efficiency",
        "Volume",
        "Distance",
        "Temperature"
    )

    private val currencyUnits = arrayOf("USD", "AUD", "EUR", "JPY", "GBP")
    private val fuelUnits = arrayOf("MPG", "KM/L")
    private val volumeUnits = arrayOf("Gallon", "Liter")
    private val distanceUnits = arrayOf("Kilometer", "Nautical Mile")
    private val temperatureUnits = arrayOf("Celsius", "Fahrenheit", "Kelvin")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinnerCategory = findViewById(R.id.spinnerCategory)
        spinnerFrom = findViewById(R.id.spinnerFrom)
        spinnerTo = findViewById(R.id.spinnerTo)
        etInput = findViewById(R.id.etInput)
        btnConvert = findViewById(R.id.btnConvert)
        tvResult = findViewById(R.id.tvResult)

        val categoryAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )
        spinnerCategory.adapter = categoryAdapter

        updateUnitSpinners("Currency")

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategory = categories[position]
                updateUnitSpinners(selectedCategory)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        btnConvert.setOnClickListener {
            handleConversion()
        }
    }

    private fun updateUnitSpinners(category: String) {
        val units = when (category) {
            "Currency" -> currencyUnits
            "Fuel Efficiency" -> fuelUnits
            "Volume" -> volumeUnits
            "Distance" -> distanceUnits
            "Temperature" -> temperatureUnits
            else -> arrayOf()
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, units)
        spinnerFrom.adapter = adapter
        spinnerTo.adapter = adapter
    }

    private fun handleConversion() {
        val inputText = etInput.text.toString().trim()

        if (inputText.isEmpty()) {
            etInput.error = "Please enter a value"
            return
        }

        val inputValue = inputText.toDoubleOrNull()
        if (inputValue == null) {
            etInput.error = "Please enter a valid number"
            return
        }

        val category = spinnerCategory.selectedItem.toString()
        val fromUnit = spinnerFrom.selectedItem.toString()
        val toUnit = spinnerTo.selectedItem.toString()

        if (fromUnit == toUnit) {
            tvResult.text = "Result: $inputValue $toUnit"
            Toast.makeText(this, "Same  conversion selected", Toast.LENGTH_SHORT).show()
            return
        }

        if ((category == "Fuel Efficiency" || category == "Volume" || category == "Distance" || category == "Currency") && inputValue < 0) {
            etInput.error = "Negative values are not allowed for this conversion"
            return
        }

        val result = when (category) {
            "Currency" -> convertCurrency(inputValue, fromUnit, toUnit)
            "Fuel Efficiency" -> convertFuelEfficiency(inputValue, fromUnit, toUnit)
            "Volume" -> convertVolume(inputValue, fromUnit, toUnit)
            "Distance" -> convertDistance(inputValue, fromUnit, toUnit)
            "Temperature" -> convertTemperature(inputValue, fromUnit, toUnit)
            else -> null
        }

        if (result != null) {
            tvResult.text = "Result: %.2f %s".format(result, toUnit)
        } else {
            tvResult.text = "Conversion not supported"
        }
    }

    private fun convertCurrency(value: Double, from: String, to: String): Double? {
        val usdValue = when (from) {
            "USD" -> value
            "AUD" -> value / 1.55
            "EUR" -> value / 0.92
            "JPY" -> value / 148.50
            "GBP" -> value / 0.78
            else -> return null
        }

        return when (to) {
            "USD" -> usdValue
            "AUD" -> usdValue * 1.55
            "EUR" -> usdValue * 0.92
            "JPY" -> usdValue * 148.50
            "GBP" -> usdValue * 0.78
            else -> null
        }
    }

    private fun convertFuelEfficiency(value: Double, from: String, to: String): Double? {
        return when {
            from == "MPG" && to == "KM/L" -> value * 0.425
            from == "KM/L" && to == "MPG" -> value / 0.425
            else -> null
        }
    }

    private fun convertVolume(value: Double, from: String, to: String): Double? {
        return when {
            from == "Gallon" && to == "Liter" -> value * 3.785
            from == "Liter" && to == "Gallon" -> value / 3.785
            else -> null
        }
    }

    private fun convertDistance(value: Double, from: String, to: String): Double? {
        return when {
            from == "Nautical Mile" && to == "Kilometer" -> value * 1.852
            from == "Kilometer" && to == "Nautical Mile" -> value / 1.852
            else -> null
        }
    }

    private fun convertTemperature(value: Double, from: String, to: String): Double? {
        return when {
            from == "Celsius" && to == "Fahrenheit" -> (value * 1.8) + 32
            from == "Fahrenheit" && to == "Celsius" -> (value - 32) / 1.8
            from == "Celsius" && to == "Kelvin" -> value + 273.15
            from == "Kelvin" && to == "Celsius" -> value - 273.15
            from == "Fahrenheit" && to == "Kelvin" -> ((value - 32) / 1.8) + 273.15
            from == "Kelvin" && to == "Fahrenheit" -> ((value - 273.15) * 1.8) + 32
            else -> null
        }
    }
}