package pbs.edu.kotlin.navigation

enum class PlaceScreens {
    HomeScreen,
    DetailsScreen;
    companion object {
        fun fromRoute(route: String?): PlaceScreens
                = when (route?.substringBefore("/")) {
            HomeScreen.name -> HomeScreen
            DetailsScreen.name -> DetailsScreen
            null -> HomeScreen
            else -> throw IllegalArgumentException("Route $route is not recognized")
        }
    }
}