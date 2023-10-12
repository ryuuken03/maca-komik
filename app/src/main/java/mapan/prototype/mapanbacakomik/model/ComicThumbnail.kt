package mapan.prototype.mapanbacakomik.model

import java.io.Serializable

open class ComicThumbnail: Serializable{

    var id: Long?= null

    var url: String?= null

    var type: String?= null

    var imgSrc: String?= null

    var title: String?= null

    var lastChap: String?= null

    var urlLastChap: String?= null
}