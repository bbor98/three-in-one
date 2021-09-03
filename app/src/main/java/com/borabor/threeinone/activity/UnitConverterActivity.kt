package com.borabor.threeinone.activity

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.borabor.threeinone.R
import com.borabor.threeinone.util.ButtonUtil.addNumberValueToText
import com.borabor.threeinone.util.ButtonUtil.vibratePhone
import com.borabor.threeinone.util.CalculationUtil
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_unit_converter.*
import kotlinx.android.synthetic.main.fragment_scientific_calculator.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class UnitConverterActivity : AppCompatActivity() {

    enum class Unit {
        Other, Currency, Temperature
    }

    private var unitId = Unit.Other

    private val angleValues = doubleArrayOf(1.0, 57.295779513, 0.9, 0.0002777778, 0.0166666667)

    private val areaValues = doubleArrayOf(
        4046.8564224,
        100.0,
        10000.0,
        0.09290304,
        0.000001,
        0.0001,
        0.01,
        1.0,
        10000.0,
        1000000.0,
        2589988.1103,
        0.83612736,
        0.00064516
    )

    private val currencyValues = doubleArrayOf()

    private val dataValues = doubleArrayOf(
        0.125,
        131072.0,
        1.0,
        1024.0,
        1048576.0,
        1073741824.0,
        1099511627776.0,
        1125899906842580.0
    )

    private val energyValues = doubleArrayOf(1.0, 1000.0, 4.184, 4184.0)

    private val forceValues = doubleArrayOf(1.0, 1000.0, 1000000.0, 0.00980665)

    private val lengthValues = doubleArrayOf(
        0.000000001,
        0.000001,
        0.001,
        0.01,
        0.1,
        1.0,
        1000.0,
        1609.344,
        1852.0,
        0.3048,
        0.9144,
        0.0254,
        9460730472580044.0
    )

    private val powerValues = doubleArrayOf(
        1.0,
        1000.0,
        1000000.0,
        1000000000.0,
        1000000000000.0,
        1000000000000000.0,
        745.69987158,
        3516.8528421,
        0.293
    )

    private val pressureValues = doubleArrayOf(
        100.0,
        100000.0,
        6894.7572932,
        6894757.2932,
        1.0,
        100.0,
        1000.0,
        1000000.0,
        1000000000.0
    )

    private val speedValues = doubleArrayOf(
        3600.0,
        60.0,
        1.0,
        3600000.0,
        60000.0,
        1000.0,
        5793638.4,
        96560.64,
        1609.344,
        1097.28,
        18.288,
        0.3048,
        3291.84,
        54.864,
        0.9144,
        1852.0,
        1234800.0,
        1079252848800.0
    )

    private val temperatureValues = doubleArrayOf(0.0, 1.0, 2.0, 3.0, 4.0)

    private val timeValues = doubleArrayOf(
        0.000000001,
        0.000001,
        0.001,
        1.0,
        60.0,
        3600.0,
        86400.0,
        604800.0,
        2628000.0,
        31536000.0
    )

    private val volumeValues = doubleArrayOf(
        0.000000001,
        0.000001,
        0.001,
        1.0,
        1000000000.0,
        0.000001,
        0.00001,
        0.0001,
        0.001,
        0.1,
        1.0,
        0.0002365882,
        0.00378541178
    )

    private val weightValues = doubleArrayOf(
        0.000001,
        0.001,
        1.0,
        100.0,
        1000.0,
        453.59237,
        28.3495231,
        0.2,
        0.06479891,
        1000000.0,
        1016046.91,
        907184.74
    )

    private var fromCoefficient = 0.0
    private var toCoefficient = 0.0

    private val apiKey = "f9d334b1e6287a2c6258"
    private var fromCurrency = ""
    private var toCurrency = ""
    private var conversionRate = 0.0

    private var animStateSaver = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unit_converter)

        val actionBar = supportActionBar
        actionBar!!.title = getString(R.string.unit_converter)
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        popupMenu()

        addNumberValueToText(this, bt0, tvInput, null)
        addNumberValueToText(this, bt1, tvInput, null)
        addNumberValueToText(this, bt2, tvInput, null)
        addNumberValueToText(this, bt3, tvInput, null)
        addNumberValueToText(this, bt4, tvInput, null)
        addNumberValueToText(this, bt5, tvInput, null)
        addNumberValueToText(this, bt6, tvInput, null)
        addNumberValueToText(this, bt7, tvInput, null)
        addNumberValueToText(this, bt8, tvInput, null)
        addNumberValueToText(this, bt9, tvInput, null)

        btDot.setOnClickListener {
            vibratePhone(this)
            if (!tvInput.text.contains(".")) tvInput.text = tvInput.text.toString() + "."
        }

        btAC.setOnClickListener {
            vibratePhone(this)
            tvInput.text = ""
            tvOutput.text = ""
        }

        btDelete.setOnClickListener {
            vibratePhone(this)
            if (tvInput.text.isNotEmpty()) tvInput.text = tvInput.text.subSequence(0, tvInput.length() - 1)
        }

        btEqual.setOnClickListener {
            vibratePhone(this)
            convert()
        }

        fabForward.setOnClickListener {
            fabForward.visibility = View.INVISIBLE
            fabForward.isEnabled = false

            fabBackward.visibility = View.VISIBLE
            fabBackward.isEnabled = true

            fromCoefficient = toCoefficient.also { toCoefficient = fromCoefficient }
            tvInputUnit.text = tvOutputUnit.text.toString().also { tvOutputUnit.text = tvInputUnit.text.toString() }
            convert()
        }

        fabBackward.setOnClickListener {
            fabBackward.visibility = View.INVISIBLE
            fabBackward.isEnabled = false

            fabForward.visibility = View.VISIBLE
            fabForward.isEnabled = true

            fromCoefficient = toCoefficient.also { toCoefficient = fromCoefficient }
            tvInputUnit.text = tvOutputUnit.text.toString().also { tvOutputUnit.text = tvInputUnit.text.toString() }
            convert()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun popupMenu() {
        val popupMenu = PopupMenu(this, btUnitSelect)
        popupMenu.inflate(R.menu.menu_unit)
        popupMenu.setOnMenuItemClickListener {
            if (!animStateSaver) ll.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_500))
            animStateSaver = true

            when (it.itemId) {
                R.id.angle -> {
                    unitId = Unit.Other
                    btUnitSelect.text = getString(R.string.angle)
                    spinnerSetup(R.array.angle, angleValues, R.array.angle_abb)
                    true
                }
                R.id.area -> {
                    unitId = Unit.Other
                    btUnitSelect.text = getString(R.string.area)
                    spinnerSetup(R.array.area, areaValues, R.array.area_abb)
                    true
                }
                R.id.currency -> {
                    unitId = Unit.Currency
                    btUnitSelect.text = getString(R.string.currency)
                    spinnerSetup(R.array.currency, currencyValues, R.array.currency_abb)
                    true
                }
                R.id.data -> {
                    unitId = Unit.Other
                    btUnitSelect.text = getString(R.string.data)
                    spinnerSetup(R.array.data, dataValues, R.array.data_abb)
                    true
                }
                R.id.energy -> {
                    unitId = Unit.Other
                    btUnitSelect.text = getString(R.string.energy)
                    spinnerSetup(R.array.energy, energyValues, R.array.energy_abb)
                    true
                }
                R.id.force -> {
                    unitId = Unit.Other
                    btUnitSelect.text = getString(R.string.force)
                    spinnerSetup(R.array.force, forceValues, R.array.force_abb)
                    true
                }
                R.id.length -> {
                    unitId = Unit.Other
                    btUnitSelect.text = getString(R.string.length)
                    spinnerSetup(R.array.length, lengthValues, R.array.length_abb)
                    true
                }
                R.id.power -> {
                    unitId = Unit.Other
                    btUnitSelect.text = getString(R.string.power)
                    spinnerSetup(R.array.power, powerValues, R.array.power_abb)
                    true
                }
                R.id.pressure -> {
                    unitId = Unit.Other
                    btUnitSelect.text = getString(R.string.pressure)
                    spinnerSetup(R.array.pressure, pressureValues, R.array.pressure_abb)
                    true
                }
                R.id.speed -> {
                    unitId = Unit.Other
                    btUnitSelect.text = getString(R.string.speed)
                    spinnerSetup(R.array.speed, speedValues, R.array.speed_abb)
                    true
                }
                R.id.temperature -> {
                    unitId = Unit.Temperature
                    btUnitSelect.text = getString(R.string.temperature)
                    spinnerSetup(R.array.temperature, temperatureValues, R.array.temperature_abb)
                    true
                }
                R.id.time -> {
                    unitId = Unit.Other
                    btUnitSelect.text = getString(R.string.time)
                    spinnerSetup(R.array.time, timeValues, R.array.time_abb)
                    true
                }
                R.id.volume -> {
                    unitId = Unit.Other
                    btUnitSelect.text = getString(R.string.volume)
                    spinnerSetup(R.array.volume, volumeValues, R.array.volume_abb)
                    true
                }
                R.id.weight -> {
                    unitId = Unit.Other
                    btUnitSelect.text = getString(R.string.weight)
                    spinnerSetup(R.array.weight, weightValues, R.array.weight_abb)
                    true
                }
                else -> true
            }
        }

        btUnitSelect.setOnClickListener {
            try {
                val popup = PopupMenu::class.java.getDeclaredField("mPopup")
                popup.isAccessible = true
                val mPopup = popup.get(popupMenu)
                mPopup.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java).invoke(mPopup, true)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                popupMenu.show()
            }
        }
    }

    private fun spinnerSetup(textArrayResId: Int, values: DoubleArray, abbreviationId: Int) {
        btUnitSelect.animate().translationY(-200f).duration = 100

        spFrom.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_500))
        spFrom.isClickable = true

        spTo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_500))
        spTo.isClickable = true

        fabForward.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_500))
        fabForward.isEnabled = true

        ArrayAdapter.createFromResource(this, textArrayResId, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spFrom.adapter = adapter
                spTo.adapter = adapter
            }

        val abbreviationArray = resources.getStringArray(abbreviationId)

        spFrom.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                tvInputUnit.text = abbreviationArray[position]
                tvInputUnit.startAnimation(AnimationUtils.loadAnimation(this@UnitConverterActivity, R.anim.fade_in_500))

                val selectedText = parent?.getChildAt(0) as TextView
                selectedText.text = abbreviationArray[position]

                when (unitId) {
                    Unit.Currency -> fromCurrency = abbreviationArray[position]
                    else -> fromCoefficient = values[position]
                }
                convert()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        })

        spTo.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                tvOutputUnit.text = abbreviationArray[position]
                tvOutputUnit.startAnimation(AnimationUtils.loadAnimation(this@UnitConverterActivity, R.anim.fade_in_500))

                val selectedText = parent?.getChildAt(0) as TextView
                selectedText.text = abbreviationArray[position]

                when (unitId) {
                    Unit.Currency -> toCurrency = parent.getItemAtPosition(position).toString()
                    else -> toCoefficient = values[position]
                }
                convert()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        })
    }

    private fun convert() {
        if (tvInput != null && tvInput.text.isNotEmpty() && tvInput.text.isNotBlank()) {
            var result = 0.0
            val inputValue = tvInput.text.toString().toDouble()
            var resultStr: String

            when (unitId) {
                Unit.Other -> {
                    result = if (fromCoefficient == toCoefficient) inputValue
                    else inputValue * fromCoefficient / toCoefficient

                    resultStr = result.toFloat().toString()
                    tvOutput.text = CalculationUtil.trimResult(resultStr)
                }
                Unit.Currency -> {
                    val api = "https://free.currconv.com/api/v7/convert?q=${fromCurrency}_$toCurrency&compact=ultra&apiKey=$apiKey"

                    GlobalScope.launch(Dispatchers.IO) {
                        try {
                            val apiResult = URL(api).readText()
                            val jsonObject = JSONObject(apiResult)
                            conversionRate = jsonObject.getString("${fromCurrency}_$toCurrency").toDouble()

                            withContext(Dispatchers.Main) {
                                result = inputValue * conversionRate
                                resultStr = result.toFloat().toString()
                                tvOutput.text = CalculationUtil.trimResult(resultStr)
                            }
                        } catch (e: Exception) {
                            Handler(Looper.getMainLooper()).post {
                                Toast.makeText(this@UnitConverterActivity, getString(R.string.api_error), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                Unit.Temperature -> {
                    when (fromCoefficient) {
                        temperatureValues[0] -> {
                            result = when (toCoefficient) {
                                temperatureValues[0] -> inputValue
                                temperatureValues[1] -> inputValue * 1.8 + 32
                                temperatureValues[2] -> inputValue + 273.15
                                temperatureValues[3] -> (inputValue + 273.15) * 1.8
                                else -> inputValue * 0.8
                            }
                        }
                        temperatureValues[1] -> {
                            result = when (toCoefficient) {
                                temperatureValues[0] -> (inputValue - 32) / 1.8
                                temperatureValues[1] -> inputValue
                                temperatureValues[2] -> (inputValue - 32) / 1.8 + 273.15
                                temperatureValues[3] -> inputValue + 459.67
                                else -> (inputValue - 32) / 2.25
                            }
                        }
                        temperatureValues[2] -> {
                            result = when (toCoefficient) {
                                temperatureValues[0] -> inputValue - 273.15
                                temperatureValues[1] -> 1.8 * (inputValue - 273.15) + 32
                                temperatureValues[2] -> inputValue
                                temperatureValues[3] -> inputValue * 1.8
                                else -> (inputValue - 273.15) * 0.8
                            }
                        }
                        temperatureValues[3] -> {
                            result = when (toCoefficient) {
                                temperatureValues[0] -> (inputValue - 32 - 459.67) / 1.8
                                temperatureValues[1] -> inputValue - 459.67
                                temperatureValues[2] -> inputValue / 1.8
                                temperatureValues[3] -> inputValue
                                else -> (inputValue - 32 - 459.67) / 2.25
                            }
                        }
                        temperatureValues[4] -> {
                            result = when (toCoefficient) {
                                temperatureValues[0] -> inputValue * 1.25
                                temperatureValues[1] -> inputValue * 2.25 + 32
                                temperatureValues[2] -> inputValue * 1.25 + 273.15
                                temperatureValues[3] -> inputValue * 2.25 + 32 + 459.67
                                else -> inputValue
                            }
                        }
                    }
                    resultStr = result.toFloat().toString()
                    tvOutput.text = CalculationUtil.trimResult(resultStr)
                }

            }
        }
    }
}



