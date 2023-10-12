package mapan.prototype.mapanbacakomik.model

import java.io.Serializable

open class ListComic: Serializable{

    var query: String?= null

    var isSuccessed = true

    var page1: String?= null

    var page2: String?= null

    var page2Url: String?= null

    var page3: String?= null

    var pageLast: String?= null

    var pagePrevUrl: String?= null

    var pageNextUrl: String?= null

    var list: ArrayList<ComicThumbnail>?= null

    var genres: ArrayList<FilterComic>?= null

    var types: ArrayList<FilterComic>?= null

    var orderbys: ArrayList<FilterComic>?= null
}