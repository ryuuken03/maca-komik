package mapan.prototype.mapanbacakomik.model

import java.io.Serializable

open class HomeComic: Serializable{

    var popular: ArrayList<ComicThumbnail>?= null

    var list: ArrayList<ComicThumbnail>?= null
}