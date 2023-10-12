package mapan.prototype.mapanbacakomik.AsyncTask

import android.os.AsyncTask
import mapan.prototype.mapanbacakomik.model.ComicChapter
import mapan.prototype.mapanbacakomik.model.DetailComic
import mapan.prototype.mapanbacakomik.util.Log
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URL

/***
 * Created By Mohammad Toriq on 19/02/2023
 */
class ComicDetailAsyncTask : AsyncTask<String, Void, DetailComic>() {

    override fun onPostExecute(result: DetailComic) {
        super.onPostExecute(result)
    }

    override fun doInBackground(vararg url: String): DetailComic{
//        Log.d("OkCheck", url[0])
//        var doc = Jsoup.connect(url[0]).get()
//        var doc = Jsoup.parse(URL(url[0]).openStream(),  "ISO-8859-1", url[0])
        var doc = Jsoup.parse(URL(url[0]).openStream(),  "UTF-8", url[0])
        var result = DetailComic()
        if(url[1].equals("0")){
            result = parsingKomikcast(doc)
        }else if(url[1].equals("1")){
            result = parsingWestmanga(doc)
        }else if(url[1].equals("2")){
            result = parsingNgomik(doc)
        }else if(url[1].equals("3")){
            result = parsingShinigamiId(doc)
        }
        return result
    }

    fun parsingKomikcast(doc:Document) : DetailComic{
        var listChap = ArrayList<ComicChapter>()

        val content = doc.select(".komik_info-content")
        val thumbnails = doc.select(".komik_info-content-thumbnail")
        val contentBody = doc.select(".komik_info-content-body")
        val contentGenre = doc.select(".komik_info-content-genre")
        val contentMeta = doc.select(".komik_info-content-meta")
        val contentChapter = doc.select(".komik_info-chapters-item")
        Log.d("OkCheck",content.text())
        var imgThumb = ""
        if(thumbnails.size > 0){
            var img = thumbnails[0].getElementsByTag("img")
            if(img.size>0){
                imgThumb = img[0].attr("src")
            }
            if(imgThumb.equals("")){
                var metas = thumbnails[0].getElementsByTag("meta")
                for(meta in metas){
                    if(meta.attr("itemprop").equals("url")){
                        imgThumb = meta.attr("content")
                    }
                }
            }
        }

        var title = contentBody[0].getElementsByTag("h1")[0].text()
        var genreTags = contentGenre[0].getElementsByTag("a")
        var genre = ""
        for(i in 0 .. genreTags.size-1){
            genre += genreTags[i].text()
            if(i < genreTags.size-1){
                genre += ", "
            }
        }
        var release = contentMeta[0].getElementsByClass("komik_info-content-info-release")[0].text()
        var type = contentMeta[0].getElementsByClass("komik_info-content-info-type")[0].text()

        var i = 1
        for(chapter in contentChapter){
            var name = chapter.getElementsByTag("a").text()
            var url = chapter.getElementsByTag("a").attr("href")
            var time = chapter.getElementsByClass("chapter-link-time").text()

            var ch = ComicChapter()
            ch.id = i.toLong()
            ch.name = name
            ch.time = time
            ch.url = url
            listChap.add(ch)
            i++
        }
        var chPage = DetailComic()
        chPage.title = title
        chPage.genre = genre
        chPage.type = type
        chPage.release = release
        chPage.imgSrc = imgThumb
        chPage.list = listChap
        return chPage
    }

    fun parsingWestmanga(doc:Document) : DetailComic{
        var listChap = ArrayList<ComicChapter>()

        val thumbnails = doc.select(".seriestucontl")
        val contentBody = doc.select(".seriestucon")
        val contentGenre = doc.select(".seriestugenre")
        val contentMeta = doc.select(".infotable")
        val contentChapter = doc.select(".eplister")

        var imgThumb = thumbnails[0].getElementsByTag("img")[0].attr("src")
        var title = contentBody[0].getElementsByTag("h1")[0].text()
        var genreTags = contentGenre[0].getElementsByTag("a")
        var genre = ""
        for(i in 0 .. genreTags.size-1){
            genre += genreTags[i].text()
            if(i < genreTags.size-1){
                genre += ", "
            }
        }
        var trs = contentMeta[0].getElementsByTag("tr")
        var type = ""
        var release = ""
        for(tr in trs){
            if(tr.text().contains("Type",true)){
                type = tr.text().replace("Type","Type:")
            }
            if(tr.text().contains("Author",true)){
                release = tr.text().replace("Author","Author:")
            }
        }
        var chapters = contentChapter[0].getElementsByTag("li")
        var i = 1
        for(chapter in chapters){
            var name = chapter.getElementsByClass("chapternum").text()
            var url = chapter.getElementsByTag("a").attr("href")
            var time = chapter.getElementsByClass("chapterdate").text()

            var ch = ComicChapter()
            ch.id = i.toLong()
            ch.name = name
            ch.time = time
            ch.url = url
            listChap.add(ch)
            i++
        }
        var chPage = DetailComic()
        chPage.title = title
        chPage.genre = genre
        chPage.type = type
        chPage.release = release
        chPage.imgSrc = imgThumb
        chPage.list = listChap
        return chPage
    }

