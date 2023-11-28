package mapan.prototype.mapanbacakomik.model.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/***
 * Created By Mohammad Toriq on 28/11/2023
 */
class Detail : Serializable{
    @SerializedName("name")
    var name : String?= null

    @SerializedName("value")
    var value : String?= null
}