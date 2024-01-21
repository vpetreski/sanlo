package io.vanja

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.InputStream
import java.time.LocalDate

data class Company(val id: Int, val name: String)
data class Metric(
    val appName: String,
    val companyId: Int,
    var marketingSpend: Float = 0f,
    var marketingSpendDay: Int? = null,
    var paybackRevenue: Float = 0f,
    var paybackPeriod: Int? = null,
    var totalRevenue: Float = 0f,
    var ltvCacRatio: Float? = null
)

class Sanlo {
    private val companies = hashMapOf<Int, Company>()
    private val metrics = hashMapOf<String, Metric>()

    fun execute(test: Boolean = false) {
        csvReader().open(getCsvFile("${if (test) "example-" else ""}app-companies")) {
            readAllWithHeaderAsSequence().forEach { row: Map<String, String> ->
                val company = Company(row["company_id"]?.trim()!!.toInt(), row["company_name"]?.trim()!!)
                companies[company.id] = company
            }
        }

        csvReader().open(getCsvFile("${if (test) "example-" else ""}app-financial-metrics")) {
            readAllWithHeaderAsSequence().forEach { row: Map<String, String> ->
                var revenue = row["revenue"]?.trim()
                if (revenue == "") {
                    revenue = "0"
                }

                var marketingSpend = row["marketing_spend"]?.trim()

                if (marketingSpend == "") {
                    marketingSpend = "0"
                }

                val date = LocalDate.parse(row["date"]?.trim())

                val currentMetric = Metric(
                    row["app_name"]?.trim()!!,
                    row["company_id"]?.trim()!!.toInt()
                )

                if (!metrics.contains(currentMetric.appName)) {
                    metrics[currentMetric.appName] = currentMetric
                }

                val metric = metrics[currentMetric.appName]

                if (marketingSpend!!.toFloat() != 0f) {
                    metric!!.marketingSpend = marketingSpend.toFloat()
                    metric.marketingSpendDay = date.dayOfMonth
                }

                if (metric!!.marketingSpendDay != null) {
                    metric.paybackRevenue += revenue!!.toFloat()

                    if (metric.paybackPeriod == null && metric.paybackRevenue >= metric.marketingSpend) {
                        metric.paybackPeriod = date.dayOfMonth - metric.marketingSpendDay!! + 1
                    }
                }

                metric.totalRevenue += revenue!!.toFloat()

                if (metric.marketingSpend != 0f) {
                    metric.ltvCacRatio = metric.totalRevenue / metric.marketingSpend
                    metric.ltvCacRatio
                }
            }

            // TODO
            println(metrics)
        }
    }

    private fun getCsvFile(name: String): InputStream {
        return this::class.java.classLoader.getResource("$name.csv")!!.openStream()
    }
}

fun main() {
    Sanlo().execute()
}