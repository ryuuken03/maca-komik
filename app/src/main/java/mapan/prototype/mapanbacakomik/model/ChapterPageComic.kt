package mapan.prototype.mapanbacakomik.model

import java.io.Serializable

open class ChapterPageComic: Serializable{

    var title: String?= null

    var currentChap: String?= null

    var urlAllChapter: String?= null

    var pagePrev: String?= null

    var pageNext: String?= null

    var pageAll: String?= null

    var list: ArrayList<ComicChapterPage>?= null
}