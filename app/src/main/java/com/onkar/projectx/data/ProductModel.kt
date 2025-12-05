package com.onkar.projectx.data

import com.google.gson.annotations.SerializedName

data class CatalogResponse(
    @SerializedName("city_id")
    val cityId: Int? = null, // Ignored, just for parsing
    @SerializedName("city_name")
    val cityName: String? = null, // Ignored, just for parsing
    val categories: List<CategoryModel>,
    val error: String? = null
)

data class CategoryModel(
    val id: String,
    val name: String,
    val image: String? = null,
    val description: String? = null, // Made optional since new API doesn't have it
    val subcategories: List<SubcategoryModel>
)

data class SubcategoryModel(
    val id: String,
    val name: String,
    val image: String,
    val description: String? = null, // Made optional since new API doesn't have it
    val products: List<Product> = emptyList() // New: products are now nested here
)

data class Product(
    val id: String,
    var subcategoryId: Int? = null, // Will be set during transformation
    val image: String,
    val name: String,
    @SerializedName("unit_size")
    val unitSize: String,
    @SerializedName("unit_type")
    val unitType: String,
    @SerializedName("unit_text")
    val unitText: String,
    @SerializedName("delivery_text")
    val deliveryText: String,
    val mrp: Double, // Changed to String to match API response
    @SerializedName("selling_price")
    val sellingPrice: Double, // Changed to String to match API response
    @SerializedName("max_order")
    val maxOrder: Int? = null,
    @SerializedName("top_label_text")
    val topLabelText: String? = null,
    @SerializedName("bottom_label_text")
    val bottomLabelText: String? = null,
    // Legacy fields for compatibility
    val offerPrice: Double? = null,
    val reasons: List<Reason>? = emptyList(),
    val description: String? = null,
    val offer: Offer? = null,
    var quantity: Int = 0
)

data class Reason(
    val reasonText: String, // Reason text
    val reasonImage: String // URL or path to the reason image
)

data class Offer(
    val id: String,
    val offerText: String, // Text describing the offer (e.g., "Buy 1 Get 1 Free")
    val offerPercentage: Double, // Discount percentage (e.g., 10.0 for 10%)
    val offerPrice: Double // Discounted price after applying the offer
)

// Helper extensions to convert String prices to Double for calculations
fun Product.getMrpAsDouble(): Double = mrp
fun Product.getSellingPriceAsDouble(): Double = sellingPrice

