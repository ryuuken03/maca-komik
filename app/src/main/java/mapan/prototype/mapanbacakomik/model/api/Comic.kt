package mapan.prototype.mapanbacakomik.model.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/***
 * Created By Mohammad Toriq on 28/11/2023
 */
class Comic : Serializable{
    @SerializedName("title")
    var title : String?= null

    @SerializedName("url")
    var url : String?= null

    @SerializedName("cover")
    var cover : String?= null

    @SerializedName("latestChapter")
    var latestChapter : String?= null

    @SerializedName("latestChapterUrl")
    var latestChapterUrl : String?= null

    @SerializedName("rating")
    var rating : Double?= null
}