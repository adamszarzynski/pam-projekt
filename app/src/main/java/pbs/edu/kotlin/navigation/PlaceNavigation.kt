package pbs.edu.kotlin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pbs.edu.kotlin.screens.add.AddPlaceScreen
import pbs.edu.kotlin.screens.details.DetailsScreen
import pbs.edu.kotlin.screens.home.HomeScreen
import pbs.edu.kotlin.viewmodel.PlaceViewModel

@Composable
fun PlaceNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val viewModel: PlaceViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    NavHost(navController = navController, startDestination = PlaceScreens.HomeScreen.name) {

        composable(PlaceScreens.HomeScreen.name) {
            HomeScreen(navController = navController, viewModel = viewModel)
        }

        composable("AddPlaceScreen") {
            AddPlaceScreen(navController = navController, viewModel = viewModel)
        }

        composable(
            route = PlaceScreens.DetailsScreen.name + "/{placeId}",
            arguments = listOf(navArgument("placeId") { type = NavType.LongType })
        ) { backStackEntry ->
            val placeId = backStackEntry.arguments?.getLong("placeId")
            val place = viewModel.getPlace(placeId ?: 0)
            if(place != null) {
                DetailsScreen(navController = navController, place = place, viewModel = viewModel)
            }
        }

        composable(
            route = "EditPlaceScreen/{placeId}",
            arguments = listOf(navArgument("placeId") { type = NavType.LongType })
        ) { backStackEntry ->
            val placeId = backStackEntry.arguments?.getLong("placeId")
            if (placeId != null) {
                pbs.edu.kotlin.screens.edit.EditPlaceScreen(navController = navController, viewModel = viewModel, placeId = placeId)
            }
        }
    }
}