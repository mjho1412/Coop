package com.hb.coop.data.repository

import com.hb.coop.data.api.request.vbd.DistanceParams
import com.hb.coop.data.api.request.vbd.FindShortestPathParams
import com.hb.coop.data.api.response.VBDResponse
import com.hb.lib.map.model.FindShortestPath
import com.hb.lib.map.model.GeoPoint
import com.hb.lib.map.model.POI
import io.reactivex.Observable

interface VietbandoRepository {

    fun whatHere(params: GeoPoint): Observable<VBDResponse<POI>>

    fun findShortestPath(params: FindShortestPathParams): Observable<VBDResponse<FindShortestPath>>

    fun autoSuggestSearch(params: String): Observable<VBDResponse<String>>

//    fun searchAll(keyword: String,
//                  page: Int = 1,
//                  pageSize: Int = 10,
//                  isOrder: Boolean = true,
//                  lx: Double? = null,
//                  ly: Double? = null,
//                  rx: Double? = null,
//                  ry: Double? = null): Observable<VBDResponse<POI>>

    fun searchAll(keyword: String,
                     cx: Double,
                     cy: Double,
                     radius: Int = 1000 * 1000,
                     page: Int = 1,
                     pageSize: Int = 10): Observable<VBDResponse<POI>>

    fun searchNearby(keyword: String,
                     cx: Double,
                     cy: Double,
                     radius: Int = 1000 * 1000,
                     page: Int = 1,
                     pageSize: Int = 10): Observable<VBDResponse<POI>>

    fun distance(params: DistanceParams): Observable<VBDResponse<List<List<Double>>>>
}