package mahmoud.nousir.contentmanagertest2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.nousdigital.ngcontentmanager.NGContentManager
import com.nousdigital.ngcontentmanager.data.db.NGDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        NGContentManager.init(
                NGContentManager.Configuration
                        .builder()
                        .apiUri(BuildConfig.API_URL)
                        .contentUri(BuildConfig.CDN_URL)
                        .syncServerUri(BuildConfig.SYNC_SERVER_BASE_URL)
                        .context(this)
                        .language( Locale.getDefault().language
                        )
                        .build()
        )

        val boolean  = NGDatabase.exists(this)


//        NGContentManager.instance().downloadDatabaseAndFiles(true)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnComplete {
//                    Log.i("mahmoud01010","completed")
//                }
//                .doOnError { th ->
//                    Log.i("mahmoud01010", th.message.toString())
//                }
//                .doOnNext{ percent ->
//                    Log.i("mahmoud01010", percent.toString())
//                }
//                .doAfterTerminate(NGContentManager.instance().cleanUpAfterDownloadingFiles()).subscribe()



        NGContentManager.instance().downloadDatabase(true)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                Log.i("mahmoud01010","completed")
                val x =  NGContentManager.instance().getTour(470989790)
                x.subscribe({ t ->
                    if (t != null) {
                        Log.i("mahmoud01010","getTourError: tour is here")

                    }
                    Log.i("mahmoud01010","getTourError: tour equal null")

                }, {
                    Log.i("mahmoud01010","getTourError: ${it.message.toString()}")
                })
            }
            .doOnError {
                Log.i("mahmoud0101", it.message.toString())
            }
            .doOnNext{ percent ->
                Log.i("mahmoud0101", percent.toString())
            }
            .doAfterTerminate(NGContentManager.instance().cleanUpAfterDownloadingFiles()).subscribe()


//        NGContentManager.instance().downloadDatabase(false).subscribe {
//            val contentManagerInstance = NGContentManager.instance()
//            Log.i("mahmoud0101", it.toString())
//            if (it == 100) {
//
//            }
//        }
    }
}