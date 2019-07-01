package com.hb.coop.data.store.vietbando

import com.hb.coop.BuildConfig
import com.hb.lib.map.model.FindShortestPath
import com.hb.lib.map.model.GeoPoint
import com.hb.lib.map.model.POI
import com.hb.coop.data.api.request.vbd.AutoSuggestSearchParams
import com.hb.coop.data.api.request.vbd.DistanceParams
import com.hb.coop.data.api.request.vbd.FindShortestPathParams
import com.hb.coop.data.api.request.vbd.SearchParams
import com.hb.coop.data.api.response.VBDResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface VietbandoStore {

    companion object {
        const val VERSION = "v2"
    }

    interface LocalStorage {
        fun clearRoute()

        fun setRoute(route: FindShortestPath)
    }


    interface RequestService {

        @POST("$VERSION/${BuildConfig.FLAVOR}/map/WhatHere")
        fun whatHere(@Body params: GeoPoint): Observable<VBDResponse<POI>>

        @POST("$VERSION/${BuildConfig.FLAVOR}/map/Route")
        fun findShortestPath(@Body params: FindShortestPathParams): Observable<VBDResponse<FindShortestPath>>

        @POST("$VERSION/${BuildConfig.FLAVOR}/map/AutoSuggestSearch")
        fun autoSuggestSearch(@Body params: AutoSuggestSearchParams): Observable<VBDResponse<String>>

        @POST("$VERSION/${BuildConfig.FLAVOR}/map/SearchAll")
        fun searchAll(@Body params: SearchParams): Observable<VBDResponse<POI>>

        @POST("$VERSION/${BuildConfig.FLAVOR}/map/SearchNearBy")
        fun searchNearby(@Body params: SearchParams): Observable<VBDResponse<POI>>

        @POST("$VERSION/${BuildConfig.FLAVOR}/map/Distance")
        fun distance(@Body params: DistanceParams): Observable<VBDResponse<List<List<Double>>>>
    }
}