    fun parsingNgomik(doc:Document) : DetailComic{
        var listChap = ArrayList<ComicChapter>()

        val thumbnails = doc.select(".seriestucontl")
        val contentBody = doc.select(".seriestucon")
        val contentGenre = doc.select(".seriestugenre")
        val contentMeta = doc.select(".infotable")
        val contentChapter = doc.select(".eplister")

        var imgThumb = thumbnails[0].getElementsByTag("img")[0].attr("src")
        var title = contentBody[0].getElementsByTag("h1")[0].text()
        var genreTags = contentGenre[0].getElementsByTag("a")
        var genre = ""
        for(i in 0 .. genreTags.size-1){
            genre += genreTags[i].text()
            if(i < genreTags.size-1){
                genre += ", "
            }
        }
        var trs = contentMeta[0].getElementsByTag("tr")
        var type = ""
        var release = ""
        for(tr in trs){
            if(tr.text().contains("Type",true)){
                type = tr.text().replace("Type","Type:")
            }
            if(tr.text().contains("Author",true)){
                release = tr.text().replace("Author","Author:")
            }
        }
        var chapters = contentChapter[0].getElementsByTag("li")
        var i = 1
        for(chapter in chapters){
            var name = chapter.getElementsByClass("chapternum").text()
            var url = chapter.getElementsByTag("a").attr("href")
            var time = chapter.getElementsByClass("chapterdate").text()

            var ch = ComicChapter()
            ch.id = i.toLong()
            ch.name = name
            ch.time = time
            ch.url = url
            listChap.add(ch)
            i++
        }
        var chPage = DetailComic()
        chPage.title = title
        chPage.genre = genre
        chPage.type = type
        chPage.release = release
        chPage.imgSrc = imgThumb
        chPage.list = listChap
        return chPage
    }

    fun parsingShinigamiId(doc:Document) : DetailComic{
        var listChap = ArrayList<ComicChapter>()

        val postTitle  = doc.select(".post-title ")
        val tabSummary  = doc.select(".tab-summary ")
        val summaryContentWrap  = doc.select(".summary_content_wrap ")

        val versionChap = doc.select(".version-chap")

        var summaryImage = doc.select(".summary_image")
        var imgThumb = ""
//        if(summaryImage.size > 0){
            imgThumb = summaryImage[0].getElementsByTag("img")[0].attr("data-src")
//        }else{
//            postTitle
//        }
        var title = postTitle[0].getElementsByTag("h1")[0].text()

        var genre = ""
        var type = ""
        var release = ""
        var postContentItem = summaryContentWrap[0].getElementsByClass("post-content_item")
        for(post in postContentItem){
            var summaryHeading = post.getElementsByClass("summary-heading")[0].getElementsByTag("h5").text()
            var summaryContent = post.getElementsByClass("summary-content")[0]
            if(summaryHeading.contains("genre",true)){
                var genreTags = summaryContent.getElementsByTag("a")
                for(i in 0 .. genreTags.size-1){
                    genre += genreTags[i].text()
                    if(i < genreTags.size-1){
                        genre += ", "
                    }
                }
            }

            if(summaryHeading.contains("type",true)){
                type = summaryContent.text()
            }

            if(summaryHeading.contains("release",true)){
                release = summaryContent.text()
            }
        }

        var chapters = versionChap[0].getElementsByClass("wp-manga-chapter")
        var i = 1
        for(chapter in chapters){
            var chapterLink = chapter.getElementsByClass("chapter-link")[0]
            var name = chapterLink.getElementsByTag("a")[0].getElementsByTag("p").text()
            var url = chapterLink.getElementsByTag("a")[0].attr("href")
            var time = chapterLink.getElementsByTag("a")[0].getElementsByClass("chapter-release-date").text()

            var ch = ComicChapter()
            ch.id = i.toLong()
            ch.name = name
            ch.time = time
            ch.url = url
            listChap.add(ch)
            i++
        }
        var chPage = DetailComic()
        chPage.title = title
        chPage.genre = genre
        chPage.type = type
        chPage.release = release
        chPage.imgSrc = imgThumb
        chPage.list = listChap
        return chPage
    }
}