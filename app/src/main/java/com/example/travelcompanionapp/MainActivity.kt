package com.example.travelcompanionapp

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var typeBox: Spinner
    private lateinit var startBox: Spinner
    private lateinit var endBox: Spinner
    private lateinit var numberInput: EditText
    private lateinit var answerText: TextView
    private lateinit var convertBtn: Button
    private val conversionTypes = listOf("Currency", "Fuel Efficiency", "Liquid Volume", "Temperature")
    private val units = mapOf(
        "Currency" to listOf("USD", "AUD", "EUR", "JPY", "GBP"),
        "Fuel Efficiency" to listOf("mpg", "km/L"),
        "Liquid Volume" to listOf("Gallon", "Liter"),
        "Temperature" to listOf("Celsius", "Fahrenheit", "Kelvin")
    )
    private val currencyRate = mapOf(
        "USD" to 1.0,
        "AUD" to 1.55,
        "EUR" to 0.92,
        "JPY" to 148.50,
        "GBP" to 0.78
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        typeBox = findViewById(R.id.typeBox)
        startBox = findViewById(R.id.startBox)
        endBox = findViewById(R.id.endBox)
        numberInput = findViewById(R.id.numberInput)
        answerText = findViewById(R.id.answerText)
        convertBtn = findViewById(R.id.convertBtn)
        //puts the conversion categories into the first spinner.
        typeBox.adapter = makeAdapter(conversionTypes)
        loadUnitOptions("Currency")

        typeBox.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                loadUnitOptions(conversionTypes[position])
                answerText.text = "Result will appear here"
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        convertBtn.setOnClickListener {
            convertNow()
        }
    }

    private fun makeAdapter(items: List<String>): ArrayAdapter<String> {
        return ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
    }

    private fun loadUnitOptions(type: String) {
        val selectedUnits = units[type] ?: emptyList()
        startBox.adapter = makeAdapter(selectedUnits)
        endBox.adapter = makeAdapter(selectedUnits)
    }

    private fun convertNow() {
        val rawValue = numberInput.text.toString().trim()

        if (rawValue.isBlank()) {
            numberInput.error = "Enter a value"
            return
        }

        val amount = rawValue.toDoubleOrNull()

        if (amount == null) {
            numberInput.error = "Only numbers are allowed"
            return
        }

        val selectedType = typeBox.selectedItem.toString()
        val from = startBox.selectedItem.toString()
        val to = endBox.selectedItem.toString()

        if (from == to) {
            answerText.text = "No conversion needed: $amount $to"
            Toast.makeText(this, "Same unit selected", Toast.LENGTH_SHORT).show()
            return
        }

        if (amount < 0 && selectedType != "Temperature") {
            numberInput.error = "Negative values are not valid for this category"
            return
        }

        val finalAnswer = when (selectedType) {
            "Currency" -> moneyConvert(amount, from, to)
            "Fuel Efficiency" -> fuelConvert(amount, from, to)
            "Liquid Volume" -> volumeConvert(amount, from, to)
            "Temperature" -> temperatureConvert(amount, from, to)
            else -> null
        }
        if (finalAnswer == null) {
            answerText.text = "This conversion is not available"
        } else {
            answerText.text = "Converted value: %.2f %s".format(finalAnswer, to)
        }
    }
    private fun moneyConvert(amount: Double, from: String, to: String): Double? {
        val fromRate = currencyRate[from] ?: return null
        val toRate = currencyRate[to] ?: return null

        val amountInUsd = amount / fromRate
        return amountInUsd * toRate
    }
    private fun fuelConvert(amount: Double, from: String, to: String): Double? {
        return if (from == "mpg" && to == "km/L") {
            amount * 0.425
        } else if (from == "km/L" && to == "mpg") {
            amount / 0.425
        } else {
            null
        }
    }
    private fun volumeConvert(amount: Double, from: String, to: String): Double? {
        return if (from == "Gallon" && to == "Liter") {
            amount * 3.785
        } else if (from == "Liter" && to == "Gallon") {
            amount / 3.785
        } else {
            null
        }
    }
    private fun temperatureConvert(amount: Double, from: String, to: String): Double? {
        val celsiusValue = when (from) {
            "Celsius" -> amount
            "Fahrenheit" -> (amount - 32) / 1.8
            "Kelvin" -> amount - 273.15
            else -> return null
        }

        return when (to) {
            "Celsius" -> celsiusValue
            "Fahrenheit" -> (celsiusValue * 1.8) + 32
            "Kelvin" -> celsiusValue + 273.15
            else -> null
        }
    }
}