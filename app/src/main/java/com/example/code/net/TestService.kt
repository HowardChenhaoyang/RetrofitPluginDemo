package com.example.code.net

import retrofit2.http.GET
import rx.Observable

interface TestService {
    @GET()
    fun request(): Observable<ResponseBean>
}

class TestServiceImpl {
    companion object {
        fun request(): Observable<ResponseBean> {
            return RetrofitManager.retrofit.create(TestService::class.java)
                    .request()
                    .compose(RetrofitManager.threadSwitch())
        }
    }
}