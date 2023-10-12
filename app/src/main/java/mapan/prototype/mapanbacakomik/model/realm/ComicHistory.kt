package mapan.prototype.mapanbacakomik.model.realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable

open class ComicHistory: RealmObject() {
    @PrimaryKey
    var id: Long?= null

    var title: String?= null

    var type: String?= null

    var genre: String?= null

    var imgSrc: String?= null

    var urlDetail: String?= null

    var chapter: String?= null

    var urlChapter: String?= null

    var pageChapter: Int?= null
}