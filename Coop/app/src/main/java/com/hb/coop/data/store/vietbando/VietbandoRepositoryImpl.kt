package com.hb.coop.data.store.vietbando

import com.hb.coop.data.api.request.vbd.AutoSuggestSearchParams
import com.hb.coop.data.api.request.vbd.DistanceParams
import com.hb.coop.data.api.request.vbd.FindShortestPathParams
import com.hb.coop.data.api.request.vbd.SearchParams
import com.hb.coop.data.api.response.VBDResponse
import com.hb.coop.data.repository.VietbandoRepository
import com.hb.lib.map.model.FindShortestPath
import com.hb.lib.map.model.GeoPoint
import com.hb.lib.map.model.POI
import com.mapbox.mapboxsdk.geometry.LatLng
import io.reactivex.Observable

class VietbandoRepositoryImpl(
    private val storage: VietbandoStore.LocalStorage,
    private val service: VietbandoStore.RequestService
) : VietbandoRepository {

    private var fsp: FindShortestPathParams? = null

    override fun whatHere(params: GeoPoint): Observable<VBDResponse<POI>> {
        return service.whatHere(params)
    }

    override fun findShortestPath(params: FindShortestPathParams): Observable<VBDResponse<FindShortestPath>> {
        return service
                .findShortestPath(params = params)
                .doOnNext {
                    val value = it.value!!
                    value.startPoint = LatLng(
                            params.points!![0].latitude,
                            params.points!![0].longitude
                    )
                    value.endPoint = LatLng(
                            params.points!![1].latitude,
                            params.points!![1].longitude
                    )
                    storage.setRoute(value)
                }
                .doOnError {
                    storage.clearRoute()
                }
    }

    override fun autoSuggestSearch(params: String): Observable<VBDResponse<String>> {
        val p = AutoSuggestSearchParams(params)
        return service.autoSuggestSearch(params = p)
    }

//    override fun searchAll(keyword: String, page: Int, pageSize: Int, isOrder: Boolean, lx: Double?, ly: Double?, rx: Double?, ry: Double?): Observable<VBDResponse<POI>> {
//        val params = SearchParams(
//                keyword = keyword,
//                page = page,
//                pageSize = pageSize,
//                isOrder = isOrder,
//                lx = lx, ly = ly,
//                rx = rx, ry = ry
//        )
//        return service.searchAll(params = params)
//    }

    override fun searchAll(keyword: String, cx: Double, cy: Double, radius: Int, page: Int, pageSize: Int): Observable<VBDResponse<POI>> {
        val params = SearchParams(
                keyword = keyword,
                point = SearchParams.newPoint(lat = cy, lon = cx),
                page = 1,
                pageSize = 10,
                radius = radius
        )
        return service.searchAll(params)

    }

    override fun searchNearby(keyword: String, cx: Double, cy: Double, radius: Int, page: Int, pageSize: Int): Observable<VBDResponse<POI>> {
        val params = SearchParams(
                keyword = keyword,
                point = SearchParams.newPoint(lat = cy, lon = cx),
                page = 1,
                pageSize = 10,
                radius = radius
        )
        return service.searchNearby(params)
    }

    override fun distance(params: DistanceParams): Observable<VBDResponse<List<List<Double>>>> {
        return service.distance(params)
    }
}