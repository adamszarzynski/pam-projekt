package pbs.edu.kotlin.screens.add

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pbs.edu.kotlin.viewmodel.PlaceViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlaceScreen(navController: NavController, viewModel: PlaceViewModel) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var tempUri by remember { mutableStateOf<Uri?>(null) }

    var addressText by remember { mutableStateOf("Brak lokalizacji") }

    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }


    fun getAddressFromLocation(lat: Double, lng: Double) {
        scope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(lat, lng, 1)

                withContext(Dispatchers.Main) {
                    if (!addresses.isNullOrEmpty()) {
                        val address = addresses[0]
                        val addressLine = address.getAddressLine(0) ?: "Nieznany adres"
                        addressText = addressLine
                    } else {
                        addressText = "Lat: $lat, Lng: $lng (Adres nieznany)"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    addressText = "Lat: $lat, Lng: $lng (Błąd geokodera)"
                }
            }
        }
    }

    fun createImageFile(): Uri {
        val file = File(context.getExternalFilesDir(null), "foto_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(context, "pbs.edu.kotlin.provider", file)
    }

    fun saveImageToInternalStorage(context: Context, uri: Uri): Uri? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val fileName = "gallery_${System.currentTimeMillis()}.jpg"
            val file = File(context.getExternalFilesDir(null), fileName)
            val outputStream = FileOutputStream(file)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            FileProvider.getUriForFile(context, "pbs.edu.kotlin.provider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && tempUri != null) {
            imageUri = tempUri
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = createImageFile()
            tempUri = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Brak zgody na kamerę", Toast.LENGTH_SHORT).show()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            val savedUri = saveImageToInternalStorage(context, uri)
            imageUri = savedUri
        }
    }

    val locationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                    if (loc != null) {
                        latitude = loc.latitude
                        longitude = loc.longitude
                        getAddressFromLocation(loc.latitude, loc.longitude)
                    } else {
                        Toast.makeText(context, "Włącz GPS!", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: SecurityException) { }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Utwórz miejsce") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wróć")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(value = title, onValueChange = { title = it }, label = { Text("Tytuł") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = description, onValueChange = { description = it }, label = { Text("Opis") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(16.dp))

            Text("Multimedia i Lokalizacja", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = {
                    val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    val uri = createImageFile()
                    tempUri = uri
                    if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                        cameraLauncher.launch(uri)
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }, modifier = Modifier.weight(1f).padding(end = 4.dp)) {
                    Text("Aparat")
                }

                Button(onClick = {
                    galleryLauncher.launch("image/*")
                }, modifier = Modifier.weight(1f).padding(horizontal = 4.dp)) {
                    Text("Galeria")
                }

                Button(onClick = {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        fusedLocationClient.getCurrentLocation(
                            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                            null
                        ).addOnSuccessListener { loc ->
                            if (loc != null) {
                                latitude = loc.latitude
                                longitude = loc.longitude
                                getAddressFromLocation(loc.latitude, loc.longitude)
                            } else {
                                Toast.makeText(context, "Nie udało się ustalić pozycji (spróbuj wyjść na zewnątrz)", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        locationLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                }, modifier = Modifier.weight(1f).padding(start = 4.dp)) {
                    Text("GPS")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (imageUri != null) {
                Card(
                    elevation = CardDefaults.cardElevation(4.dp),
                    modifier = Modifier.fillMaxWidth().height(300.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize().background(Color.LightGray)) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Podgląd",
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
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Brak zdjęcia", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            ) {
                Text(
                    text = "📍 $addressText",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                modifier = Modifier.fillMaxWidth().height(50.dp),
                onClick = {
                    if (title.isNotEmpty()) {
                        viewModel.addPlace(title, description, imageUri?.toString(), latitude, longitude, addressText)
                        navController.popBackStack()
                    } else {
                        Toast.makeText(context, "Podaj tytuł!", Toast.LENGTH_SHORT).show()
                    }
                }
            ) { Text("ZAPISZ") }
        }
    }
}