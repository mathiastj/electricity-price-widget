package io.github.mathiastj.electricity_price_widget

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.reflect.Type
import java.net.URL

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        Log.i("Electricity price widget", "Main activity")
        val chart = findViewById<BarChart>(R.id.bar_chart)

        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(1.0F, 2.0F))
        entries.add(BarEntry(2.0F, 25.0F))
        entries.add(BarEntry(3.0F, 20.0F))
        entries.add(BarEntry(4.0F, 20.0F))
        entries.add(BarEntry(5.0F, 20.0F))
        entries.add(BarEntry(6.0F, 20.0F))
        entries.add(BarEntry(7.0F, 20.0F))
        entries.add(BarEntry(8.0F, 20.0F))
        entries.add(BarEntry(9.0F, 20.0F))
        entries.add(BarEntry(10.0F, 20.0F))
        entries.add(BarEntry(11.0F, 20.0F))
        entries.add(BarEntry(12.0F, 20.0F))
        entries.add(BarEntry(13.0F, 20.0F))
        entries.add(BarEntry(14.0F, 20.0F))
        entries.add(BarEntry(15.0F, 20.0F))
        entries.add(BarEntry(16.0F, 20.0F))
        entries.add(BarEntry(17.0F, 20.0F))
        entries.add(BarEntry(18.0F, 20.0F))
        entries.add(BarEntry(19.0F, 20.0F))
        entries.add(BarEntry(20.0F, 20.0F))
        entries.add(BarEntry(21.0F, 20.0F))
        entries.add(BarEntry(22.0F, 20.0F))
        entries.add(BarEntry(23.0F, 20.0F))
        entries.add(BarEntry(24.0F, 20.0F))


        val dataSet = BarDataSet(entries, "price")
        val barData = BarData(dataSet)
        chart.data = barData
        chart.invalidate()

        val scope = CoroutineScope(context = Dispatchers.Main)
        scope.launch {
            Log.i("Electricity price widget in scope", "scope")
            val electricityData = try {
                withContext(Dispatchers.IO) {
                    getPollenData()
                }
            } catch (err: Exception) {
                // Set data to null if request fails

                Log.i("Electricity price widget in scope", "went bad")
                err.printStackTrace()
                Log.e("Electricity price widget in scope", err.message)
                null
            }
            Log.i("Electricity price widget in scope", electricityData)
            if (electricityData !== null) {
                Log.i("Electricity price widget text", electricityData)
                val moshi = Moshi.Builder().build()
               /* val map: Type = Types.newParameterizedType(
                    MutableMap::class.java,
                    String::class.java,
                    Any::class.java
                )*/
/*                val type: Type = Types.newParameterizedType(
                    ElectricityDataResponse::class.java,
                    Any::class.java
                )*/
                val adapter = moshi.adapter<ElectricityDataResponse>(ElectricityDataResponse::class.java)
                val json = adapter.fromJson(electricityData)
                Log.i("Electricity price widget json", json.toString())
            } else {
                Log.i("Electricity price widget in scope", "data is null")
            }


        }


    }

    @JsonClass(generateAdapter = true)
    data class ElectricityDataResponse (
        val data: ElectricityDataResponseData,
        val cacheKey: String,
        val conf: Map<String, Object>,
        val header: ElectricityDataResponseHeader,
        val currency: String,
        val endDate: String?,
        val pageId: Int
    )

    @JsonClass(generateAdapter = true)
    data class ElectricityDataResponseHeader (
        val title: String,
        val description: String,
        val questionMarkInfo: String
    )

    @JsonClass(generateAdapter = true)
    data class ElectricityDataResponseData (
        val Rows: Array<ElectricityDataResponseDataRows>,
        val DataStartdate: String,
        val DataEnddate: String
    )

    @JsonClass(generateAdapter = true)
    data class ElectricityDataResponseDataRows (
        val Columns: Array<ElectricityDataResponseDataColumns>,
        val StartTime: String,
        val EndTime: String
    )

    @JsonClass(generateAdapter = true)
    data class ElectricityDataResponseDataColumns (
        val Name: String,
        val Value: String
    )

    private fun getPollenData(): String {
        return URL("https://www.nordpoolspot.com/api/marketdata/page/10?currency=DKK").readText()
    }
}