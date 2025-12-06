package com.onkar.projectx.data

import com.google.gson.annotations.SerializedName

// New Dashboard API Response
data class DashboardResponse(
    @SerializedName("city_id")
    val cityId: Int? = null,
    @SerializedName("city_name")
    val cityName: String? = null,
    @SerializedName("header_text")
    val headerText: String? = null,
    val items: List<DashboardSection>? = null
)

data class DashboardSection(
    val id: Int,
    val title: String,
    val subtitle: String? = null,
    val type: String, // FULL_BANNER, PRODUCT, SUBCATEGORY
    @SerializedName("layout_type")
    val layoutType: String, // HORIZONTAL, GRID
    @SerializedName("span_count")
    val spanCount: Int? = null,
    val sequence: Int,
    @SerializedName("sub_items")
    val subItems: List<DashboardSubItem>? = null
)

data class DashboardSubItem(
    val id: Int,
    @SerializedName("product_id")
    val productId: Int? = null,
    @SerializedName("subcategory_id")
    val subcategoryId: Int? = null,
    @SerializedName("generic_text")
    val genericText: String? = null,
    @SerializedName("generic_image_url")
    val genericImageUrl: String? = null,
    @SerializedName("generic_action")
    val genericAction: String? = null,
    @SerializedName("generic_action_parameter")
    val genericActionParameter: String? = null,
    val sequence: Int
)

// Legacy models kept for compatibility
data class HomeResponse(
    val banners: List<Banner>,
    val items: List<HomeSection>
)

data class UserDashboardResponse(
    val address_line1: String,
    val address_full: String,
    val wallet_balance: String
)

data class Banner(
    val title: String,
    val subtitle: String,
    val image_url: String
)

data class HomeSection(
    val title: String,
    val subtitle: String,
    val layoutType: LayoutType,
    val items: List<HomeItem>,
    val spanCount: Int = 4 // Added for dynamic grid columns
)

enum class LayoutType {
    HORIZONTAL, GRID
}

enum class SectionType {
    FULL_BANNER, PRODUCT, SUBCATEGORY, GENERIC
}

data class HomeItem(
    val product: Product? = null,
    val subcategory: SubcategoryModel? = null,
    val generic: GenericItem? = null
)

data class GenericItem(
    val title: String,
    val image_url: String,
    val action: String? = null,
    val actionParameter: String? = null
)