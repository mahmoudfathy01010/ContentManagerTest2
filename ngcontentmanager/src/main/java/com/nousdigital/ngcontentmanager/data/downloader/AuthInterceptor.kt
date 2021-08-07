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

        if(!chain.request().url.toUrl().toString().contains("content/sync/inhouse_v2_0.db")){
            //Add jwt for api
            request.header("Authorization", "Bearer $jwtApi")

            //Add jwt for sync server if applicable (only for inhouse not null)
            if(jwtSyncServer !=null){
                request.header("Authorization", "Bearer $jwtSyncServer")
            }
        }

        return chain.proceed(request.build())
    }

}