package com.gipra.vicibshoppy.models

data class CategoryModel(var category: String)

object catSupplier {

    val  category = listOf(
        CategoryModel("Fruits"),
        CategoryModel("Vegetables"),
        CategoryModel("Electronics"),
        CategoryModel("Mobiles"),
        CategoryModel("Laptops"),
        CategoryModel("Head Phones")
    )


}
