package mapan.prototype.mapanbacakomik.AsyncTask

import android.os.AsyncTask
import mapan.prototype.mapanbacakomik.model.ComicThumbnail
import mapan.prototype.mapanbacakomik.model.FilterComic
import mapan.prototype.mapanbacakomik.model.HomeComic
import mapan.prototype.mapanbacakomik.model.ListComic
import mapan.prototype.mapanbacakomik.util.Log
import mapan.prototype.mapanbacakomik.util.Util
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import java.net.URL

/***
 * Created By Mohammad Toriq on 19/02/2023
 */
class HomeComicAsyncTask : AsyncTask<String, Void, HomeComic>() {

    override fun onPostExecute(result: HomeComic) {
        super.onPostExecute(result)
    }

    override fun doInBackground(vararg url: String): HomeComic{
        var result = HomeComic()
//        Log.d("OkCheck","HomeComicAsyncTask doInBackground:"+url[1])
        try{
//            var doc = Jsoup.parse(URL(url[0]).openStream(),  "ISO-8859-1", url[0])
            var doc = Jsoup.parse(URL(url[0]).openStream(),  "UTF-8", url[0])
            if(url[1].equals("0")){
                result = parsingKomikcast(doc)
            }else if(url[1].equals("1")){
                result = parsingWestmanga(doc)
            }else if(url[1].equals("2")){
                result = parsingNgomik(doc)
            }else if(url[1].equals("3")){
//                Log.d("OkCheck","parsingShinigamiId")
                result = parsingShinigamiId(doc)
            }
        }
        catch (e:Exception){
            var homeComic = HomeComic()
            var listPopularThumb = ArrayList<ComicThumbnail>()
            var listThumb = ArrayList<ComicThumbnail>()
            homeComic.popular = listPopularThumb
            homeComic.list = listThumb

            result = homeComic
        }
        return result
    }

