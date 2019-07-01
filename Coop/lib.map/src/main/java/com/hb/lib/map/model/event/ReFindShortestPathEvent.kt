package com.hb.lib.map.model.event

import com.hb.lib.map.model.GeoPoint

data class ReFindShortestPathEvent(
    val startPoint: GeoPoint,
    val endPoint: GeoPoint
)