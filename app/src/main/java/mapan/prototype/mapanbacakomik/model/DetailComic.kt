package mapan.prototype.mapanbacakomik.model

import java.io.Serializable

open class DetailComic: Serializable{

    var title: String?= null

    var type: String?= null

    var imgSrc: String?= null

    var release: String?= null

    var genre: String?= null

    var list: ArrayList<ComicChapter>?= null
}