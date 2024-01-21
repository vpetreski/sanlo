package io.vanja

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
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
    var ltvCacRatio: Float? = null,
    var riskScore: Double = 0.0,
    var riskRating: String = "Unacceptable"
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

                if (metric.paybackPeriod != null && metric.ltvCacRatio != null) {
                    metric.riskScore =
                        getPaybackPeriodValue(metric.paybackPeriod!!) * 0.7 + getLtvCacRatioValue(metric.ltvCacRatio!!) * 0.3
                    metric.riskRating = getRiskRating(metric.riskScore)
                }
            }
        }

        val rows = mutableListOf(listOf("company_id", "company_name", "app_name", "risk_score", "risk_rating"))
        metrics.toList()
            .sortedByDescending { it.second.riskScore }
            .forEach { (_, v) ->
                rows.add(
                    listOf(
                        v.companyId.toString(),
                        companies[v.companyId]!!.name,
                        v.appName,
                        v.riskScore.toInt().toString(),
                        v.riskRating
                    )
                )
            }

        csvWriter().writeAll(rows, "app-credit-risk-ratings.csv")
    }

    private fun getPaybackPeriodValue(input: Int): Int {
        return when (input) {
            in 0..6 -> 100
            in 7..13 -> 80
            in 14..20 -> 60
            in 21..27 -> 30
            else -> {
                10
            }
        }
    }

    private fun getLtvCacRatioValue(input: Float): Int {
        return when {
            input >= 3.0 -> 100
            input >= 2.5 && input < 3.0 -> 80
            input >= 2.0 && input < 2.5 -> 60
            input >= 1.5 && input < 2.0 -> 30
            else -> {
                10
            }
        }
    }

    private fun getRiskRating(input: Double): String {
        return when (input) {
            in 85.0..100.0 -> "Undoubted"
            in 65.0..84.0 -> "Low"
            in 45.0..64.0 -> "Moderate"
            in 25.0..44.0 -> "Cautionary"
            in 15.0..24.0 -> "Unsatisfactory"
            else -> {
                "Unacceptable"
            }
        }
    }

    private fun getCsvFile(name: String): InputStream {
        return this::class.java.classLoader.getResource("$name.csv")!!.openStream()
    }
}

fun main() {
    Sanlo().execute()
}