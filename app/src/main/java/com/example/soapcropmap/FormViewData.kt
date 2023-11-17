package com.example.soapcropmap

data class FormViewData(var x1: String, var x2: String, var x3: String, var x4: String) {
    fun mapToGpsModel(): GpsCoordinates? {
        return try {
            GpsCoordinates(
                x1.toDouble(),
                x2.toDouble(),
                x3.toDouble(),
                x4.toDouble(),
            )
        } catch (e: NumberFormatException) {
            null
        }
    }

    fun mapToPixelModel(): PixelCoordinates? {
        return try {
            PixelCoordinates(
                x1.toUInt().toInt(),
                x2.toUInt().toInt(),
                x3.toUInt().toInt(),
                x4.toUInt().toInt(),
            )
        } catch (e: NumberFormatException) {
            null
        }
    }
}