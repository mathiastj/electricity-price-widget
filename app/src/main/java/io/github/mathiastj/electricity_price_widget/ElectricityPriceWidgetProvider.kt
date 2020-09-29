package io.github.mathiastj.electricity_price_widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.util.Log
import android.view.View
import android.view.ViewStub
import android.widget.RemoteViews
import android.widget.TextView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.electricity_price_widget_provider.*
import kotlinx.android.synthetic.main.*


/**
 * Implementation of App Widget functionality.
 */
class ElectricityPriceWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        val remoteViews = RemoteViews(context.packageName, R.layout.electricity_price_widget_provider)

        val intent = Intent(context, ElectricityPriceWidgetProvider::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        remoteViews.setOnClickPendingIntent(R.id.layout_widget, pendingIntent)
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }

        val openAppIntent = Intent(context, MainActivity::class.java)

    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {

/*    val graphView = GraphView(context)
    val electricityPrice: LineGraphSeries<DataPoint> = LineGraphSeries()
    electricityPrice.appendData(DataPoint(1.0, 2.0), true, 100)
    electricityPrice.appendData(DataPoint(2.0, 3.0), true, 100)
    electricityPrice.appendData(DataPoint(3.0, 4.0), true, 100)
    electricityPrice.appendData(DataPoint(4.0, 5.0), true, 100)*/

   /* graphView.addSeries(electricityPrice)*/


    val chart = /*view.findViewById<BarChart>(R.id.bar_chart)
    // So this won't wort because I can never create this type in a RemoteView

    val entries = ArrayList<BarEntry>()
    entries.add(BarEntry(1.0F, 2.0F))
    entries.add(BarEntry(2.0F, 25.0F))
    entries.add(BarEntry(50.0F, 75.0F))


    val dataSet = BarDataSet(entries, "price")
    val barData = BarData(dataSet)
    chart.data = barData
    chart.invalidate()


    val conf = Bitmap.Config.RGB_565 // see other conf types
    val bmp = Bitmap.createBitmap(50, 75, conf) // this creates a MUTABLE bitmap
    bmp.eraseColor(Color.WHITE)
    val canvas = Canvas(chart.chartBitmap)
    canvas.drawBitmap(chart.chartBitmap, Matrix(), null)

    *//*chart.draw(canvas)*/

    Log.i("Electricity price widget", "I have been clicked")

//    val graphImage = graphView.takeSnapshot()

    val widgetText = context.getString(R.string.appwidget_text)
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.electricity_price_widget_provider)
//    views.setTextViewText(R.id.appwidget_text, widgetText)
    /*views.setImageViewBitmap(R.id.electricity_price_graph, bmp)*/

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}