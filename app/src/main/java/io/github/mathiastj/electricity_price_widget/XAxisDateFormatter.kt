
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
        val tomorrow = now.plusDays(1)

        return if (value < 24f) {
            "${now.dayOfMonth}/${now.monthValue} $${value.toInt()}:00"
        } else {
            "${tomorrow.dayOfMonth}/${tomorrow.monthValue} ${value.toInt()-24}:00"
        }

//        return Date(value.toLong()).toString()
    }
}