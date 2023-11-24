@file:JsQualifier("google.visualization")
package google.visualization

external class DataTable {

    fun addColumn(type: String, name: String)

    fun addRows(rows: Array<Array<Any>>)

}