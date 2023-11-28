package mapan.prototype.mapanbacakomik.model.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/***
 * Created By Mohammad Toriq on 28/11/2023
 */
class DetailComic : Serializable{
    @SerializedName("synopsis")
    var synopsis : String?= null

    @SerializedName("detailList")
    var detailList : ArrayList<Detail>?= null

    @SerializedName("chapterList")
    var chapterList : ArrayList<ChapterComic>?= null

    @SerializedName("related")
    var related : ArrayList<Comic>?= null

}