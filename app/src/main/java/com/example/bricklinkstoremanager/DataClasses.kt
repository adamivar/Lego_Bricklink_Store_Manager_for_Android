package com.example.bricklinkstoremanager

import java.io.Serializable

data class APIResponseForItemInfo(
    val meta: Meta,
    val data: ItemInfo
)

data class APIResponseForSalesInfo(
    val meta: Meta,
    val data: SalesInfo
)

data class APIResponseForPricesInfo(
    val meta: Meta,
    val data: PricesInfo
)

data class APIResponseForSubsetInfo(
    val meta: Meta,
    val data: List<SubsetInfo>
)

data class APIResponseForColorInfo(
    val meta: Meta,
    val data: List<ColorQuantity>
)

data class APIResponseForStoreInfo(
    val meta: Meta,
    val data: List<InventoryInfo>
)

data class APIResponseForCategoryList(
    val meta: Meta,
    val data: List<Category>
)

data class Category(
    val category_id: Int,
    val category_name: String,
    val parent_id: Int
)

data class ColorQuantity(
    val color_id: Int,
    val quantity: Int
)

data class PostToShopResponseData(
    val meta: Meta,
    val data: InventoryInfo
)

data class InventoryInfo(
    val inventory_id: Long,
    val item: InventoryItem,
    val color_id: Int,
    val quantity: Int,
    val new_or_used: String,
    val unit_price: String,
    val bind_id: Int,
    val description: String,
    val remarks: String,
    val bulk: Int,
    val is_retain: Boolean,
    val is_stock_room: Boolean,
    val date_created: String,
    val my_cost: String,
    val sale_rate: Int,
    val tier_quantity1: Int,
    val tier_price1: String,
    val tier_quantity2: Int,
    val tier_price2: String,
    val tier_quantity3: Int,
    val tier_price3: String,
    val my_weight: String
)


data class Meta(
    val description: String,
    val message: String,
    val code: Int
)

data class ItemInfo(
    val no: String,
    val name: String,
    val type: String,
    val category_id: Int,
    val alternate_no: String?,
    val image_url: String,
    val thumbnail_url: String,
    val weight: Double,
    val dim_x: String,
    val dim_y: String,
    val dim_z: String,
    val year_released: Int,
    val description: String,
    val is_obsolete: Boolean,
    val language_code: String
)

class SaleDetail(
    val quantity: Int,
    val unit_price: String,
    val seller_country_code: String,
    val buyer_country_code: String,
    val date_ordered: String,  // You may need to parse the date string to a Date object
    val qunatity: Int  // Typo corrected to "quantity"
)

data class Item(
    val no: String,
    val type: String
)

data class InventoryItem(
    val no: String,
    val name: String,
    val type: String,
    val category_id: Int
)

data class SalesInfo(
    val item: Item,
    val new_or_used: String,
    val currency_code: String,
    val min_price: String,
    val max_price: String,
    val avg_price: String,
    val qty_avg_price: String,
    val unit_quantity: Int,
    val total_quantity: Int,
    val price_detail: List<SaleDetail>
)

data class PriceDetail(
    val quantity: Int,
    val unit_price: String,
    val shipping_available: Boolean,
    val qunatity: Int
)

data class PricesInfo(
    val item: Item,
    val new_or_used: String,
    val currency_code: String,
    val min_price: String,
    val max_price: String,
    val avg_price: String,
    val qty_avg_price: String,
    val unit_quantity: Int,
    val total_quantity: Int,
    val price_detail: List<PriceDetail>
)

data class SubsetItem(
    val no: String,
    val name: String,
    val type: String,
    val category_id: Int
)

data class SubsetEntry(
    val item: SubsetItem,
    val color_id: Int,
    val quantity: Int,
    val extra_quantity: Int,
    val is_alternate: Boolean,
    val is_counterpart: Boolean
)

data class SubsetInfo(
    val match_no: Int,
    val entries: List<SubsetEntry>
)



class InfoDict(
    var name: String = "undefined",
    var type: String = "undefined",
    var no: String = "0000-0",
    var color: String = "undefined",
    val category_id: Int = 0,
    val alternate_no: String = "undefined",
    var image_url: String = "undefined",
    val thumbnail_url: String = "undefined",
    var weight: Double = 00.00,
    var dim_x: String = "undefined",
    var dim_y: String = "undefined",
    var dim_z: String = "undefined",
    var year_released: Int = 1900,
    var description: String = "No description.",
    val is_obsolete: Boolean = false,
    val language_code: String = "undefined",
    var unique_parts: Int = 0,
    var total_parts: Int = 0,

    var est_price_per_part: Double = 00.00,
    var est_price_per_gram: Double = 00.00,
    var min_used_sale_price: Double = 00.00,
    var avg_used_sale_price: Double = 00.00,
    var qty_avg_used_sale_price: Double = 00.00,
    var max_used_sale_price: Double = 00.00,
    var avg_sales_per_month: Double = 00.00,
    var avg_parts_per_month: Double = 00.00,
    var avg_parts_per_sale: Double = 00.00,
    var volatility: Double = 00.00,
    var current_item_supply: Int = 0,
    var number_of_sellers: Int = 0,
    var listings_for_every_one_purchase: Double = 00.00,
    var sellers_for_every_one_buyer: Double = 00.00
):Serializable


data class PostValues(
    val item: Item,
    val color_id: Int,
    val quantity: Int,
    val unit_price: Float,
    val new_or_used: String,
    val completeness: String,
    val description: String,
    val remarks: String,
    val bulk: Int,
    val is_retain: Boolean,
    val is_stock_room: Boolean,
    val sale_rate: Int,
    val tier_quantity1: Int,
    val tier_price1: Float,
    val tier_quantity2: Int,
    val tier_price2: Float,
    val tier_quantity3: Int,
    val tier_price3: Float
)
