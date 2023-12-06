package com.example.bricklinkstoremanager

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso

class ResultsPage : AppCompatActivity() {
    @SuppressLint("SetTextI18n", "MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results_page)

        // Retrieve the data from the intent's extras
        val infoDict = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("infoDict", InfoDict::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("infoDict") as? InfoDict
        }

        if (infoDict != null) {
            val firstLetter = infoDict.type.first()
            val image = "https://img.bricklink.com/ItemImage/${firstLetter}N/${infoDict.color}/${infoDict.no}.png"
            //val image = "https:" + infoDict.image_url
            Log.d("image", image)
            val resultImageView = findViewById<ImageView>(R.id.imageView2)
            // Load the image into the ImageView using Picasso
            Picasso.get().load(image).into(resultImageView)

            findViewById<TextView>(R.id.itemTitle)
                .text = "${infoDict.type}: ${infoDict.no} Color: ${infoDict.color}"
            findViewById<TextView>(R.id.itemName)
                .text = infoDict.name
            findViewById<TextView>(R.id.itemWeight)
                .text = "${infoDict.weight} grams"
            findViewById<TextView>(R.id.itemSize)
                .text = "${infoDict.dim_x} x ${infoDict.dim_y} x ${infoDict.dim_z}"
            findViewById<TextView>(R.id.itemYear)
                .text = "Released in ${infoDict.year_released}"
            findViewById<TextView>(R.id.itemParts)
                .text = "${infoDict.total_parts} parts | ${infoDict.unique_parts} uniques"
            findViewById<TextView>(R.id.itemDesc)
                .text = infoDict.description

            // Scroller
            findViewById<TextView>(R.id.est_price_per_part_ans)
                .text = if (infoDict.est_price_per_part != 0.0) "$${infoDict.est_price_per_part}" else "---"

            findViewById<TextView>(R.id.est_price_per_gram_ans)
                .text = if (infoDict.est_price_per_gram != 0.0) "$${infoDict.est_price_per_gram}" else "---"

            findViewById<TextView>(R.id.min_used_sale_price_ans)
                .text = if (infoDict.min_used_sale_price != 0.0) "$${infoDict.min_used_sale_price}" else "---"


            findViewById<TextView>(R.id.avg_used_sale_price_ans)
                .text = if (infoDict.avg_used_sale_price != 0.0) "$${infoDict.avg_used_sale_price}" else "---"

            findViewById<TextView>(R.id.qty_avg_used_sale_price_ans)
                .text = if (infoDict.qty_avg_used_sale_price != 0.0) "$${infoDict.qty_avg_used_sale_price}" else "---"

            findViewById<TextView>(R.id.max_used_sale_price_ans)
                .text = if (infoDict.max_used_sale_price != 0.0) "$${infoDict.max_used_sale_price}" else "---"

            findViewById<TextView>(R.id.avg_sales_per_month_ans)
                .text = if (infoDict.avg_sales_per_month != 0.0) "${infoDict.avg_sales_per_month}" else "---"

            findViewById<TextView>(R.id.avg_parts_per_month_ans)
                .text = if (infoDict.avg_parts_per_month != 0.0) "${infoDict.avg_parts_per_month}" else "---"

            findViewById<TextView>(R.id.avg_parts_per_sale_ans)
                .text = if (infoDict.avg_parts_per_sale != 0.0) "${infoDict.avg_parts_per_sale}" else "---"

            findViewById<TextView>(R.id.volatility_ans)
                .text = if (infoDict.volatility != 0.0) "${(infoDict.volatility * 100).toInt()} %" else "---"

            findViewById<TextView>(R.id.current_item_supply_ans)
                .text = infoDict.current_item_supply.toString()

            findViewById<TextView>(R.id.number_of_sellers_ans)
                .text = infoDict.number_of_sellers.toString()

            findViewById<TextView>(R.id.listings_for_every_one_purchase_ans)
                .text = if (infoDict.listings_for_every_one_purchase.toString() != "Infinity") "${infoDict.listings_for_every_one_purchase}" else "---"

            findViewById<TextView>(R.id.sellers_for_every_one_buyer_ans)
                .text = if (infoDict.sellers_for_every_one_buyer.toString() != "Infinity") "${infoDict.sellers_for_every_one_buyer}" else "---"

        }

        val button = findViewById<Button>(R.id.startListingButton)
        button.setOnClickListener {
            val intent = Intent(this@ResultsPage, SellPage::class.java).apply {
                putExtra("infoDict", infoDict)
            }
            startActivity(intent)
        }


    }



}
