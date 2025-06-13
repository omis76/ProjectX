package com.onkar.projectx.data


data class CatalogResponse(
    val categories: List<CategoryModel>,
    val products: List<Product>,
    val error: String?
)

data class CategoryModel(
    val id: String,
    val name: String,
    val description: String,
    val subcategories: List<SubcategoryModel>
)

data class SubcategoryModel(
    val id: String,
    val name: String,
    val description: String,
    val image: String // URL or path to the subcategory image
)

data class Product(
    val id: String,
    val subcategoryId: String,
    val image: String, // URL or path to the product image
    val name: String, // Name of the product
    val unitSize: String, // The size of the unit (e.g., "500g", "1L")
    val unitType: String, // The type of unit (e.g., "kg", "ltr")
    val unitText: String, // Text that explains the unit (optional)
    val mrp: Double, // MRP of the product
    val sellingPrice: Double, // Selling price of the product
    val offerPrice: Double, // Offer price of the product (if any)
    val deliveryText: String, // Delivery-related text (e.g., "Delivered in 2-3 days")
    val reasons: List<Reason>, // List of reasons (strings and images)
    val description: String, // Product description
    val offer: Offer?, // Offer details (offer text, percentage, and price)
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
            "subcat_meat_1",
            "Chicken",
            "Fresh chicken cuts",
            "https://picsum.photos/200"
        ),
        SubcategoryModel(
            "subcat_meat_2",
            "Mutton",
            "Tender mutton meat",
            "https://picsum.photos/200"
        ),
        SubcategoryModel(
            "subcat_meat_3",
            "Pork",
            "Juicy pork selections",
            "https://picsum.photos/200"
        ),
        SubcategoryModel("subcat_meat_4", "Beef", "Premium beef cuts", "https://picsum.photos/200"),
        SubcategoryModel(
            "subcat_meat_5",
            "Turkey",
            "Delicious turkey options",
            "https://picsum.photos/200"
        ),
        SubcategoryModel(
            "subcat_meat_6",
            "Duck",
            "Farm-raised duck meat",
            "https://picsum.photos/200"
        ),
        SubcategoryModel("subcat_meat_7", "Goat", "Fresh goat meat", "https://picsum.photos/200"),
        SubcategoryModel("subcat_meat_8", "Rabbit", "Lean rabbit meat", "https://picsum.photos/200")
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