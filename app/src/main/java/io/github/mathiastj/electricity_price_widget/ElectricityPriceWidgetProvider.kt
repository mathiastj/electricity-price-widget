package io.github.mathiastj.electricity_price_widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.RemoteViews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class ElectricityPriceWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Log.i("Electricity price widget", "I have been clicked")

        // Set up update on click
        appWidgetIds.forEach { appWidgetId ->
            val views = RemoteViews(context.packageName, R.layout.electricity_price_widget_provider)

            val intentUpdate = Intent(context, ElectricityPriceWidgetProvider::class.java)
            intentUpdate.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

            val idArray = intArrayOf(appWidgetId)
            intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray)
            val pendingUpdate = PendingIntent.getBroadcast(
                context, appWidgetId, intentUpdate,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            views.setOnClickPendingIntent(R.id.layout_widget, pendingUpdate)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }


        Log.i("Electricity price widget", "I have been clicked")

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val today = LocalDate.now()
        val tomorrow = today.plusDays((1))
        val formattedToday = today.format(formatter)
        val formattedTomorrow = tomorrow.format(formatter)

        val views = RemoteViews(context.packageName, R.layout.electricity_price_widget_provider)

        val scope = CoroutineScope(context = Dispatchers.Main)
        scope.launch {
            val todayPricesImage = try {
                withContext(Dispatchers.IO) {
                    getElectricityPriceImage(formattedToday)
                }
            } catch (err: Exception) {
                Log.i("Electricity price widget in scope", "went bad")
                err.printStackTrace()
                if (err.message !== null) {
                    Log.e("Electricity price widget in scope", err.message)
                }
                null
            }
            if (todayPricesImage !== null) {
                Log.i("Electricity price widget in scope", "today")
                val bitmap: Bitmap =
                    BitmapFactory.decodeByteArray(todayPricesImage, 0, todayPricesImage.size)
                views.setImageViewBitmap(R.id.today_price_image, bitmap)

            }

            val tomorrowPricesImage = try {
                withContext(Dispatchers.IO) {
                    getElectricityPriceImage(formattedTomorrow)
                }
            } catch (err: Exception) {
                Log.i("Electricity price widget in scope", "went bad")
                err.printStackTrace()
                if (err.message !== null) {
                    Log.e("Electricity price widget in scope", err.message)
                }
                null
            }
            if (tomorrowPricesImage !== null) {
                Log.i("Electricity price widget in scope", "tomorrow")
                val bitmap: Bitmap =
                    BitmapFactory.decodeByteArray(tomorrowPricesImage, 0, tomorrowPricesImage.size)
                views.setImageViewBitmap(R.id.tomorrow_price_image, bitmap)

            }
            appWidgetIds.forEach { appWidgetId ->
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }


    private fun getElectricityPriceImage(day: String): ByteArray {
        Log.i(
            "url",
            "https://raw.githubusercontent.com/mathiastj/electricity-price-render/master/daily_prices/${day}.png"
        )
        return URL("https://raw.githubusercontent.com/mathiastj/electricity-price-render/master/daily_prices/${day}.png").readBytes()
    }
}

