package com.hb.lib.map.navigation.camera

import android.location.Location
import com.hb.lib.map.model.FindShortestPath
import com.hb.lib.map.route.NavigateManager

data class RouteInformation(
        val location: Location? = null,
        val route: FindShortestPath? = null,
        val routeProgress: NavigateManager? = null
)