package io.github.mathiastj.electricity_price_widget

import XAxisDateFormatter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.i("Electricity price widget", "Main activity")
        val chart = findViewById<BarChart>(R.id.bar_chart)
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.valueFormatter = XAxisDateFormatter()
        chart.xAxis.granularity = 1f

        val entries = ArrayList<BarEntry>()

        val scope = CoroutineScope(context = Dispatchers.Main)
        scope.launch {
            Log.i("Electricity price widget in scope", "scope")
            val electricityData = try {
                withContext(Dispatchers.IO) {
                    getElectricityPriceData()
                }
            } catch (err: Exception) {
                Log.i("Electricity price widget in scope", "went bad")
                err.printStackTrace()
                if (err.message !== null) {
                    Log.e("Electricity price widget in scope", err.message)
                }
                null
            }
            if (electricityData !== null) {
                renderBarChartValues(electricityData, entries)
            }
            val dataSet = BarDataSet(entries, "kr/kWh")
            val barData = BarData(dataSet)
            chart.data = barData
            chart.invalidate()


        }


    }

    private fun renderBarChartValues(
        electricityData: String,
        entries: ArrayList<BarEntry>
    ) {
        Log.i("Electricity price widget text", electricityData)
        val moshi = Moshi.Builder().build()
        val adapter = moshi.adapter<ElectricityDataResponse>(ElectricityDataResponse::class.java)
        val json = adapter.fromJson(electricityData)
        Log.i("Electricity price widget json", json.toString())


        for (row in json?.data?.Rows!!) {
            if (row.IsExtraRow) {
                // Extra rows contain info on Min, Max, Average, Peak, Off-Peak 1, Off-Peak 2
                continue
            }
            val rowDate = row.StartTime.substring(0, 10)
            val rowStartHour = row.StartTime.substring(11, 13)
            Log.i("Electricity price widget rowDate", rowDate)
            Log.i("Electricity price widget rowStartHour", rowStartHour)

//            var formatter = DateTimeFormatter.ofPattern("dd-MM-yyyyTHH:MM:SS")
//            var date = LocalDate.parse(rowDate, formatter)
//            Log.i("Electricity price widget date formatted", date.toString())

            val now = LocalDate.now()
            val todayFormatted = now.format(DateTimeFormatter.ISO_LOCAL_DATE)
            val tomorrowFormatted = now.plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)
//            Log.i("Electricity price widget todayFormatted", todayFormatted)
//            Log.i("Electricity price widget tomorrowFormatted", tomorrowFormatted)
            for (column in row.Columns) {
                if (column.Name == "DK2") {
                    val xAxisOffset = if (rowDate == todayFormatted) {
                        0f
                    } else if (rowDate == tomorrowFormatted) {
                        24f
                    } else {
                        // Skip row if it's not today or tomorrow
                        continue
                    }

                    Log.i("Electricity price widget value", column.Value)
                    val commaToDotValue: Float? = try {
                        val commaReplacedWithDotValue = column.Value.replace(',', '.').toFloat()
                        val priceInKrPerHour: Float = commaReplacedWithDotValue.div(1000f)
                        val priceInKrWithVATPerHour = priceInKrPerHour * 1.25f
                        priceInKrWithVATPerHour
                    } catch (err: java.lang.Exception) {
                        null
                    }
                    if (commaToDotValue !== null) {
                        entries.add(BarEntry(rowStartHour.toFloat() + xAxisOffset, commaToDotValue))
                    }
                }
            }

        }
    }

    @JsonClass(generateAdapter = true)
    data class ElectricityDataResponse(
        val data: ElectricityDataResponseData,
        val cacheKey: String,
        val header: ElectricityDataResponseHeader,
        val currency: String,
        val endDate: String?,
        val pageId: Int
    )

    @JsonClass(generateAdapter = true)
    data class ElectricityDataResponseHeader(
        val title: String,
        val description: String,
        val questionMarkInfo: String
    )

    @JsonClass(generateAdapter = true)
    data class ElectricityDataResponseData(
        val Rows: Array<ElectricityDataResponseDataRows>,
        val DataStartdate: String,
        val DataEnddate: String
    )

    @JsonClass(generateAdapter = true)
    data class ElectricityDataResponseDataRows(
        val Columns: Array<ElectricityDataResponseDataColumns>,
        val StartTime: String,
        val EndTime: String,
        val IsExtraRow: Boolean
    )

    @JsonClass(generateAdapter = true)
    data class ElectricityDataResponseDataColumns(
        val Name: String,
        val Value: String
    )

    private fun getElectricityPriceData(): String {
        return URL("https://www.nordpoolspot.com/api/marketdata/page/10?currency=DKK").readText()
    }
}