    fun parsingKomikcast(doc: Document): HomeComic{
        doc.charset()
        var listThumb = ArrayList<ComicThumbnail>()
        var listThumbPopular = ArrayList<ComicThumbnail>()
        val listupds = doc.select(".listupd")
        Log.d("OkSize", "listupd "+listupds.size.toString())
        var listUpdatePopular = listupds[0].getElementsByClass("swiper-slide")
        Log.d("OkSize", listUpdatePopular.size.toString())
        for(utao in listUpdatePopular){
//            var slide_image = utao.getElementsByClass("splide__slide-image")
//            Log.d("OkSize", "slide-image "+slide_image.size.toString())
            var slideImage = utao.getElementsByClass("splide__slide-image")[0]
            var span = slideImage.getElementsByTag("span")
            var type = ""
            if(span.size > 0){
                for(it in span){
//                    Log.d("OkCheck","class:"+it.className())
                    if(it.className().contains("type")){
                        type = it.text()
                        break
                    }
                }
            }
//            Log.d("OkCheck","Type:"+type)
            var imgSrc = slideImage.getElementsByTag("img")[0].attr("src")
            if(imgSrc.equals("")){
                var noScript = slideImage.getElementsByTag("noscript")[0]
                imgSrc = noScript.getElementsByTag("img")[0].attr("src")
            }
            var info = utao.getElementsByClass("splide__slide-info")[0]
            var title = info.getElementsByClass("title")[0].text()
//            Log.d("OkCon", title)
            var other = info.getElementsByClass("other")[0]
            var chapterClass = other.getElementsByClass("chapter")[0]
            var chapter = chapterClass.text()
//            Log.d("OkCon", chapter)
            var chapterLink = chapterClass.attr("href")
//            Log.d("OkCon", chapterLink)
            var url = utao.getElementsByTag("a")[0].attr("href")
//            Log.d("OkCon", url)

            var com = ComicThumbnail()
            com.id = listThumbPopular.size+1.toLong()
            com.title = title
            com.url = url
            com.type = type
            com.imgSrc = imgSrc
            com.lastChap = chapter
            com.urlLastChap = chapterLink
            listThumbPopular.add(com)
        }
        var listUpdateItem = listupds[2].getElementsByClass("utao")
//        Log.d("OkCon", "listupds[0] "+listupds[0].children().size)
//        Log.d("OkCon", "listupds[1] "+listupds[1].children().size)
//        Log.d("OkCon", "listupds[2] "+listupds[2].children().size)
//        Log.d("OkCon", "listUpdateItem "+listUpdateItem.size.toString())
        for(utao in listUpdateItem){
            var uta = utao.getElementsByClass("uta")[0]
            var imgSrc = uta.getElementsByClass("data-tooltip")[0].getElementsByTag("img")[0].attr("src")
            if(imgSrc.equals("")){
                var noScript = uta.getElementsByClass("data-tooltip")[0].getElementsByTag("noscript")[0]
                imgSrc = noScript.getElementsByTag("img")[0].attr("src")
            }
//            Log.d("OkCon", imgSrc)
            var luf = uta.getElementsByClass("luf")[0]
            var ul = uta.getElementsByClass("luf")[0].getElementsByTag("ul")
            var type = ul[0].className()
//            Log.d("OkCheck","Type:"+type)
            var title = luf.getElementsByTag("h3")[0].text()
//            Log.d("OkCon", title)
            var manga = luf.getElementsByTag("ul")[0].getElementsByTag("li")
            var chapter = ""
            var chapterLink = ""
            var url = ""
            if(manga.size > 0){
                var aLink = manga[0].getElementsByTag("a")[0]
                chapter = aLink.text()
//            Log.d("OkCon", chapter)
                chapterLink = aLink.attr("href")
//            Log.d("OkCon", chapterLink)
                url = uta.getElementsByClass("data-tooltip")[0].getElementsByTag("a")[0].attr("href")
//            Log.d("OkCon", url)
            }

            var com = ComicThumbnail()
            com.id = listThumb.size+1.toLong()
            com.title = title
            com.url = url
            com.type = type
            com.imgSrc = imgSrc
            com.lastChap = chapter
            com.urlLastChap = chapterLink
            listThumb.add(com)
        }

        var homePage = HomeComic()
        homePage.popular = listThumbPopular
        homePage.list = listThumb
        return homePage
    }

    fun parsingWestmanga(doc: Document): HomeComic{
        var listThumb = ArrayList<ComicThumbnail>()
        var listThumbPopular = ArrayList<ComicThumbnail>()
        val listupds = doc.select(".listupd")
        var listUpdateItemProject = listupds[0].getElementsByClass("bs")
        Log.d("OkSize",listUpdateItemProject.size.toString() )
        var i = 0
        for(uta in listUpdateItemProject){
            var links = uta.getElementsByTag("a")
            var limit = links[0].getElementsByClass("limit")
            var type = ""
            if(limit.size > 0){
                var span = limit[0].getElementsByTag("span")
                if(span.size > 0){
                    for(it in span){
                        Log.d("OkCheck","class:"+it.className())
                        if(it.className().contains("type")){
                            type = it.className().replace("type ","")
                            break
                        }
                    }
                }
            }
            Log.d("OkCheck","Type:"+type)
            var imgSrc = links[0].getElementsByTag("img")[0].attr("src")
            var title = links[0].attr("title")
//            var chapter = links[0].getElementsByClass("bigor")[0].getElementsByClass("epxs")[0].text()
//            var bigor = links[0].getElementsByClass("bigor")
//            var adds = bigor[0].getElementsByClass("adds")
            var chapter = uta.getElementsByClass("epxs")[0].text()
            var url = links[0].attr("href")

            var com = ComicThumbnail()
            com.id = (listThumbPopular.size+1).toLong()
            com.title = title
            com.url = url
            com.type = type
            com.imgSrc = imgSrc
            com.lastChap = chapter
            com.urlLastChap = ""
            listThumbPopular.add(com)
            i++
        }
        var listUpdateItem = listupds[2].getElementsByClass("bs")
        Log.d("OkSize",listUpdateItemProject.size.toString() )
        var j = 0
        for(uta in listUpdateItem){
            var links = uta.getElementsByTag("a")
            var limit = links[0].getElementsByClass("limit")
            var type = ""
            if(limit.size > 0){
                var span = limit[0].getElementsByTag("span")
                if(span.size > 0){
                    for(it in span){
                        Log.d("OkCheck","class:"+it.className())
                        if(it.className().contains("type")){
                            type = it.className().replace("type ","")
                            break
                        }
                    }
                }
            }
            var imgSrc = links[0].getElementsByTag("img")[0].attr("src")
            var title = links[0].attr("title")
//            var chapter = links[0].getElementsByClass("bigor")[0].getElementsByClass("epxs")[0].text()
//            var bigor = links[0].getElementsByClass("bigor")
//            var adds = bigor[0].getElementsByClass("adds")
            var chapter = uta.getElementsByClass("epxs")[0].text()
            var url = links[0].attr("href")

            var com = ComicThumbnail()
            com.id = (listThumb.size+1).toLong()
            com.title = title
            com.url = url
            com.type = type
            com.imgSrc = imgSrc
            com.lastChap = chapter
            com.urlLastChap = ""
            listThumb.add(com)
            j++
        }

        var homePage = HomeComic()
        homePage.popular = listThumbPopular
        homePage.list = listThumb
        return homePage
    }

