package pbs.edu.kotlin.screens.home

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import pbs.edu.kotlin.model.Place
import pbs.edu.kotlin.navigation.PlaceScreens
import pbs.edu.kotlin.viewmodel.PlaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: PlaceViewModel) {
    val places by viewModel.places.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    val filteredPlaces = if (searchQuery.isEmpty()) {
        places
    } else {
        places.filter { place ->
            place.title.contains(searchQuery, ignoreCase = true) ||
                    place.description.contains(searchQuery, ignoreCase = true) ||
                    (place.address?.contains(searchQuery, ignoreCase = true) == true)
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Moje Miejsca") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("AddPlaceScreen") }) {
                Icon(Icons.Default.Add, contentDescription = "Dodaj")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Szukaj") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Wyczyść")
                        }
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredPlaces) { place ->
                    PlaceRow(place = place) {
                        navController.navigate(PlaceScreens.DetailsScreen.name + "/${place.id}")
                    }
                }
            }
        }
    }
}

@Composable
fun PlaceRow(place: Place, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            if (place.imagePath != null) {
                AsyncImage(
                    model = Uri.parse(place.imagePath),
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .padding(end = 16.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(modifier = Modifier.size(80.dp).padding(end = 16.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = place.title,
                    style = MaterialTheme.typography.titleMedium
                )

                if (place.description.isNotEmpty()) {
                    Text(
                        text = place.description,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        color = Color.DarkGray
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                val locationText = when {
                    !place.address.isNullOrEmpty() -> "📍 ${place.address}"
                    place.latitude != null -> "📍 ${place.latitude}, ${place.longitude}"
                    else -> null
                }

                if (locationText != null) {
                    Text(
                        text = locationText,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }
            }
        }
    }
}