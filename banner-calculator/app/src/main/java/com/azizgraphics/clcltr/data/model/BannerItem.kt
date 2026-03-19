package com.azizgraphics.clcltr.data.model

data class BannerItem(
    var width: Double = 0.0,
    var widthUnit: String = "Feet",
    var height: Double = 0.0,
    var heightUnit: String = "Feet",
    var quantity: Int = 1,
    var pricePerSqft: Double = 0.0
) {
    val widthInFeet: Double
        get() = if (widthUnit == "Inches") width / 12.0 else width

    val heightInFeet: Double
        get() = if (heightUnit == "Inches") height / 12.0 else height

    val areaSqft: Double
        get() = widthInFeet * heightInFeet

    val totalArea: Double
        get() = areaSqft * quantity

    val totalPrice: Double
        get() = totalArea * pricePerSqft
}
