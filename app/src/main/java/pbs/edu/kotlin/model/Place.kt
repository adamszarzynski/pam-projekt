package pbs.edu.kotlin.model

data class Place(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val description: String,
    val imagePath: String?,
    val latitude: Double?,
    val longitude: Double?,
    val address: String?
)