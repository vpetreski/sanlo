package io.vanja

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.InputStream
import java.time.LocalDate

data class Company(
    val id: Int,
    val name: String
)

data class Metric(
    val date: LocalDate,
    val appName: String,
    val companyId: Int,
    val revenue: Float,
    val marketingSpend: Float
)

class Sanlo {
    private val companies = hashMapOf<Int, Company>()
    private val metrics = hashMapOf<String, Metric>()

    fun execute() {
        csvReader().open(getCsvFile("app-companies")) {
            readAllWithHeaderAsSequence().forEach { row: Map<String, String> ->
                val company = Company(row["company_id"]?.trim()!!.toInt(), row["company_name"]?.trim()!!)
                companies[company.id] = company
            }
        }

        csvReader().open(getCsvFile("app-financial-metrics")) {
            readAllWithHeaderAsSequence().forEach { row: Map<String, String> ->
                var revenue = row["revenue"]?.trim()
                if (revenue == "") {
                    revenue = "0"
                }

                var marketingSpend = row["marketing_spend"]?.trim()
                if (marketingSpend == "") {
                    marketingSpend = "0"
                }

                val metric = Metric(
                    LocalDate.parse(row["date"]?.trim()),
                    row["app_name"]?.trim()!!,
                    row["company_id"]?.trim()!!.toInt(),
                    revenue!!.toFloat(),
                    marketingSpend!!.toFloat(),
                )

                metrics[metric.appName] = metric

                // TODO
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