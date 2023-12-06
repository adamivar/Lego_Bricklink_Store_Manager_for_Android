package com.example.bricklinkstoremanager

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SellPage : AppCompatActivity() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sell_page)

        val infoDict = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("infoDict", InfoDict::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("infoDict") as? InfoDict
        }

        if (infoDict != null) {
            findViewById<TextInputLayout>(R.id.color_id)
                .editText?.setText(infoDict.color)
            findViewById<TextInputLayout>(R.id.unit_price)
                .editText?.setText(infoDict.avg_used_sale_price.toString())
            findViewById<TextInputLayout>(R.id.description)
                .editText?.setText(infoDict.description)

            if (infoDict.avg_used_sale_price < 0.10) {
                // Set all TextInputLayout fields to 0
                findViewById<TextInputLayout>(R.id.tier_price1).editText?.setText("0")
                findViewById<TextInputLayout>(R.id.tier_price2).editText?.setText("0")
                findViewById<TextInputLayout>(R.id.tier_price3).editText?.setText("0")
                findViewById<TextInputLayout>(R.id.tier_quantity1).editText?.setText("0")
                findViewById<TextInputLayout>(R.id.tier_quantity2).editText?.setText("0")
                findViewById<TextInputLayout>(R.id.tier_quantity3).editText?.setText("0")
            } else {
                // Calculate and set values based on avg_used_sale_price
                findViewById<TextInputLayout>(R.id.tier_price1).editText?.setText(String.format("%.2f", infoDict.avg_used_sale_price * 0.90))
                findViewById<TextInputLayout>(R.id.tier_price2).editText?.setText(String.format("%.2f", infoDict.avg_used_sale_price * 0.80))
                findViewById<TextInputLayout>(R.id.tier_price3).editText?.setText(String.format("%.2f", infoDict.avg_used_sale_price * 0.70))
            }


            val createListingButton = findViewById<Button>(R.id.createListingButton)
            createListingButton.setOnClickListener {
                val apiService = ApiService()

                coroutineScope.launch {

                    val item = Item(no = infoDict.no, type = infoDict.type)

                    val body = PostValues(
                        item = item,
                        color_id = findViewById<TextInputLayout>(R.id.color_id).editText?.text.toString().toInt(),
                        quantity = findViewById<TextInputLayout>(R.id.quantity).editText?.text.toString().toInt(),
                        unit_price = findViewById<TextInputLayout>(R.id.unit_price).editText?.text.toString().toFloat(),
                        new_or_used = findViewById<TextInputLayout>(R.id.new_or_used).editText?.text.toString(),
                        completeness = findViewById<TextInputLayout>(R.id.completeness).editText?.text.toString(),
                        description = findViewById<TextInputLayout>(R.id.description).editText?.text.toString(),
                        remarks = findViewById<TextInputLayout>(R.id.remarks).editText?.text.toString(),
                        bulk = findViewById<TextInputLayout>(R.id.bulk).editText?.text.toString().toInt(),
                        is_retain = findViewById<TextInputLayout>(R.id.is_retain).editText?.text.toString().toBoolean(),
                        is_stock_room = findViewById<TextInputLayout>(R.id.is_stock_room).editText?.text.toString().toBoolean(),
                        sale_rate = findViewById<TextInputLayout>(R.id.sale_rate).editText?.text.toString().toInt(),
                        tier_quantity1 = findViewById<TextInputLayout>(R.id.tier_quantity1).editText?.text.toString().toInt(),
                        tier_price1 = findViewById<TextInputLayout>(R.id.tier_price1).editText?.text.toString().toFloat(),
                        tier_quantity2 = findViewById<TextInputLayout>(R.id.tier_quantity2).editText?.text.toString().toInt(),
                        tier_price2 = findViewById<TextInputLayout>(R.id.tier_price2).editText?.text.toString().toFloat(),
                        tier_quantity3 = findViewById<TextInputLayout>(R.id.tier_quantity3).editText?.text.toString().toInt(),
                        tier_price3 = findViewById<TextInputLayout>(R.id.tier_price3).editText?.text.toString().toFloat()

                    )


                    val gson = Gson()
                    val postBody = gson.toJson(body)
                    val listingResponse = apiService.apiPostRequest(postBody)
                    val listing = gson.fromJson(listingResponse, PostToShopResponseData::class.java)

                    if (listing.meta.code > 399) {
                        Toast.makeText(applicationContext, "Description: ${listing.meta.description} Message: ${listing.meta.message}", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(applicationContext, "LISTING_SUCCESSFUL", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@SellPage, MainActivity::class.java).apply {
                        }
                        val invID = listing.data.inventory_id
                        val url = "https://www.bricklink.com/imgAdd.asp?invid=${invID}"
                        println(url)
                        val intentURL = Intent(Intent.ACTION_VIEW)
                        intentURL.data = Uri.parse(url)
                        startActivity(intentURL)
                        startActivity(intent)
                    }
                }
            }
        }
        val startOverButton = findViewById<Button>(R.id.startOverButton)
        startOverButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
