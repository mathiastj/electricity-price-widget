
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class XAxisDateFormatter : ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {

        val now = LocalDate.now()
        val todayFormatted = now.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val tomorrowFormatted = now.plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)

        return if (value < 24f) {
            "$todayFormatted $${value.toInt()}:00"
        } else {
            "$tomorrowFormatted ${value.toInt()-24}:00"
        }

//        return Date(value.toLong()).toString()
    }
}