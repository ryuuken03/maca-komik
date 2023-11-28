package mapan.prototype.mapanbacakomik.api

import io.reactivex.Observable
import io.realm.RealmList
import mapan.prototype.mapanbacakomik.model.api.Browse
import mapan.prototype.mapanbacakomik.model.api.Comic
import mapan.prototype.mapanbacakomik.model.api.DetailComic
import mapan.prototype.mapanbacakomik.model.api.ImageList
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

    @GET("v1/browse")
    fun home(
    ): Call<Browse>

    @GET("v1/filter/latest")
    fun loadComicList(
        @Query("page") page : Int,
    ): Call<ArrayList<Comic>>

    @GET("v1/search")
    fun loadComicListSearch(
        @Query("keyword") keyword : String,
        @Query("page") page : Int,
    ): Call<ArrayList<Comic>>

    @GET("v1/projects")
    fun loadComicListProject(
        @Query("page") page : Int,
    ): Call<ArrayList<Comic>>

    @GET("v1/comic")
    fun loadComicDetail(
        @Query("url") url : String,
    ): Call<DetailComic>

    @GET("v2/chapter")
    fun loadComicChapter(
        @Query("url") url : String,
        @Query("id") id : Int = 0,
    ): Call<ImageList>

}