// Commented out test data generation - needs update for new data model
/*
fun generateFakeCategoriesAndProducts(): Pair<List<CategoryModel>, List<Product>> {
    val reasons = listOf(
        Reason("Fresh from farm", "https://picsum.photos/200"),
        Reason("Premium quality", "https://picsum.photos/200"),
        Reason("Sustainably raised", "https://picsum.photos/200")
    )

    val offers = listOf(
        Offer("offer_1", "10% off", 10.0, 200.0),
        Offer("offer_2", "Buy 1 Get 1 Free", 0.0, 150.0),
        Offer("offer_3", "15% off", 15.0, 180.0)
    )

    // ----- Create Subcategories -----

    val meatSubcategories = listOf(
        SubcategoryModel(
            1,
            "Chicken",
            "https://picsum.photos/200",
            "Fresh chicken cuts"
        ),
        SubcategoryModel(
            2,
            "Mutton",
            "https://picsum.photos/200",
            "Tender mutton meat"
        ),
        SubcategoryModel(
            3,
            "Pork",
            "https://picsum.photos/200",
            "Juicy pork selections"
        ),
        SubcategoryModel(4, "Beef", "https://picsum.photos/200", "Premium beef cuts"),
        SubcategoryModel(
            5,
            "Turkey",
            "https://picsum.photos/200",
            "Delicious turkey options"
        ),
        SubcategoryModel(
            6,
            "Duck",
            "https://picsum.photos/200",
            "Farm-raised duck meat"
        ),
        SubcategoryModel(7, "Goat", "https://picsum.photos/200", "Fresh goat meat"),
        SubcategoryModel(8, "Rabbit", "https://picsum.photos/200", "Lean rabbit meat")
    )

    val dairySubcategories = listOf(
        SubcategoryModel("subcat_dairy_1", "Milk", "Fresh dairy milk", "https://picsum.photos/200"),
        SubcategoryModel(
            "subcat_dairy_2",
            "Cheese",
            "Variety of cheeses",
            "https://picsum.photos/200"
        ),
        SubcategoryModel("subcat_dairy_3", "Butter", "Premium butter", "https://picsum.photos/200"),
        SubcategoryModel("subcat_dairy_4", "Yogurt", "Healthy yogurt", "https://picsum.photos/200")
    )

    val fruitsSubcategories = listOf(
        SubcategoryModel("subcat_fruit_1", "Apples", "Fresh apples", "https://picsum.photos/200"),
        SubcategoryModel("subcat_fruit_2", "Bananas", "Ripe bananas", "https://picsum.photos/200"),
        SubcategoryModel("subcat_fruit_3", "Oranges", "Juicy oranges", "https://picsum.photos/200"),
        SubcategoryModel(
            "subcat_fruit_4",
            "Grapes",
            "Seedless grapes",
            "https://picsum.photos/200"
        ),
        SubcategoryModel("subcat_fruit_5", "Mangoes", "Sweet mangoes", "https://picsum.photos/200"),
        SubcategoryModel("subcat_fruit_6", "Berries", "Mixed berries", "https://picsum.photos/200"),
        SubcategoryModel(
            "subcat_fruit_7",
            "Pineapple",
            "Fresh pineapple",
            "https://picsum.photos/200"
        ),
        SubcategoryModel(
            "subcat_fruit_8",
            "Watermelon",
            "Juicy watermelon",
            "https://picsum.photos/200"
        ),
        SubcategoryModel("subcat_fruit_10", "Apples", "Fresh apples", "https://picsum.photos/200"),
        SubcategoryModel("subcat_fruit_20", "Bananas", "Ripe bananas", "https://picsum.photos/200"),
        SubcategoryModel(
            "subcat_fruit_30",
            "Oranges",
            "Juicy oranges",
            "https://picsum.photos/200"
        ),
        SubcategoryModel(
            "subcat_fruit_40",
            "Grapes",
            "Seedless grapes",
            "https://picsum.photos/200"
        ),
        SubcategoryModel(
            "subcat_fruit_50",
            "Mangoes",
            "Sweet mangoes",
            "https://picsum.photos/200"
        ),
        SubcategoryModel(
            "subcat_fruit_60",
            "Berries",
            "Mixed berries",
            "https://picsum.photos/200"
        ),
        SubcategoryModel(
            "subcat_fruit_70",
            "Pineapple",
            "Fresh pineapple",
            "https://picsum.photos/200"
        ),
        SubcategoryModel(
            "subcat_fruit_80",
            "Watermelon",
            "Juicy watermelon",
            "https://picsum.photos/200"
        )
    )

    val vegetablesSubcategories = listOf(
        SubcategoryModel("subcat_veg_1", "Tomatoes", "Fresh tomatoes", "https://picsum.photos/200"),
        SubcategoryModel(
            "subcat_veg_2",
            "Potatoes",
            "Organic potatoes",
            "https://picsum.photos/200"
        ),
        SubcategoryModel("subcat_veg_3", "Onions", "Red onions", "https://picsum.photos/200"),
        SubcategoryModel("subcat_veg_4", "Carrots", "Crunchy carrots", "https://picsum.photos/200"),
        SubcategoryModel(
            "subcat_veg_5",
            "Broccoli",
            "Healthy broccoli",
            "https://picsum.photos/200"
        )
    )

    // ----- Create Categories -----

    val categories = listOf(
        CategoryModel(
            "cat_meat",
            "Meat & Poultry",
            "Fresh meat and poultry products",
            meatSubcategories
        ),
        CategoryModel("cat_dairy", "Dairy Products", "Fresh dairy goods", dairySubcategories),
        CategoryModel("cat_fruits", "Fruits", "Seasonal and exotic fruits", fruitsSubcategories),
        CategoryModel(
            "cat_vegetables",
            "Vegetables",
            "Farm fresh vegetables",
            vegetablesSubcategories
        )
    )

    // ----- Create Products -----

    val productNames = listOf(
        "Chicken Breast", "Mutton Leg", "Pork Ribs", "Beef Steak", "Turkey Meat",
        "Milk Pack", "Cheddar Cheese", "Salted Butter", "Greek Yogurt",
        "Apple Gala", "Banana Cavendish", "Orange Valencia", "Grapes Seedless",
        "Tomato Roma", "Potato Yukon", "Onion Red", "Carrot Organic"
    )

    val unitSizes = listOf("500g", "1kg", "750g", "1.5kg", "250g", "2L", "500ml")
    val unitTypes = listOf("kg", "g", "ltr", "ml")
    val deliveryTexts =
        listOf("Delivered in 2-3 days", "Same-day delivery", "Shipped within 24 hours")
    val descriptions = listOf(
        "High quality product sourced from premium farms.",
        "Tender and juicy selections for your delicious meals.",
        "Freshly harvested, packed with nutrition.",
        "Perfect for everyday healthy cooking."
    )
    val images = listOf(
        "https://picsum.photos/200", "https://picsum.photos/200", "https://picsum.photos/200",
        "https://picsum.photos/200", "https://picsum.photos/200", "https://picsum.photos/200",
        "https://picsum.photos/200", "https://picsum.photos/200", "https://picsum.photos/200",
        "https://picsum.photos/200", "https://picsum.photos/200", "https://picsum.photos/200",
        "https://picsum.photos/200", "https://picsum.photos/200", "https://picsum.photos/200",
        "https://picsum.photos/200", "https://picsum.photos/200"
    )

    val subcategories =
        meatSubcategories + dairySubcategories + fruitsSubcategories + vegetablesSubcategories

    val products = List(20) { index ->
        val subcategory = subcategories[index % subcategories.size]
        Product(
            id = "prod_${index + 1}",
            subcategoryId = subcategory.id,
            image = images[index % images.size],
            name = productNames[index % productNames.size],
            unitSize = unitSizes[index % unitSizes.size],
            unitType = unitTypes[index % unitTypes.size],
            unitText = "Freshly sourced",
            mrp = 400.0 + index * 10,
            sellingPrice = 350.0 + index * 10,
            offerPrice = 300.0 + index * 10,
            deliveryText = deliveryTexts[index % deliveryTexts.size],
            reasons = reasons.shuffled().take(2),
            description = descriptions[index % descriptions.size],
            offer = offers[index % offers.size]
        )
    }

    return Pair(categories, products)
}
*/