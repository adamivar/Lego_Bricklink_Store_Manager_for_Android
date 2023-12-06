package com.example.bricklinkstoremanager

//API

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.webkit.URLUtil
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.opencsv.CSVReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URI
import java.text.DecimalFormat
import java.util.regex.Pattern


class MainActivity : AppCompatActivity() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    @SuppressLint("MissingInflatedId", "ClickableViewAccessibility", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val gson = Gson()
        val apiService = ApiService() // Initialize your ApiService


        val emptyQuery: Map<String, String> = mapOf()

        coroutineScope.launch {
            val storeResponse = apiService.apiSimpleGetRequest("inventories", emptyQuery)
            val categoryListResponse = apiService.apiSimpleGetRequest("categories", emptyQuery)

            val storeInfo = gson.fromJson(storeResponse, APIResponseForStoreInfo::class.java)
            val categoryList = gson.fromJson(categoryListResponse, APIResponseForCategoryList::class.java)

            println(storeInfo)
            println(categoryList)

            val verticalLayout = findViewById<LinearLayout>(R.id.VerticalInventoryLayout)


            if (storeInfo.meta.code >= 400 || categoryList.meta.code >= 400 ){
                Toast.makeText(applicationContext, "Description: ${storeInfo.meta.description} Message: ${storeInfo.meta.message}", Toast.LENGTH_SHORT).show()
            }
            else{
                val sortedListingList = storeInfo.data.sortedByDescending{ it.date_created }

                val assetManager: AssetManager = assets

                val colorMap = mutableMapOf<String, String>()

                try {
                    val inputStream = assetManager.open("LegoColorGuide.csv") // Replace with your CSV file name
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    var line: String?

                    while (withContext(Dispatchers.IO) {
                            reader.readLine()
                        }.also { line = it } != null) {
                        val parts = line!!.split(",")
                        if (parts.size == 3) {
                            val colorId: String = parts[0]
                            val hexValue: String = parts[2]
                            colorMap[colorId] = hexValue
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                for (listing in sortedListingList) {



                    val horizontalLayout = LinearLayout(this@MainActivity)
                    horizontalLayout.orientation = LinearLayout.HORIZONTAL

                    // Get the hex value based on listing.color_id
                    val colorId: String = listing.color_id.toString()

                    val hexValue: String = "#${colorMap[colorId]}" ?: "#FFC0CB" // Default color if not found


                    // Set the background color
                    horizontalLayout.setBackgroundColor(Color.parseColor(hexValue))

                    // Create TextViews for each field
                    val typeTextView = TextView(this@MainActivity)
                    val idTextView = TextView(this@MainActivity)
                    val remarksTextView = TextView(this@MainActivity)
                    val quantityTextView = TextView(this@MainActivity)
                    val priceTextView = TextView(this@MainActivity)

                    // Set the text for each TextView and make it bold
                    typeTextView.text = categoryList.data.find { it.category_id == listing.item.category_id }?.category_name
                    idTextView.text = "${listing.item.type} \n ${listing.item.no}"
                    remarksTextView.text = listing.remarks
                    quantityTextView.text = listing.quantity.toString()
                    val priceFormat = DecimalFormat("$#.##")
                    priceTextView.text = priceFormat.format(listing.unit_price.toDouble())

                    val boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD)
                    typeTextView.setTypeface(boldTypeface)
                    idTextView.setTypeface(boldTypeface)
                    remarksTextView.setTypeface(boldTypeface)
                    quantityTextView.setTypeface(boldTypeface)
                    priceTextView.setTypeface(boldTypeface)

                    // Check if the background color is dark (e.g., by comparing brightness)
                    val colorInt: Int = Color.parseColor(hexValue)
                    val brightness = (Color.red(colorInt) * 299 + Color.green(colorInt) * 587 + Color.blue(colorInt) * 114) / 1000
                    val textColor = if (brightness < 128) {Color.WHITE} else {Color.BLACK}

                    // Set text color for all TextViews
                    typeTextView.setTextColor(textColor)
                    idTextView.setTextColor(textColor)
                    remarksTextView.setTextColor(textColor)
                    quantityTextView.setTextColor(textColor)
                    priceTextView.setTextColor(textColor)

                    // Create LayoutParams with weights and margins
                    val weight1Params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    val weight2Params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f)
                    val weight3Params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 3f)
                    val weight4Params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 4f)

                    // Add left margin to typeTextView and right margin to priceTextView
                    weight1Params.setMargins(8, 0, 8, 0) // 8 pixels to the left of typeTextView
                    weight2Params.setMargins(8, 0, 8, 0) // 8 pixels to the right of priceTextView
                    weight3Params.setMargins(8, 0, 8, 0) // 8 pixels to the left of typeTextView
                    weight4Params.setMargins(8, 0, 8, 0) // 8 pixels to the right of priceTextView

                    // Apply layout parameters to TextViews
                    typeTextView.layoutParams = weight2Params
                    idTextView.layoutParams = weight3Params
                    remarksTextView.layoutParams = weight4Params
                    quantityTextView.layoutParams = weight1Params
                    priceTextView.layoutParams = weight2Params

                    // Add TextViews to the horizontalLayout
                    horizontalLayout.addView(typeTextView)
                    horizontalLayout.addView(idTextView)
                    horizontalLayout.addView(remarksTextView)
                    horizontalLayout.addView(quantityTextView)
                    horizontalLayout.addView(priceTextView)

                    // Add the horizontalLayout to the verticalLayout (assuming you have defined verticalLayout)
                    verticalLayout.addView(horizontalLayout)

                    // Add a grey divider view
                    val dividerView = View(this@MainActivity)
                    val dividerParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        8 // Height of the divider
                    )
                    dividerView.setBackgroundColor(Color.WHITE)
                    dividerView.layoutParams = dividerParams
                    verticalLayout.addView(dividerView)
                }
            }
        }




        val itemIdBox = findViewById<TextInputEditText>(R.id.ItemIdInputBox)
        // Populate the Spinner with item types
        val itemTypes = arrayOf("PART", "SET", "MINIFIG", "BOOK", "GEAR", "INSTRUCTION", "ORIGINAL_BOX")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, itemTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val itemTypeSpinner = findViewById<Spinner>(R.id.ItemTypeSpinner)
        itemTypeSpinner.adapter = adapter

        val spinner3 = findViewById<Spinner>(R.id.spinner3)
        val customAdapter1 = ArrayAdapter<Spannable>(this@MainActivity, android.R.layout.simple_spinner_item, listOf(SpannableString("0 - No Color")))
        customAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner3.adapter = customAdapter1

        val csvFilePath = "LegoColorGuide.csv" // Define the path to your CSV file
        // Create a flag to track whether the Spinner has been clicked
        var isSpinnerClicked = false
        // Move these constants outside the touch event listener
        val assetManager = this.assets
        val defaultItemId = "3001" // Replace with your default value

        spinner3.setOnTouchListener { _, _ ->
            if (!isSpinnerClicked) {
                val itemType = itemTypeSpinner.selectedItem.toString()
                var itemId = itemIdBox.text.toString()
                if (itemId.isEmpty()) {
                    itemId = defaultItemId
                }
                coroutineScope.launch(Dispatchers.IO) {
                    try {
                        val colorInfoResponse = apiService.apiItemGetRequest("PART", itemId, "colors", emptyMap())
                        val colorInfo = gson.fromJson(colorInfoResponse, APIResponseForColorInfo::class.java)
                        val coloredItems: List<SpannableString>

                        if (colorInfo.meta.code > 399) {
                            withContext(Dispatchers.Main) {
                                Log.d("MyApp", "Error: ${colorInfo.meta.message}")
                                Toast.makeText(this@MainActivity, "${colorInfo.meta.message}_WITH_COLORS" , Toast.LENGTH_SHORT).show()
                            }
                            // If there is an error in the meta section, show only the color with ID 0
                            coloredItems = listOf(SpannableString("0 - No Color"))
                        } else if(colorInfo.data.isEmpty()){
                            withContext(Dispatchers.Main) {
                                Log.d("MyApp", "Error: ${colorInfo.meta.message}")
                                Toast.makeText(this@MainActivity, "NO_COLORS_FOUND" , Toast.LENGTH_SHORT).show()
                            }
                            // If there is an error in the meta section, show only the color with ID 0
                            coloredItems = listOf(SpannableString("0 - No Color"))

                        } else {
                            val sortedColorData = colorInfo.data.sortedByDescending { it.quantity }
                            coloredItems = sortedColorData.map { it ->
                                val colorId = it.color_id.toString()
                                val colorName: String
                                val colorHexCode: String

                                val inputStream = assetManager.open(csvFilePath)
                                val reader = CSVReader(InputStreamReader(inputStream))
                                var nextLine: Array<String>?

                                while (reader.readNext().also { nextLine = it } != null) {
                                    if (nextLine!![0] == colorId) {
                                        colorName = nextLine!![1]
                                        colorHexCode = nextLine!![2]

                                        val color = Color.parseColor("#$colorHexCode")
                                        val textBrightness = (Color.red(color) * 299 + Color.green(color) * 587 + Color.blue(color) * 114) / 1000

                                        val coloredText = SpannableString("$colorId - $colorName")
                                        coloredText.setSpan(BackgroundColorSpan(color), 0, coloredText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                                        if (textBrightness < 128) {
                                            // Background is dark, set text color to white
                                            coloredText.setSpan(ForegroundColorSpan(Color.WHITE), 0, coloredText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                        } else {
                                            // Background is light, set text color to black
                                            coloredText.setSpan(ForegroundColorSpan(Color.BLACK), 0, coloredText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                        }

                                        Log.d("MyApp", "Colored Text: $coloredText")

                                        return@map coloredText
                                    }
                                }
                                Log.d("MyApp", "Color Not Found")
                                return@map SpannableString("$colorId - Color Not Found")
                            }
                        }
                        withContext(Dispatchers.Main) {
                            val customAdapter = ArrayAdapter<Spannable>(this@MainActivity, android.R.layout.simple_spinner_item, coloredItems)
                            customAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            spinner3.adapter = customAdapter
                            isSpinnerClicked = true
                        }
                    } catch (e: Exception) {
                        Log.e("MyApp", "Exception: ${e.message}", e)
                    }
                }
            }
            isSpinnerClicked = false
            false // Return false to allow the Spinner to handle the touch event as well
        }



        val button = findViewById<Button>(R.id.SubmitButton)
        button.setOnClickListener {
            var itemId = itemIdBox.text.toString()
            if (itemId.isEmpty()) {
                itemId = "3001" // replace with your default value
            }
            val selectedItem = spinner3.selectedItem

            var colorId: String
            if (selectedItem != null) {
                colorId = spinner3.selectedItem.toString()
                println(colorId)
                colorId = colorId.substringBefore(" - ").toInt().toString()
                // Rest of your code here
            } else {
                colorId = "0"
            }

            if (URLUtil.isValidUrl(itemId)) {
                try {
                    val url = URI(itemId)
                    // If the URL is valid, you can access its components like hostname, path, etc.
                    val hostname = url.host
                    val path = url.path
                    val query = url.query
                    val fragment = url.fragment

                    itemId = query.substring(2)
                    if(fragment.contains("C=")){
                        colorId = Pattern
                            .compile("C=(\\d+)")
                            .matcher(fragment)
                            .group(1)?.toInt().toString()

                    }
                    else{
                        colorId = "0"
                    }
                    // Print the parsed components using log.d
                    Log.d("URL Parsing", "Hostname: $hostname")
                    Log.d("URL Parsing", "Path: $path")
                    Log.d("URL Parsing", "Query: $query")
                    Log.d("URL Parsing", "Fragment: $fragment")
                } catch (e: Exception) {
                    Log.e("URL Parsing", "Error parsing URL: $itemId")
                }
            }


            val itemInfoQueries: Map<String, String> = mapOf() // replace with your queries
            val salesInfoQueries: Map<String, String> = mapOf(
                "color_id" to colorId,
                "guide_type" to "sold",
                "new_or_used" to "U",
                "country_code" to "US?currency_code=USD"
            ) // replace with your queries
            val pricesInfoQueries: Map<String, String> = mapOf(
                "color_id" to colorId,
                "guide_type" to "stock",
                "new_or_used" to "U",
                "country_code" to "US?currency_code=USD"
            ) // replace with your queries
            val subsetInfoQueries: Map<String, String> = mapOf(
                "color_id" to colorId,
                "box" to "false",
                "instruction" to "false",
                "break_subsets" to "true",
                "break_minifigs" to "true"
            ) // replace with your queries

            // Get the selected item type from the Spinner
            val itemType = itemTypeSpinner.selectedItem.toString()
            val infoDict = InfoDict()

            coroutineScope.launch {

                val itemInfoResponse = apiService.apiItemGetRequest(itemType, itemId, "null", itemInfoQueries)
                val salesInfoResponse = apiService.apiItemGetRequest(itemType, itemId, "price", salesInfoQueries)
                val pricesInfoResponse = apiService.apiItemGetRequest(itemType, itemId, "price", pricesInfoQueries)
                val subsetInfoResponse = apiService.apiItemGetRequest(itemType, itemId, "subsets", subsetInfoQueries)


                val itemInfo = gson.fromJson(itemInfoResponse, APIResponseForItemInfo::class.java)
                val salesInfo = gson.fromJson(salesInfoResponse, APIResponseForSalesInfo::class.java)
                val pricesInfo = gson.fromJson(pricesInfoResponse, APIResponseForPricesInfo::class.java)
                val subsetInfo = gson.fromJson(subsetInfoResponse, APIResponseForSubsetInfo::class.java)

                if (itemInfo.meta.code > 399){
                    Toast.makeText(applicationContext, "Description: ${itemInfo.meta.description} Message: ${itemInfo.meta.message}", Toast.LENGTH_SHORT).show()
                }
                else if(salesInfo.meta.code > 399){
                    Toast.makeText(applicationContext, "COLOR_ID_NOT_FOUND", Toast.LENGTH_SHORT).show()
                }
                else{
                    val tag = "Debugging"


                    Log.d(tag, "itemInfo.data.description = ${itemInfo.data.description}")
                    if (itemInfo.data.description != null){
                        infoDict.description = itemInfo.data.description
                    }




                    infoDict.year_released = itemInfo.data.year_released

                    infoDict.dim_x = itemInfo.data.dim_x
                    infoDict.dim_y = itemInfo.data.dim_y
                    infoDict.dim_z = itemInfo.data.dim_z


                    infoDict.name = itemInfo.data.name
                    Log.d(tag, "infoDict.name = ${infoDict.name}")

                    infoDict.type = itemInfo.data.type
                    Log.d(tag, "infoDict.type = ${infoDict.type}")

                    infoDict.no = itemInfo.data.no
                    Log.d(tag, "infoDict.no = ${infoDict.no}")

                    infoDict.image_url = itemInfo.data.image_url
                    Log.d(tag, "infoDict.image_url = ${infoDict.image_url}")

                    infoDict.color = colorId
                    Log.d(tag, "infoDict.color = ${infoDict.color}")

                    infoDict.volatility = String.format("%.2f", calculateVolatility(salesInfo.data.price_detail).toDouble()).toDouble()
                    Log.d(tag, "infoDict.volatility = ${infoDict.volatility}")


                    val minMaxPair = calculateActualMinMaxPricesWithZScore(salesInfo.data.price_detail, 1)
                    Log.d(tag, minMaxPair.toString())
                    infoDict.min_used_sale_price = String.format("%.2f", minMaxPair.first!!).toDouble()
                    Log.d(tag, "infoDict.min_used_sale_price = ${infoDict.min_used_sale_price}")
                    infoDict.max_used_sale_price = String.format("%.2f", minMaxPair.second!!).toDouble()
                    Log.d(tag, "infoDict.max_used_sale_price = ${infoDict.max_used_sale_price}")

                    if (salesInfo.data.avg_price.toDouble() != 0.0){
                        infoDict.avg_used_sale_price = String.format("%.2f", salesInfo.data.avg_price.toDouble()).toDouble()
                    }
                    else{
                        infoDict.avg_used_sale_price = String.format("%.2f", pricesInfo.data.avg_price.toDouble()).toDouble()
                    }
                    Log.d(tag, "infoDict.avg_used_sale_price = ${infoDict.avg_used_sale_price}")


                    infoDict.qty_avg_used_sale_price = String.format("%.2f", salesInfo.data.qty_avg_price.toDouble()).toDouble()
                    Log.d(tag, "infoDict.qty_avg_used_sale_price = ${infoDict.qty_avg_used_sale_price}")

                    val avgSalesQtyPerMonth = calculateAverageSalesAndQtyPerMonth(salesInfo.data.price_detail)
                    infoDict.avg_sales_per_month = String.format("%.2f", avgSalesQtyPerMonth.first).toDouble()
                    Log.d(tag, "infoDict.avg_sales_per_month = ${infoDict.avg_sales_per_month}")
                    infoDict.avg_parts_per_month = String.format("%.2f", avgSalesQtyPerMonth.second).toDouble()
                    Log.d(tag, "infoDict.avg_parts_per_month = ${infoDict.avg_parts_per_month}")

                    infoDict.avg_parts_per_sale = String.format("%.2f", calculateAvgPartsPerSale(salesInfo.data.price_detail)).toDouble()
                    Log.d(tag, "infoDict.avg_parts_per_sale = ${infoDict.avg_parts_per_sale}")

                    val sellersAndSupplyPair = calculateUnitsAndListingsWithinRange(pricesInfo.data.price_detail, infoDict.min_used_sale_price.toFloat(), infoDict.max_used_sale_price.toFloat().takeIf { it != 0f } ?: infoDict.avg_used_sale_price.toFloat())
                    infoDict.current_item_supply = sellersAndSupplyPair.first
                    Log.d(tag, "infoDict.current_item_supply = ${infoDict.current_item_supply}")
                    infoDict.number_of_sellers = sellersAndSupplyPair.second
                    Log.d(tag, "infoDict.number_of_sellers = ${infoDict.number_of_sellers}")

                    infoDict.listings_for_every_one_purchase = String.format("%.2f", (infoDict.current_item_supply / infoDict.avg_parts_per_month)).toDouble()
                    Log.d(tag, "listings_for_every_one_purchase = ${infoDict.listings_for_every_one_purchase}")

                    infoDict.sellers_for_every_one_buyer = String.format("%.2f", (infoDict.number_of_sellers / infoDict.avg_sales_per_month)).toDouble()
                    Log.d(tag, "sellers_for_every_one_buyer = ${infoDict.sellers_for_every_one_buyer}")

                    infoDict.weight = String.format("%.2f", itemInfo.data.weight).toDouble()
                    Log.d(tag, "infoDict.weight = ${infoDict.weight}")

                    infoDict.est_price_per_gram = String.format("%.2f", if (infoDict.weight != 0.0) { infoDict.avg_used_sale_price / infoDict.weight } else { 0.0 }).toDouble()
                    Log.d(tag, "infoDict.est_price_per_gram = ${infoDict.est_price_per_gram}")



                    if (subsetInfo.data.isNotEmpty()){
                        infoDict.unique_parts = subsetInfo.data.size
                        Log.d("Debug", "unique_parts: ${infoDict.unique_parts}")
                        infoDict.total_parts = subsetInfo.data.flatMap { it.entries }.sumOf { it.quantity }
                        Log.d("Debug", "total_parts: ${infoDict.total_parts}")
                        infoDict.est_price_per_part = String.format("%.2f", if (infoDict.total_parts != 0) { infoDict.avg_used_sale_price / infoDict.total_parts } else {0.0}).toDouble()
                        Log.d("Debug", "est_price_per_part: ${infoDict.est_price_per_part}")
                    }
                    else{
                        infoDict.unique_parts = 1
                        Log.d("Debug", "unique_parts: ${infoDict.unique_parts}")
                        infoDict.total_parts = 1
                        Log.d("Debug", "total_parts: ${infoDict.total_parts}")
                        infoDict.est_price_per_part = String.format("%.2f", if (infoDict.total_parts != 0) { infoDict.avg_used_sale_price / infoDict.total_parts } else {0.0}).toDouble()
                        Log.d("Debug", "est_price_per_part: ${infoDict.est_price_per_part}")

                    }

                    val intent = Intent(this@MainActivity, ResultsPage::class.java).apply {
                        Log.d("LOOK HERE", infoDict.toString())
                        putExtra("infoDict", infoDict)
                    }
                    startActivity(intent)
                }
            }
        }
    }
}




