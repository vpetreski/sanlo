package io.vanja

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.InputStream

class Sanlo {
    fun execute() {
        loadCompanies()
    }

    private fun loadCompanies() {
        csvReader().open(getCsvFile("app-companies")) {
            readAllWithHeaderAsSequence().forEach { row: Map<String, String> ->
                println(row["company_name"]?.trim() ?: "")
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