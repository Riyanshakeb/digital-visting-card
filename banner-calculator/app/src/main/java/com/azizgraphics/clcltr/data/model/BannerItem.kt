package com.azizgraphics.clcltr.data.model

data class BannerItem(
    var width: Double = 0.0,
    var widthUnit: String = "Feet",
    var height: Double = 0.0,
    var heightUnit: String = "Feet",
    var quantity: Int = 1,
    var ratePerSqft: Double = 0.0,
    var material: String = ""
) {
    val widthFt: Double get() = if (widthUnit == "Inches") width / 12.0 else width
    val heightFt: Double get() = if (heightUnit == "Inches") height / 12.0 else height
    val sqft: Double get() = widthFt * heightFt
    val totalArea: Double get() = sqft * quantity
    val totalPrice: Double get() = totalArea * ratePerSqft

    fun dimensionLabel(): String {
        val wFt = widthFt.toInt()
        val wIn = ((widthFt - wFt) * 12).toInt()
        val hFt = heightFt.toInt()
        val hIn = ((heightFt - hFt) * 12).toInt()
        val ws = if (wIn > 0) "${wFt}'${wIn}\"" else "${wFt}'"
        val hs = if (hIn > 0) "${hFt}'${hIn}\"" else "${hFt}'"
        return "$ws × $hs"
    }
}
