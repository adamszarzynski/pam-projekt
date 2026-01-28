package pbs.edu.kotlin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pbs.edu.kotlin.model.Place
import java.io.File

class PlaceViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private val gson = Gson()
    private val fileName = "places_database.json"

    private val _places = MutableStateFlow<List<Place>>(emptyList())
    val places = _places.asStateFlow()

    init {
        loadPlaces()
    }

    fun addPlace(title: String, description: String, imagePath: String?, lat: Double?, lng: Double?, address: String?) {
        val newPlace = Place(
            title = title,
            description = description,
            imagePath = imagePath,
            latitude = lat,
            longitude = lng,
            address = address
        )

        val currentList = _places.value.toMutableList()
        currentList.add(0, newPlace)
        _places.value = currentList
        savePlaces()
    }

    fun updatePlace(id: Long, title: String, description: String, imagePath: String?, lat: Double?, lng: Double?, address: String?) {
        val currentList = _places.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            val updatedPlace = currentList[index].copy(
                title = title,
                description = description,
                imagePath = imagePath,
                latitude = lat,
                longitude = lng,
                address = address
            )
            currentList[index] = updatedPlace
            _places.value = currentList
            savePlaces()
        }
    }

    fun deletePlace(placeId: Long) {
        val currentList = _places.value.toMutableList()
        currentList.removeIf { it.id == placeId }
        _places.value = currentList
        savePlaces()
    }

    fun getPlace(placeId: Long): Place? {
        return _places.value.find { it.id == placeId }
    }

    private fun savePlaces() {
        viewModelScope.launch {
            val jsonString = gson.toJson(_places.value)
            val file = File(context.filesDir, fileName)
            file.writeText(jsonString)
        }
    }

    private fun loadPlaces() {
        val file = File(context.filesDir, fileName)
        if (file.exists()) {
            val jsonString = file.readText()
            val type = object : TypeToken<List<Place>>() {}.type
            _places.value = gson.fromJson(jsonString, type) ?: emptyList()
        }
    }
}