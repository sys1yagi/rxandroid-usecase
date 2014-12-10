package com.sys1yagi.rxandroid.retrofit

import com.sys1yagi.rxandroid.models.Item
import retrofit.http.GET
import rx.Observable

interface AndroidArsenalService {

    @GET("/Android_Arsenal")
    Observable<List<Item>> androidArsenal();
}
