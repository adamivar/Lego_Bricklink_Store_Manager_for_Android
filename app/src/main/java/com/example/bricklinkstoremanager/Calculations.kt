package com.example.bricklinkstoremanager

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

fun calculateAverageSalesAndQtyPerMonth(saleDetails: List<SaleDetail>): Pair<Double, Double> {
    val sixMonthsAgo = Calendar.getInstance().apply {
        add(Calendar.MONTH, -6)
    }.time

    val salesInLastSixMonths = saleDetails.filter { saleDetail ->
        val saleDate = try {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).parse(saleDetail.date_ordered)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        saleDate?.after(sixMonthsAgo) == true
    }

    // Log the most recent sale
    if (salesInLastSixMonths.isNotEmpty()) {
        val mostRecentSale = salesInLastSixMonths.maxByOrNull { it.date_ordered }
        if (mostRecentSale != null) {
            println("Most recent sale: ${mostRecentSale.date_ordered}")
        }
    }

    // Log the first and last date of the 6 month time window
    val sixMonthsLater = Calendar.getInstance().apply {
        add(Calendar.MONTH, 0)
    }.time
    println("First date of the 6 month time window: $sixMonthsAgo")
    println("Last date of the 6 month time window: $sixMonthsLater")

    val totalPartsSold = salesInLastSixMonths.sumOf { it.quantity }.toDouble()
    val numberOfSales = salesInLastSixMonths.size.toDouble()

    return if (salesInLastSixMonths.isNotEmpty()) {
        Pair(numberOfSales / 6, totalPartsSold / 6)
    } else {
        Pair(0.0, 0.0)
    }
}


fun calculateAvgPartsPerSale(saleDetails: List<SaleDetail>): Double {
    // Log the size of the saleDetails list
    Log.d("MyApp", "Number of Sale Details: ${saleDetails.size}")

    // Check if the list of sale details is empty to avoid division by zero
    if (saleDetails.isEmpty()) {
        // Log and return 0.0 when the list is empty
        Log.d("MyApp", "No Sale Details to calculate average parts per sale.")
        return 0.0
    }

    // Calculate the total quantity
    val totalQuantity = saleDetails.sumOf { it.quantity }

    // Log the totalQuantity
    Log.d("MyApp", "Total Quantity: $totalQuantity")

    // Calculate the average quantity
    val averageQuantity = totalQuantity.toDouble() / saleDetails.size

    // Log the averageQuantity
    Log.d("MyApp", "Average Quantity: $averageQuantity")

    return averageQuantity
}


fun calculateVolatility(priceDetails: List<SaleDetail>): Double {
    // Extract unit prices from price details
    val prices = priceDetails.map { it.unit_price.toDouble() }

    val returns = mutableListOf<Double>()
    for (i in 1 until prices.size) {
        val prevPrice = prices[i - 1]
        val currentPrice = prices[i]
        val dailyReturn = (currentPrice - prevPrice) / prevPrice
        returns.add(dailyReturn)
    }

    // Calculate the standard deviation of returns as volatility
    val mean = returns.average()
    val squaredDifferences = returns.map { (it - mean).pow(2) }
    val variance = squaredDifferences.sum() / (returns.size - 1)

    return sqrt(variance)
}


fun calculateActualMinMaxPricesWithZScore(priceDetails: List<SaleDetail>, zThreshold: Int): Pair<Double?, Double?> {
    val prices = priceDetails.map { it.unit_price.toDouble() }

    // Log the prices list
    Log.d("MyApp", "Prices: $prices")

    val meanPrice = prices.average()
    val stdDev = if (prices.size > 1) prices.stdDev() else 0.0

    // Log meanPrice and stdDev
    Log.d("MyApp", "Mean Price: $meanPrice, Standard Deviation: $stdDev")

    val zScores = prices.map { (it - meanPrice) / stdDev }

    // Log the zScores list
    Log.d("MyApp", "Z-Scores: $zScores")

    val filteredPrices = prices.zip(zScores).filter { abs(it.second) <= zThreshold }.map { it.first }

    // Log the filteredPrices list
    Log.d("MyApp", "Filtered Prices: $filteredPrices")

    return if (filteredPrices.isNotEmpty()) {
        val minPrice = filteredPrices.minOrNull()
        val maxPrice = filteredPrices.maxOrNull()

        // Log minPrice and maxPrice
        Log.d("MyApp", "Min Price: $minPrice, Max Price: $maxPrice")

        Pair(minPrice, maxPrice)
    } else {
        if (prices.isNotEmpty()) {
            // Log when there are no filtered prices
            Log.d("MyApp", "No prices within zThreshold")

            Pair(0.0, prices.max())
        } else {
            // Handle the case when both filteredPrices and prices are empty
            Pair(0.0, 0.0) // You can choose appropriate values here
        }
    }
}

fun calculateUnitsAndListingsWithinRange(priceDetails: List<PriceDetail>, minPrice: Float, maxPrice: Float): Pair<Int, Int> {
    var unitsForSale = 0
    var listingsWithinRange = 0

    for (priceDetail in priceDetails) {
        val unitPrice = priceDetail.unit_price // Assuming 'unitPrice' is the correct property name


        if (unitPrice.toDouble() in minPrice..maxPrice) {
            unitsForSale += priceDetail.quantity
            listingsWithinRange++
        }

    }

    // Log the final result before returning
    Log.d("MyApp", "Final Result - Units For Sale: $unitsForSale, Listings Within Range: $listingsWithinRange")

    return Pair(unitsForSale, listingsWithinRange)
}


fun List<Double>.stdDev(): Double {
    val mean = this.average()
    val variance = this.map { it - mean }.map { it * it }.average()
    return sqrt(variance)
}