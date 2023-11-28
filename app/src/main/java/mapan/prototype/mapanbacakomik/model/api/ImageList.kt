package mapan.prototype.mapanbacakomik.model.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/***
 * Created By Mohammad Toriq on 28/11/2023
 */
class ImageList : Serializable{
    @SerializedName("imageList")
    var imageList : ArrayList<String>?= null
}