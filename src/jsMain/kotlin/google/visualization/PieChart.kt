@file:JsQualifier("google.visualization")
package google.visualization

import org.w3c.dom.Element

external class PieChart(container: Element?) {

    fun draw(data: DataTable, options: PieChartOptions)

}