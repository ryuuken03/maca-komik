package mapan.prototype.mapanbacakomik.AsyncTask

import android.os.AsyncTask
import mapan.prototype.mapanbacakomik.model.ChapterPageComic
import mapan.prototype.mapanbacakomik.model.ComicChapterPage
import mapan.prototype.mapanbacakomik.model.DetailComic
import mapan.prototype.mapanbacakomik.util.Log
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.net.URL

/***
 * Created By Mohammad Toriq on 19/02/2023
 */
class ListChapterPageAsyncTask : AsyncTask<String, Void, ChapterPageComic>() {

    override fun onPostExecute(result: ChapterPageComic) {
        super.onPostExecute(result)
    }

    override fun doInBackground(vararg url: String): ChapterPageComic{
        Log.d("OkCheck", url[0])
//        Log.d("OkCheck", url[1])
//        var doc = Jsoup.connect(url[0]).get()
//        var doc = Jsoup.parse(URL(url[0]).openStream(),  "ISO-8859-1", url[0])
        var doc = Jsoup.parse(URL(url[0]).openStream(),  "UTF-8", url[0])
        var result = ChapterPageComic()
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
    fun parsingKomikcast(doc: Document) : ChapterPageComic{
        var listThumb = ArrayList<ComicChapterPage>()
        val readingArea = doc.select(".main-reading-area")
        var listImage = readingArea[0].getElementsByTag("img")
        var i = 1
        for(image in listImage){
            var src = image.attr("src")
//            Log.d("OkCheck",src)
            if(!src.equals("")){
                var w = image.attr("width")
                var h = image.attr("height")
//                Log.d("OkCheck", i.toString()+":"+src)

                var com = ComicChapterPage()
                com.id = i.toLong()
                com.w = w
                com.h = h
                com.imgSrc = src
                listThumb.add(com)
                i++
            }
        }
        var nav = doc.select(".chapter_nav-control")
        var right = nav[0].getElementsByClass("right-control")
        var rels = right[0].getElementsByTag("a")
        var prev = ""
        var next = ""
        for(data in rels){
            var rel = data.attr("rel")
            if(rel.equals("prev",true)){
                prev = data.attr("href")
            }
            if(rel.equals("next",true)){
                next = data.attr("href")
            }
        }
        var allc = doc.select(".allc")[0].getElementsByTag("a")
        var all = allc.attr("href")
//        var title = allc.text()
        var title = doc.select(".chapter_headpost")[0].getElementsByTag("h1").text()
        var chap = ""
        if(title.contains("Chapter")){
            var split = title.split(" Chapter ")
            if(split.size > 1){
                title = split[0]
                chap = split[1].replace(" Bahasa Indonesia","")
            }
        }
        var chPage = ChapterPageComic()
        chPage.title = title
        chPage.currentChap = chap
        chPage.pagePrev = prev
        chPage.pageAll = all
        chPage.pageNext = next
        chPage.list = listThumb
        return chPage

    }

    fun parsingWestmanga(doc: Document) : ChapterPageComic{
        var listThumb = ArrayList<ComicChapterPage>()
        val scripts = doc.getElementsByTag("script")
        var listImage = ArrayList<String>()
        var prev = ""
        var next = ""
        for(script in scripts){
            for(node in script.dataNodes()){
                if(node.wholeData.contains("ts_reader.run(")){
                    var parse = node.wholeData.replace("ts_reader.run({","{")
                    parse = parse.replace("})","}")
                    var data = JSONObject(parse)
                    prev = data.getString("prevUrl")
                    next = data.getString("nextUrl")
                    var sources = data.getJSONArray("sources")
                    var images = sources.getJSONObject(0).getJSONArray("images")
                    for(i in 0 .. images.length()-1){
                        listImage.add(images[i].toString())
                    }
                }
            }
        }
        var i = 1
        for(image in listImage){
            var src = image
            if(!src.equals("")){
    //            Log.d("OkCheck", image)
    //            var src = image.attr("src")
    //            var w = image.attr("width")
    //            var h = image.attr("height")

                var com = ComicChapterPage()
                com.id = i.toLong()
                com.w = ""
                com.h = ""
                com.imgSrc = src
                listThumb.add(com)
                i++
            }
        }
//        var nextprev = doc.select(".nextprev")
//        var chBtn = nextprev[0].getElementsByTag("a")
//        for(data in chBtn){
//            if(!data.className().contains("disabled")){
//                var rel = data.attr("rel")
//                if(rel.equals("prev",true)){
//                    prev = data.attr("href")
//                }
//                if(rel.equals("next",true)){
//                    next = data.attr("href")
//                }
//                Log.d("OkLink",data.attr("href"))
//            }
//        }
        var allc = doc.select(".allc")[0].getElementsByTag("a")
        var all = allc.attr("href")
//        var title = allc.text()
        var title = doc.select(".entry-title")[0].text()
        var chap = ""
        if(title.contains("Chapter")){
            var split = title.split(" Chapter ")
            if(split.size > 1){
                title = split[0]
                chap = split[1].replace(" Bahasa Indonesia","")
            }
        }
        var chPage = ChapterPageComic()
        chPage.title = title
        chPage.currentChap = chap
        chPage.pagePrev = prev
        chPage.pageAll = all
        chPage.pageNext = next
        chPage.list = listThumb
        return chPage

    }

    fun parsingNgomik(doc: Document) : ChapterPageComic{
        var listThumb = ArrayList<ComicChapterPage>()
        val scripts = doc.getElementsByTag("script")
        var listImage = ArrayList<String>()
        var prev = ""
        var next = ""
        for(script in scripts){
            for(node in script.dataNodes()){
                if(node.wholeData.contains("ts_reader.run(")){
                    var parse = node.wholeData.replace("ts_reader.run({","{")
                    parse = parse.replace("})","}")
                    var data = JSONObject(parse)
                    prev = data.getString("prevUrl")
                    next = data.getString("nextUrl")
                    var sources = data.getJSONArray("sources")
                    var images = sources.getJSONObject(0).getJSONArray("images")
                    for(i in 0 .. images.length()-1){
                        listImage.add(images[i].toString())
                    }
                }
            }
        }
        var i = 1
        for(image in listImage){
            var src = image
            if(!src.equals("")){
    //            Log.d("OkCheck", image)
    //            var src = image.attr("src")
    //            var w = image.attr("width")
    //            var h = image.attr("height")

                var com = ComicChapterPage()
                com.id = i.toLong()
                com.w = ""
                com.h = ""
                com.imgSrc = src
                listThumb.add(com)
                i++
            }
        }
//        var nextprev = doc.select(".nextprev")
//        var chBtn = nextprev[0].getElementsByTag("a")
//        for(data in chBtn){
//            if(!data.className().contains("disabled")){
//                var rel = data.attr("rel")
//                if(rel.equals("prev",true)){
//                    prev = data.attr("href")
//                }
//                if(rel.equals("next",true)){
//                    next = data.attr("href")
//                }
//                Log.d("OkLink",data.attr("href"))
//            }
//        }
        var allc = doc.select(".allc")[0].getElementsByTag("a")
        var all = allc.attr("href")
//        var title = allc.text()
        var title = doc.select(".entry-title")[0].text()
        var chap = ""
        if(title.contains("Chapter")){
            var split = title.split(" Chapter ")
            if(split.size > 1){
                title = split[0]
                chap = split[1].replace(" Bahasa Indonesia","")
            }
        }
        var chPage = ChapterPageComic()
        chPage.title = title
        chPage.currentChap = chap
        chPage.pagePrev = prev
        chPage.pageAll = all
        chPage.pageNext = next
        chPage.list = listThumb
        return chPage

    }

    fun parsingShinigamiId(doc: Document) : ChapterPageComic{
        var listThumb = ArrayList<ComicChapterPage>()
        var prev = ""
        var next = ""
        val readingContent = doc.select(".reading-content")
        var images = readingContent[0].getElementsByClass("page-break no-gaps")

        for(image in images){
            var src = image.getElementsByTag("img")[0].attr("data-src").trim()
            if(!src.equals("")){
    //            Log.d("OkCheck", image.children().toString())
    //            Log.d("OkCheck", src)
    //            var src = image.attr("src")
    //            var w = image.attr("width")
    //            var h = image.attr("height")

                var com = ComicChapterPage()
                com.id = (listThumb.size+1).toLong()
                com.w = ""
                com.h = ""
                com.imgSrc = src
                listThumb.add(com)
            }
        }
        var entryHeader = doc.select(".c-blog-post")
        var breadcrumb = entryHeader[0].getElementsByClass("breadcrumb")[0]
        var li = breadcrumb.getElementsByTag("li")
//        var all = allc.attr("href")
//        var title = allc.text()
        var title = li[2].getElementsByTag("a")[0].text()
        var all = li[2].getElementsByTag("a")[0].attr("href")
        var chap = li[3].text().replace("Chapter","",true)

        var nav = entryHeader[0].getElementsByClass("select-pagination")[0].getElementsByClass("nav-links")
        if(nav.size > 0){
            var navPrevious = nav[0].getElementsByClass("nav-previous")
            if(navPrevious.size > 0){
                prev = navPrevious[0].getElementsByTag("a").attr("href")
            }
            var navNext = nav[0].getElementsByClass("nav-next ")
            if(navNext.size > 0){
                next = navNext[0].getElementsByTag("a").attr("href")
            }
        }


        var chPage = ChapterPageComic()
        chPage.title = title
        chPage.currentChap = chap
        chPage.pagePrev = prev
        chPage.pageAll = all
        chPage.pageNext = next
        chPage.list = listThumb
        return chPage

    }
}