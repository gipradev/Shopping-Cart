package com.gipra.vicibshoppy.VicibShoppy.models

import com.gipra.vicibshoppy.R


data class ImagesData(var image: Int)

object suppler {

    val images = listOf(
        ImagesData(R.drawable.one),
        ImagesData(R.drawable.two),
        ImagesData(R.drawable.zero)
    )


}
