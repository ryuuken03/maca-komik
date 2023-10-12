package mapan.prototype.mapanbacakomik.api

import io.reactivex.Observable
import io.realm.RealmList
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface APIHelper {
    @POST("admin-ajax.php")
    fun loadMore(
        @Header("content-type") contentType : String,
        @Body body: RequestBody
    ): Call<ResponseBody>

}