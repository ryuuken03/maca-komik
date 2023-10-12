package mapan.prototype.mapanbacakomik

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import io.realm.Realm
import io.realm.RealmConfiguration
import mapan.prototype.mapanbacakomik.util.Log

/***
 * Created By Mohammad Toriq on 26/02/2023
 */
class MyApplication: Application() {

    val SCHEMA_VERSION = 0
    var realmInstance: Realm? = null

    companion object {
        fun getInstance(context: Context): MyApplication {
            return context.applicationContext as MyApplication
        }
    }
    override fun onCreate() {
        super.onCreate()
        try {
            Realm.init(applicationContext)

            val config = RealmConfiguration.Builder()
                .schemaVersion(SCHEMA_VERSION.toLong())
                .deleteRealmIfMigrationNeeded()
                .build()

            Realm.setDefaultConfiguration(config)

            if(realmInstance == null){
                realmInstance = Realm.getInstance(config)
                Log.e("CheckEko", " => 1")
            }else{
                Log.e("CheckEko", " => 2")
            }
        }catch (e:Exception){
            Log.e("ExceptionStart", " => " + e.message)
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}