    fun parsingNgomik(doc: Document): HomeComic{
        var listThumb = ArrayList<ComicThumbnail>()
        var listThumbPopular = ArrayList<ComicThumbnail>()
        val listupds = doc.select(".listupd")
        var listUpdateItemProject = listupds[0].getElementsByClass("utao")
//        Log.d("OkSize",listUpdateItemProject.size.toString() )
        var i = 0
        for(uta in listUpdateItemProject){
            var links = uta.getElementsByTag("a")
            var imgSrc = links[0].getElementsByTag("img")[0].attr("src")
//            Log.d("OkCon", imgSrc)
            var title = links[0].attr("title")
//            Log.d("OkCon", title)
//            var chapter = links[0].getElementsByClass("bigor")[0].getElementsByClass("epxs")[0].text()
//            var bigor = links[0].getElementsByClass("bigor")
//            var adds = bigor[0].getElementsByClass("adds")
            var luf = uta.getElementsByClass("luf")[0]
            var type = ""
            var ul = luf.getElementsByTag("ul")
            if(ul.size > 0){
                type = ul[0].className()
            }
            Log.d("OkCheck","Type:"+type)
            var manga = luf.getElementsByTag("ul")[0].getElementsByTag("li")
            var chapter = manga[0].getElementsByTag("a").text()
//            var chapter = uta.getElementsByClass("epxs")[0].text()
//            Log.d("OkCon", chapter)
            var url = links[0].attr("href")
            var urlLastChap = manga[0].getElementsByTag("a").attr("href")
//            Log.d("OkCon", url)

            var com = ComicThumbnail()
            com.id = (listThumbPopular.size+1).toLong()
            com.title = title
            com.url = url
            com.type = type
            com.imgSrc = imgSrc
            com.lastChap = chapter
            com.urlLastChap = urlLastChap
            listThumbPopular.add(com)
            i++
        }
        var listUpdateItem = listupds[1].getElementsByClass("utao")
        Log.d("OkSize",listUpdateItem.size.toString() )
        var j = 0
        for(uta in listUpdateItem){
            var links = uta.getElementsByTag("a")
            var imgSrc = links[0].getElementsByTag("img")[0].attr("src")
//            Log.d("OkCon",imgSrc )
            var title = links[0].attr("title")
//            Log.d("OkCon",title )
//            var chapter = links[0].getElementsByClass("bigor")[0].getElementsByClass("epxs")[0].text()
//            var bigor = links[0].getElementsByClass("bigor")
            var luf = uta.getElementsByClass("luf")[0]
            var type = ""
            var ul = luf.getElementsByTag("ul")
            if(ul.size > 0){
                type = ul[0].className()
            }
            var manga = luf.getElementsByTag("ul")[0].getElementsByTag("li")
            var chapter = manga[0].getElementsByTag("a").text()
//            Log.d("OkCon",chapter )
            var url = links[0].attr("href")
//            Log.d("OkCon",url )
            var urlLastChap = manga[0].getElementsByTag("a").attr("href")
//            Log.d("OkCon",urlLastChap )

            var com = ComicThumbnail()
            com.id = (listThumb.size+1).toLong()
            com.title = title
            com.url = url
            com.type = type
            com.imgSrc = imgSrc
            com.lastChap = chapter
            com.urlLastChap = urlLastChap
            listThumb.add(com)
            j++
        }

        var homePage = HomeComic()
        homePage.popular = listThumbPopular
        homePage.list = listThumb
        return homePage
    }

