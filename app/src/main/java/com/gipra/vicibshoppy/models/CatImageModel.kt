package com.gipra.vicibshoppy.models

import com.gipra.vicibshoppy.R

class CatImageModel(var imagesCategory: Int) {

    object suppler {

        val imagesCategory = listOf(
            CatImageModel(R.drawable.orange),
            CatImageModel(R.drawable.berry),
            CatImageModel(R.drawable.water),
            CatImageModel(R.drawable.apple),
            CatImageModel(R.drawable.apple),
            CatImageModel(R.drawable.apple)
        )
    }
}