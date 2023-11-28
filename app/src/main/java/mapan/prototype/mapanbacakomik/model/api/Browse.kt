package mapan.prototype.mapanbacakomik.model.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/***
 * Created By Mohammad Toriq on 28/11/2023
 */
class Browse : Serializable{
    @SerializedName("hotList")
    var hotList : ArrayList<Comic>?= null

    @SerializedName("newsList")
    var newsList : ArrayList<Comic>?= null

    @SerializedName("trendingList")
    var trendingList : ArrayList<Comic>?= null
}