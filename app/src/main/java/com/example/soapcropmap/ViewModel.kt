package com.example.soapcropmap

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Constraints
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.properties.Delegates


class ViewModel: ViewModel() {
    private val _img = MutableLiveData<Bitmap?>()
    private val _fullImg = MutableLiveData<Bitmap?>()
    private val soapClient = SoapClient()
    private var imageInfo: ImageInfo? = null
    private var updateJob: Job? = null
    private val _isLoading = MutableLiveData(false)
    var maxGpsCoordinates: GpsCoordinates? = null
        private set
    var maxPixelCoordinates: PixelCoordinates? = null
        private set

    var gpsCoordinates: GpsCoordinates? = null
        set(value) {
            if (null != value) {
                field = value
                _gpsCoordinates = value
            }
        }
    var pixelCoordinates: PixelCoordinates? = null
        set(value) {
            if (null != value) {
                field = value
                _pixelCoordinates = value
            }
        }
    private var _gpsCoordinates: GpsCoordinates? by Delegates.observable(null) {
            _, old, new ->
            if (false == new?.equals(old)) this.updateImage(UnitType.COORDINATES) else return@observable
    }
    private var _pixelCoordinates: PixelCoordinates? by Delegates.observable(null) {
        _, old, new ->
        if (false == new?.equals(old)) this.updateImage(UnitType.PIXEL) else return@observable
    }
    val img: LiveData<Bitmap?> = _img
    val isLoading: LiveData<Boolean> = _isLoading
    var fullImg: LiveData<Bitmap?> = _fullImg

    init {
        viewModelScope.launch(Dispatchers.IO) {
            soapClient.imageInfo()?.let { info ->
                _isLoading.postValue(true)
                imageInfo = info
                val coordinates = PixelCoordinates(0, 0, info.width, info.height)
                maxPixelCoordinates = coordinates
                maxGpsCoordinates = info.gpsCoordinates
                val initImg = downloadImg(coordinates)
                _img.postValue(initImg)
                _fullImg.postValue(initImg)
                _isLoading.postValue(false)
            }
        }
    }

    private fun updateImage(unit: UnitType) {
        updateJob?.cancel()
        updateJob = viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            when (unit) {
                UnitType.PIXEL -> _pixelCoordinates?.let {c ->  downloadImg(c) }
                UnitType.COORDINATES -> _gpsCoordinates?.let { c -> downloadImg(c) }
            }?.let {i -> _img.postValue(i) }
            _isLoading.postValue(false)
        }
    }

    private suspend fun downloadImg(pixelCoordinates: PixelCoordinates): Bitmap? {
        return soapClient.getImageByPixelCoordinates(pixelCoordinates)
    }

    private suspend fun downloadImg(gpsCoordinates: GpsCoordinates): Bitmap? {
        return soapClient.getImageByGpsCoordinates(gpsCoordinates)
    }

    fun formGpsCoordinates(): GpsCoordinates? {
        return gpsCoordinates ?: maxGpsCoordinates
    }

    fun formPixelCoordinates(): PixelCoordinates? {
        return pixelCoordinates ?: maxPixelCoordinates
    }
}
