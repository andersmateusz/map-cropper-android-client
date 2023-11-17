package com.example.soapcropmap

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.ksoap2.HeaderProperty
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.MarshalBase64
import org.ksoap2.serialization.MarshalFloat
import org.ksoap2.serialization.PropertyInfo
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpResponseException
import org.ksoap2.transport.HttpTransportSE
import org.ksoap2.transport.Transport
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


data class ImageInfo(val width: Int, val height: Int, val gpsCoordinates: GpsCoordinates)
data class GpsCoordinates(val lat1: Double, val lon1: Double, val lat2: Double, val lon2: Double)
data class PixelCoordinates(val x1: Int, val y1: Int, val x2: Int, val y2: Int)
class SoapClient() {
    private val namespace = "urn:MapCropper"
    private val url = "http://10.0.2.2:65089"
    private val host = "localhost:65089"
    private fun transportFactory(): Transport {
        return HttpTransportSE(url).apply {
            setXmlVersionTag("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
            debug = true
        }
    }

    private fun headersFactory(): List<HeaderProperty> {
        return mutableListOf(HeaderProperty("host", host))
    }

    private fun envelopeFactory(): SoapSerializationEnvelope {
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        MarshalFloat().register(envelope)
        return envelope
    }

    @OptIn(ExperimentalEncodingApi::class)
    suspend fun getImageByPixelCoordinates(coordinates: PixelCoordinates): Bitmap? {
        val methodName = "byPixelCoordinates"
        val envelope = envelopeFactory().apply {
            setOutputSoapObject(SoapObject(namespace, methodName)
                .addProperty(PropertyInfo().apply {
                    name = "x1"
                    value = coordinates.x1
                    type = Int::class.java
                })
                .addProperty(PropertyInfo().apply {
                    name = "y1"
                    value = coordinates.y1
                    type = Int::class.java
                })
                .addProperty(PropertyInfo().apply {
                    name = "x2"
                    value = coordinates.x2
                    type = Int::class.java
                })
                .addProperty(PropertyInfo().apply {
                    name = "y2"
                    value = coordinates.y2
                    type = Int::class.java
                })
            )
        }

        try {
            val trans = transportFactory()
            trans.call(namespace + methodName, envelope, headersFactory())
            val img = (envelope.bodyIn as SoapObject).getPropertyAsString("img") as CharSequence
            val decodedBytes = Base64.decode(img)
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun getImageByGpsCoordinates(coordinates: GpsCoordinates): Bitmap? {
        val methodName = "byGpsCoordinates"
        val envelope = envelopeFactory().apply {
            setOutputSoapObject(SoapObject(namespace, methodName)
                .addProperty(PropertyInfo().apply {
                    name = "lat1"
                    value = coordinates.lat1
                    type = Double::class.java
                })
                .addProperty(PropertyInfo().apply {
                    name = "lon1"
                    value = coordinates.lon1
                    type = Double::class.java
                })
                .addProperty(PropertyInfo().apply {
                    name = "lat2"
                    value = coordinates.lat2
                    type = Double::class.java
                })
                .addProperty(PropertyInfo().apply {
                    name = "lon2"
                    value = coordinates.lon2
                    type = Double::class.java
                })
            )
        }

        try {
            transportFactory().call(namespace + methodName, envelope, headersFactory())
            val img = (envelope.bodyIn as SoapObject).getPropertyAsString("img") as CharSequence
            val decodedBytes = Base64.decode(img)
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun imageInfo(): ImageInfo? {
        val methodName = "imageInfo"
        val envelope = envelopeFactory()
        envelope.setOutputSoapObject(SoapObject(namespace, methodName))
        try {
            transportFactory().call(namespace + methodName, envelope, headersFactory())
            val responseBody = envelope.bodyIn as SoapObject
            return ImageInfo(
                responseBody.getPropertyAsString("width").toInt(),
                responseBody.getPropertyAsString("height").toInt(),
                GpsCoordinates(
                    responseBody.getPropertyAsString("lat1").toDouble(),
                    responseBody.getPropertyAsString("lon1").toDouble(),
                    responseBody.getPropertyAsString("lat2").toDouble(),
                    responseBody.getPropertyAsString("lon2").toDouble(),
                ),
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }
}