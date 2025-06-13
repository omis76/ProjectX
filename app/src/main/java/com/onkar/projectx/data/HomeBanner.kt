package com.onkar.projectx.data

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
    val items: List<HomeItem>
)

enum class LayoutType {
    HORIZONTAL, GRID
}

data class HomeItem(
    val product: Product? = null,
    val subcategory: SubcategoryModel? = null,
    val generic: GenericItem? = null
)

data class GenericItem(
    val title: String,
    val image_url: String
)