package pbs.edu.kotlin.screens.details

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Edit
import androidx.navigation.NavController
import coil.compose.AsyncImage
import pbs.edu.kotlin.model.Place
import pbs.edu.kotlin.viewmodel.PlaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(navController: NavController, place: Place, viewModel: PlaceViewModel) {
    val context = LocalContext.current

    fun sharePlace() {
        val shareText = buildString {
            append("Tytuł: ${place.title}\n")
            append("Opis: ${place.description}\n")
            if (!place.address.isNullOrEmpty()) {
                append("Adres: ${place.address}\n")
            }
            append("Mapa: http://maps.google.com/?q=${place.latitude},${place.longitude}")
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)

            if (place.imagePath != null) {
                val imageUri = Uri.parse(place.imagePath)
                putExtra(Intent.EXTRA_STREAM, imageUri)
                type = "image/jpeg"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } else {
                type = "text/plain"
            }
        }

        val shareIntent = Intent.createChooser(sendIntent, "Udostępnij miejsce")
        context.startActivity(shareIntent)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(place.title) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Wróć")
                    }
                },
                actions = {
                    IconButton(onClick = { sharePlace() }) {
                        Icon(Icons.Default.Share, "Udostępnij")
                    }
                    IconButton(onClick = {
                        navController.navigate("EditPlaceScreen/${place.id}")
                    }) {
                        Icon(Icons.Default.Edit, "Edytuj")
                    }
                    IconButton(onClick = {
                        viewModel.deletePlace(place.id)
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.Delete, "Usuń")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (place.imagePath != null) {
                Card(
                    elevation = CardDefaults.cardElevation(4.dp),
                    modifier = Modifier.fillMaxWidth().height(350.dp).padding(bottom = 16.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize().background(Color.LightGray)) {
                        AsyncImage(
                            model = Uri.parse(place.imagePath),
                            contentDescription = "Zdjęcie miejsca",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Brak zdjęcia", style = MaterialTheme.typography.bodyLarge)
                }
            }

            Text(text = place.title, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = place.description, style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(24.dp))

            if (place.latitude != null && place.longitude != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "📍 Lokalizacja", style = MaterialTheme.typography.titleSmall)
                        Spacer(modifier = Modifier.height(4.dp))

                        if (!place.address.isNullOrEmpty()) {
                            Text(text = place.address, style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        Text(text = "GPS: ${place.latitude}, ${place.longitude}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                try {
                                    val uri = Uri.parse("geo:0,0?q=${place.latitude},${place.longitude}(${place.title})")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                                    mapIntent.setPackage("com.google.android.apps.maps")

                                    if (mapIntent.resolveActivity(context.packageManager) != null) {
                                        context.startActivity(mapIntent)
                                    } else {
                                        val browserUri = Uri.parse("http://maps.google.com/?q=${place.latitude},${place.longitude}")
                                        val browserIntent = Intent(Intent.ACTION_VIEW, browserUri)
                                        context.startActivity(browserIntent)
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Nie znaleziono aplikacji map", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("OTWÓRZ W GOOGLE MAPS")
                        }
                    }
                }
            }
        }
    }
}