    fun parsingShinigamiId(doc: Document): HomeComic{
        var listThumb = ArrayList<ComicThumbnail>()
        var listThumbPopular = ArrayList<ComicThumbnail>()
        val listupds = doc.select(".d-flex")
        Log.d("OkCheck",listupds.size.toString())
        Log.d("OkCheck",listupds[0].children().size.toString())

        var hotList = listupds[1].getElementsByClass("col-6 col-sm-6 col-md-4 col-xl-2")
        Log.d("OkCheck",listupds[1].children().size.toString())
        for(hot in hotList){
            var seriesLink = hot.getElementsByClass("series-card")[0].getElementsByClass("series-link")
            var imgSrc = seriesLink[0].getElementsByTag("img").attr("src")
            Log.d("OkCon", imgSrc)
            var seriesBox = hot.getElementsByClass("series-box")
            var title = seriesBox[0].getElementsByTag("h5").text()
//            Log.d("OkCon", title)
            var url = seriesBox[0].getElementsByTag("a").attr("href")
//            Log.d("OkCon", url)
            var seriesContent = hot.getElementsByClass("series-content")
            var urlLastChap = seriesContent[0].getElementsByTag("a").attr("href")
//            Log.d("OkCon", urlLastChap)
            var chapter = seriesContent[0].getElementsByTag("a")[0].getElementsByClass("series-badge")[0].text()
//            Log.d("OkCon", chapter)

            var com = ComicThumbnail()
            com.id = (listThumbPopular.size+1).toLong()
            com.title = title
            com.url = url
            com.imgSrc = imgSrc
            com.lastChap = chapter
            com.urlLastChap = urlLastChap
            listThumbPopular.add(com)
        }

        var newList = listupds[3].getElementsByClass("col-6 col-sm-6 col-md-6 col-xl-3")
        Log.d("OkCheck",listupds[3].children().size.toString())
        for(new in newList){
            var seriesLink = new.getElementsByClass("series-card")[0].getElementsByClass("series-link")
            var imgSrc = seriesLink[0].getElementsByTag("img").attr("src")
            Log.d("OkCon", imgSrc)
            var seriesBox = new.getElementsByClass("series-box")
            var title = seriesBox[0].getElementsByTag("h5").text()
//            Log.d("OkCon", title)
            var url = seriesBox[0].getElementsByTag("a").attr("href")
//            Log.d("OkCon", url)
            var seriesContent = new.getElementsByClass("series-content")
            var urlLastChap = seriesContent[0].getElementsByTag("a").attr("href")
//            Log.d("OkCon", urlLastChap)
            var chapter = seriesContent[0].getElementsByTag("a")[0].getElementsByClass("series-badge")[0].text()
//            Log.d("OkCon", chapter)

            var com = ComicThumbnail()
            com.id = (listThumbPopular.size+1).toLong()
            com.title = title
            com.url = url
            com.imgSrc = imgSrc
            com.lastChap = chapter
            com.urlLastChap = urlLastChap
            listThumb.add(com)
        }

        var homePage = HomeComic()
        homePage.popular = listThumbPopular
        homePage.list = listThumb
        return homePage
    }
}