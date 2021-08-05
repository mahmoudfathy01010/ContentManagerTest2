/*
 * created by Silvana Podaras
 * © NOUS Wissensmanagement GmbH, 2019
 */

package com.nousdigital.ngcontentmanager.data.downloader;

/*
 * created by Silvana Podaras
 * © NOUS Wissensmanagement GmbH, 2019
 */

import com.nousdigital.ngcontentmanager.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val jwtApi: String, private val jwtSyncServer: String?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        var request = chain.request().newBuilder()
            .header("nc-channel", BuildConfig.NC_CHANNEL)

        //Add jwt for api
        request.header("nc-authentication", "Bearer $jwtApi")

        //Add jwt for sync server if applicable (only for inhouse not null)
        if(jwtSyncServer !=null){
            request.header("Authorization", "Bearer $jwtSyncServer")
        }

        return chain.proceed(request.build())
    }

}